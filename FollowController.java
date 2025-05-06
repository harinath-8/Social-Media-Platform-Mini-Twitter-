package mini_twitter.follow_service.controller;

import mini_twitter.follow_service.dto.UserResponseDto;
import mini_twitter.follow_service.dto.WebResponseDto;
import mini_twitter.follow_service.service.FollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FollowController {

    private static final Logger logger = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    private FollowService followService;

    @PostMapping(
            path = "/api/users/{userId}/follow",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<String> followUser(@PathVariable String userId, @RequestHeader("X-API-TOKEN") String token) {
        logger.info("Request to follow user ID: {} with token: {}", userId, token);

        try {
            WebResponseDto<String> response = followService.followUser(userId, token);
            logger.info("Successfully followed user ID: {}", userId);
            return response;
        } catch (Exception e) {
            logger.error("Error following user ID: {}", userId, e);
            throw e;
        }
    }

    @DeleteMapping(
            path = "/api/users/{userId}/follow",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<String> unfollowUser(@PathVariable String userId, @RequestHeader("X-API-TOKEN") String token) {
        logger.info("Request to unfollow user ID: {} with token: {}", userId, token);

        try {
            WebResponseDto<String> response = followService.unfollowUser(userId, token);
            logger.info("Successfully unfollowed user ID: {}", userId);
            return response;
        } catch (Exception e) {
            logger.error("Error unfollowing user ID: {}", userId, e);
            throw e;
        }
    }

    @GetMapping(
            path = "/api/users/{userId}/followers",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<List<UserResponseDto>> getFollowers(@PathVariable String userId) {
        logger.info("Request to fetch followers for user ID: {}", userId);

        try {
            WebResponseDto<List<UserResponseDto>> response = followService.getFollowers(userId);
            logger.info("Successfully fetched followers for user ID: {}", userId);
            return response;
        } catch (Exception e) {
            logger.error("Error fetching followers for user ID: {}", userId, e);
            throw e;
        }
    }

    @GetMapping(
            path = "/api/users/{userId}/following",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<List<UserResponseDto>> getFollowing(@PathVariable String userId) {
        logger.info("Request to fetch following for user ID: {}", userId);

        try {
            WebResponseDto<List<UserResponseDto>> response = followService.getFollowing(userId);
            logger.info("Successfully fetched following for user ID: {}", userId);
            return response;
        } catch (Exception e) {
            logger.error("Error fetching following for user ID: {}", userId, e);
            throw e;
        }
    }

}
