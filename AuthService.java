package mini_twitter.user_service.service;

import jakarta.transaction.Transactional;
import mini_twitter.user_service.dto.LoginUserRequest;
import mini_twitter.user_service.dto.TokenResponse;
import mini_twitter.user_service.entity.User;
import mini_twitter.user_service.repository.UserRepository;
import mini_twitter.user_service.security.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        validationService.validate(request);

        logger.info("Attempting to login user with username: {}", request.getUsername());

        User user = userRepository.findFirstByUsername(request.getUsername())
                .orElseThrow(() -> {
                    logger.warn("Login failed for username: {} - User not found", request.getUsername());
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong");
                });

        if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);

            logger.info("User {} logged in successfully. Token: {}", request.getUsername(), token);

            return TokenResponse.builder()
                    .token(token)
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            logger.warn("Login failed for username: {} - Incorrect password", request.getUsername());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or password wrong");
        }
    }

    private Long next30Days() {
        return System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 30);
    }

    @Transactional
    public void logout(String token) {
        logger.info("Logging out user with token: {}", token);

        User user = userRepository.findFirstByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        user.setToken(null);
        user.setTokenExpiredAt(null);

        userRepository.save(user);

        logger.info("User {} logged out successfully", user.getUsername());
    }
}
