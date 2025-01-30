package com.saysimple.user;

import org.saysimple.error.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserPort extends UserDetailsService {
    UserDto create(UserDto userDto) throws NotFoundException;
    UserDto get(String userId) throws NotFoundException;
    Page<UserDto> list(Pageable pageable); // 🔽 페이징 적용
    UserDto update(UserDto userDto) throws NotFoundException;
    UserDto deactivate(String userId);
    UserDto getByPhoneNumber(String userId);
}
