package mini_twitter.user_service.controller;

import mini_twitter.user_service.dto.LoginUserRequest;
import mini_twitter.user_service.dto.TokenResponse;
import mini_twitter.user_service.dto.WebResponse;
import mini_twitter.user_service.entity.User;
import mini_twitter.user_service.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping(
            path = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request) {
        logger.info("Received login request for username: {}", request.getUsername());

        TokenResponse tokenResponse;
        try {
            tokenResponse = authService.login(request);
            logger.info("Login successful for username: {}", request.getUsername());
        } catch (Exception e) {
            logger.error("Login failed for username: {} - {}", request.getUsername(), e.getMessage());
            throw e; 
        }

        return WebResponse.<TokenResponse>builder().data(tokenResponse).build();
    }

    @DeleteMapping(
            path = "/api/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> logout(@RequestHeader("X-API-TOKEN") String token) {
        logger.info("Received logout request with token: {}", token);

        try {
            authService.logout(token);
            logger.info("User successfully logged out");
            return WebResponse.<String>builder().data("OK").build();
        } catch (Exception e) {
            logger.error("Error logging out user with token {}: {}", token, e.getMessage());
            return WebResponse.<String>builder().data("Error").build();
        }
    }
}
