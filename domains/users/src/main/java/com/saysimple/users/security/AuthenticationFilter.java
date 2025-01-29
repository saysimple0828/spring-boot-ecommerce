package com.saysimple.users.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saysimple.users.dto.UserDto;
import com.saysimple.users.service.UserService;
import com.saysimple.users.vo.RequestLogin;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserService userService;
    private final Environment environment;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService userService, Environment environment) {
        super(authenticationManager);
        log.info("AuthenticationFilter");
        this.userService = userService;
        this.environment = environment;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {
            log.info("attemptAuthentication");
            RequestLogin creds = new ObjectMapper().readValue(req.getInputStream(), RequestLogin.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        log.info("successfulAuthentication");
        String userName = ((User) auth.getPrincipal()).getUsername();
        UserDto userDetails = userService.getByEmail(userName);

        byte[] secretKeyBytes = Base64.getEncoder().encode(Objects.requireNonNull(environment.getProperty("token.secret")).getBytes());

        SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);

        Instant now = Instant.now();

        String token = Jwts.builder()
                .subject(userDetails.getUserId())
                .expiration(Date.from(now.plusMillis(Long.parseLong(Objects.requireNonNull(environment.getProperty("token.expiration_time"))))))
                .issuedAt(Date.from(now))
                .signWith(secretKey)
                .compact();

        res.addHeader(HttpHeaders.AUTHORIZATION, token);
        res.addHeader("userId", userDetails.getUserId());
    }
}
