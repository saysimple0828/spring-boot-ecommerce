package com.saysimple.users.service;

import com.saysimple.users.client.CatalogServiceClient;
import com.saysimple.users.client.OrderServiceClient;
import com.saysimple.users.dto.UserDto;
import com.saysimple.users.entity.User;
import com.saysimple.users.error.exception.NotFoundException;
import com.saysimple.users.repository.UserRepository;
import com.saysimple.users.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.saysimple.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

    Environment env;
    RestTemplate restTemplate;

    OrderServiceClient orderServiceClient;
    CatalogServiceClient catalogServiceClient;

    CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           Environment env,
                           RestTemplate restTemplate,
                           OrderServiceClient orderServiceClient,
                           CatalogServiceClient catalogServiceClient,
                           CircuitBreakerFactory circuitBreakerFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.restTemplate = restTemplate;
        this.orderServiceClient = orderServiceClient;
        this.catalogServiceClient = catalogServiceClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if (user == null)
            throw new UsernameNotFoundException(username + ": not found");

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>());
    }

    @Override
    public UserDto create(UserDto userDto) throws NotFoundException {
        User user = ModelUtils.strictMap(userDto, User.class);

        user.setUserId(UUID.randomUUID().toString());
        user.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));
        user.setIsActive(true);

        try {
            return ModelUtils.strictMap(userRepository.save(user), UserDto.class);
        } catch (Exception e) {
            throw new NotFoundException("User already exists");
        }
    }

    @Override
    public UserDto get(String userId) throws NotFoundException {
        User user = userRepository.findByUserId(userId);

        log.info("UserEntity {}", user);

        if (user == null)
            throw new NotFoundException("User not found");

        UserDto userDto = new ModelMapper().map(user, UserDto.class);

        log.info("Before call orders microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker1");
        List<ResponseOrder> orders = circuitBreaker.run(() -> orderServiceClient.getOrders(userId),
                throwable -> new ArrayList<>());
        userDto.setOrders(orders);
        log.info("After called orders microservice");

        return userDto;
    }

    @Override
    public Iterable<User> list() {
        return userRepository.findAll();
    }

    @Override
    public UserDto deactivate(String userId) {
        User user = userRepository.findByUserId(userId);
        user.setIsActive(false);

        return ModelUtils.map(user, UserDto.class);
    }

    @Override
    public UserDto getByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new UsernameNotFoundException(email);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        return userDto;
    }
}
