package com.saysimple.users.controller;

import com.saysimple.users.dto.UserDto;
import com.saysimple.users.entity.User;
import com.saysimple.users.error.exception.NotFoundException;
import com.saysimple.users.service.UserService;
import com.saysimple.users.vo.RequestUser;
import com.saysimple.users.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import org.modelmapper.ModelMapper;
import org.saysimple.utils.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/")
public class UserController {
    private final Environment env;
    private final UserService userService;

    @Autowired
    public UserController(Environment env, UserService userService) {
        this.env = env;
        this.userService = userService;
    }

    @GetMapping("/health-check")
    @Timed(value = "users.status", longTask = true)
    public String status() {
        return String.format("It's Working in User Service"
                + ", port(local.server.port)=" + env.getProperty("local.seßrver.port")
                + ", port(server.port)=" + env.getProperty("server.port")
                + ", gateway ip(env)=" + env.getProperty("gateway.ip")
                + ", token expiration time=" + env.getProperty("token.expiration_time")
                + ", secret=" + env.getProperty("token.secret")
        );
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> create(@RequestBody RequestUser user) throws NotFoundException {
        UserDto userDto = ModelUtils.strictMap(user, UserDto.class);

        ResponseUser responseUser = ModelUtils.strictMap(userService.create(userDto), ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> list() {
        Iterable<User> userList = userService.list();

        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseUser> get(@PathVariable("userId") String userId) throws NotFoundException {
        UserDto userDto = userService.get(userId);

        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }

    @GetMapping("/deactivate/{userId}")
    public ResponseEntity<ResponseUser> deactivate(@PathVariable("userId") String userId) throws NotFoundException {
        UserDto userDto = userService.get(userId);

        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }

    @GetMapping("/users/hateoas")
    public ResponseEntity<CollectionModel<EntityModel<ResponseUser>>> getWithHateoas() throws NotFoundException {
        List<EntityModel<ResponseUser>> result = new ArrayList<>();
        Iterable<User> users = userService.list();

        for (User user : users) {
            EntityModel entityModel = EntityModel.of(user);
            entityModel.add(linkTo(methodOn(this.getClass()).get(user.getUserId())).withSelfRel());

            result.add(entityModel);
        }

        return ResponseEntity.ok(CollectionModel.of(result, linkTo(methodOn(this.getClass()).getWithHateoas()).withSelfRel()));
    }
}
