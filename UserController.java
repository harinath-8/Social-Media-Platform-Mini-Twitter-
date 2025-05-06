package mini_twitter.user_service.controller;

import mini_twitter.user_service.dto.RegisterUserRequest;
import mini_twitter.user_service.dto.UpdateUserRequest;
import mini_twitter.user_service.dto.UserResponse;
import mini_twitter.user_service.dto.WebResponse;
import mini_twitter.user_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping(
            path = "/api/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterUserRequest request) {
        logger.info("Received registration request for username: {}", request.getUsername());

        try {
            userService.register(request);
            logger.info("Successfully registered user: {}", request.getUsername());
            return WebResponse.<String>builder().data("OK").build();
        } catch (Exception e) {
            logger.error("Error registering user: {}", request.getUsername(), e);
            throw e;
        }
    }

    @PutMapping(
            path = "/api/users/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(@RequestBody UpdateUserRequest request,
                                            @RequestHeader("X-API-TOKEN") String token,
                                            @PathVariable("userId") String userId) {
        logger.info("Received update request for userId: {} with token: {}", userId, token);

        try {
            UserResponse updatedUserResponse = userService.update(request, token, userId);

            logger.info("User update successful for userId: {}", userId);
            logger.debug("Updated user details: {}", updatedUserResponse);

            return WebResponse.<UserResponse>builder().data(updatedUserResponse).build();
        } catch (Exception e) {
            logger.error("Error updating user with userId {} and token {}: {}", userId, token, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping(
            path = "/api/users/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> get(@PathVariable("userId") String userId) {
        logger.debug("Received request to get user with ID: {}", userId);

        try {
            UserResponse userResponse = userService.get(userId);
            logger.debug("Successfully retrieved user: {}", userResponse);
            return WebResponse.<UserResponse>builder().data(userResponse).build();
        } catch (ResponseStatusException e) {
            logger.error("Error retrieving user with ID: {}. Error: {}", userId, e.getMessage());
            throw e;
        }
    }

    @GetMapping(
            path = "/api/users/me",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> getUserIdByToken(@RequestHeader("X-API-TOKEN") String token) {
        logger.info("Received request to get user ID by token");

        try {
            String userId = userService.getUserIdByToken(token);
            logger.info("Successfully retrieved user ID by token: {}", userId);
            return WebResponse.<String>builder().data(userId).build(); // Return user ID
        } catch (ResponseStatusException e) {
            logger.error("Error retrieving user ID by token: {}. Error: {}", token, e.getMessage());
            throw e;
        }
    }

}
