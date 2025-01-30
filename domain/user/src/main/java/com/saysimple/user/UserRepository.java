package com.saysimple.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUserId(String userId);
    Optional<User> findByPhoneNumber(String username);
    Page<User> findAll(Pageable pageable);
}
