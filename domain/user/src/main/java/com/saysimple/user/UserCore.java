package com.saysimple.user;

import com.saysimple.user.vo.RequestUser;
import com.saysimple.user.vo.ResponseUser;
import io.micrometer.core.annotation.Timed;
import org.modelmapper.ModelMapper;
import org.saysimple.error.exception.NotFoundException;
import org.saysimple.util.ModelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserCore {
    private final Environment env;
    private final UserPort userPort;

    @Autowired
    public UserCore(Environment env, UserPort userPort) {
        this.env = env;
        this.userPort = userPort;
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

    @PostMapping
    public ResponseEntity<ResponseUser> create(@RequestBody RequestUser user) throws NotFoundException {
        UserDto userDto = ModelUtils.strictMap(user, UserDto.class);

        ResponseUser responseUser = ModelUtils.strictMap(userPort.create(userDto), ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sort
    ) {
        // 정렬 조건 파싱 (예: "createdAt,desc" → Sort.by("createdAt").descending())
        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        // 서비스 호출
        Page<UserDto> userPage = userPort.list(pageable);
        return ResponseEntity.ok(userPage);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseUser> get(@PathVariable("userId") String userId) throws NotFoundException {
        UserDto userDto = userPort.get(userId);

        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }

    @GetMapping("/deactivate/{userId}")
    public ResponseEntity<ResponseUser> deactivate(@PathVariable("userId") String userId) throws NotFoundException {
        UserDto userDto = userPort.deactivate(userId);

        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }
}
