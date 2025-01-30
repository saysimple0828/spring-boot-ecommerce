package com.saysimple.user;

import com.saysimple.client.CatalogServiceClient;
import com.saysimple.client.OrderServiceClient;
import com.saysimple.user.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.saysimple.error.ErrorCode;
import org.saysimple.error.exception.NotFoundException;
import org.saysimple.util.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class UserAdapter implements UserPort {
    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

    Environment env;
    RestTemplate restTemplate;

    OrderServiceClient orderServiceClient;
    CatalogServiceClient catalogServiceClient;

    CircuitBreakerFactory circuitBreakerFactory;

    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public UserAdapter(UserRepository userRepository,
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
        User user = userRepository.findByPhoneNumber(username).orElseThrow(() -> new UsernameNotFoundException(username));

        if (user == null)
            throw new UsernameNotFoundException(username + ": not found");

        return new org.springframework.security.core.userdetails.User(user.getPhoneNumber(), user.getEncryptedPwd(),
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
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Override
    public UserDto get(String userId) throws NotFoundException {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        log.info("UserEntity {}", user);
        UserDto userDto = ModelUtils.map(user, UserDto.class);

        log.info("Before call orders microservice");
//        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker1");
//        List<ResponseOrder> orders = circuitBreaker.run(() -> orderServiceClient.getOrders(userId),
//                throwable -> new ArrayList<>());
//        userDto.setOrders(orders);
        log.info("After called orders microservice");

        return userDto;
    }

    @Override
    public Page<UserDto> list(Pageable pageable) {
        // DB에서 페이징된 User 엔티티 조회
        Page<User> userPage = userRepository.findAll(pageable);

        // User → UserDto 변환 (Page 객체의 map 메서드 활용)
        return userPage.map(user -> modelMapper.map(user, UserDto.class));
    }

    @Override
    public UserDto update(UserDto userDto) throws NotFoundException {
        User user = userRepository.findByUserId(userDto.getUserId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        log.info("UserEntity before update: {}", user);

        ModelUtils.convert(user, userDto);
        user = userRepository.save(user);

        log.info("UserEntity after update: {}", user);

        // 업데이트된 user 엔터티를 다시 DTO로 변환

//        log.info("Before calling orders microservice");
//        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitBreaker1");
//
//        List<ResponseOrder> orders = circuitBreaker.run(
//                () -> orderServiceClient.getOrders(userDto.getUserId()),
//                throwable -> new ArrayList<>()
//        );
//
//        updatedUserDto.setOrders(orders);
//        log.info("After calling orders microservice");

        return ModelUtils.map(user, UserDto.class);
    }


    @Override
    public UserDto deactivate(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        user.setIsActive(false);

        return ModelUtils.map(user, UserDto.class);
    }

    @Override
    public UserDto getByPhoneNumber(String userId) {
        User user = userRepository.findByPhoneNumber(userId).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return ModelUtils.map(user, UserDto.class);
    }
}
