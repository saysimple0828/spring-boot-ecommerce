package com.saysimple.users.service;

import com.saysimple.users.dto.UserDto;
import com.saysimple.users.entity.User;
import com.saysimple.users.error.exception.NotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto create(UserDto userDto) throws NotFoundException;

    UserDto get(String userId) throws NotFoundException;

    Iterable<User> list();

    UserDto deactivate(String userId);

    UserDto getByEmail(String userName);
}
