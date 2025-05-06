package mini_twitter.follow_service.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import mini_twitter.follow_service.dto.UserResponseDto;
import mini_twitter.follow_service.dto.WebResponseDto;
import mini_twitter.follow_service.repository.FollowRepository;
import mini_twitter.follow_service.webclient.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mini_twitter.follow_service.entity.Follow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FollowService {

    private static final Logger logger = LoggerFactory.getLogger(FollowService.class);

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UserServiceClient userServiceClient; // Client to communicate with User Service

    @Transactional
    public WebResponseDto<String> followUser(String userId, String token) {
        logger.info("Received follow request. User ID to follow: {}, Token: {}", userId, token);

        // Get the ID of the follower from the token
        String followerId = userServiceClient.getUserIdFromToken(token);

        if (followerId.equals(userId)) {
            logger.warn("User ID: {} cannot follow themselves", followerId);
            throw new IllegalArgumentException("You cannot follow yourself.");
        }

        if (followRepository.existsByUserIdAndFollowerId(userId, followerId)) {
            logger.warn("Follower ID: {} is already following User ID: {}", followerId, userId);
            throw new IllegalArgumentException("You are already following this user.");
        }

        Follow follow = new Follow();
        follow.setId(UUID.randomUUID().toString());
        follow.setUserId(userId);
        follow.setFollowerId(followerId);
        followRepository.save(follow);

        logger.info("Follower ID: {} successfully followed User ID: {}", followerId, userId);
        return WebResponseDto.<String>builder().data("You are now following user-" + userId + ".").build();
    }

    @Transactional
    public WebResponseDto<String> unfollowUser(String userId, String token) {
        logger.info("Received unfollow request. User ID to unfollow: {}, Token: {}", userId, token);

        // Get the ID of the follower from the token
        String followerId = userServiceClient.getUserIdFromToken(token);

        if (!followRepository.existsByUserIdAndFollowerId(userId, followerId)) {
            logger.warn("Follower ID: {} is not following User ID: {}", followerId, userId);
            throw new IllegalArgumentException("You are not following this user.");
        }

        followRepository.deleteByUserIdAndFollowerId(userId, followerId);
        logger.info("Follower ID: {} successfully unfollowed User ID: {}", followerId, userId);
        return WebResponseDto.<String>builder().data("You have unfollowed user-" + userId + ".").build();
    }

    public WebResponseDto<List<UserResponseDto>> getFollowers(String userId) {
        logger.info("Fetching followers for User ID: {}", userId);
        List<Follow> followers = followRepository.findByUserId(userId);

        if (followers.isEmpty()) {
            logger.warn("No followers found for User ID: {}", userId);
            throw new IllegalArgumentException("No followers found for this user.");
        }

        List<UserResponseDto> followerDtos = followers.stream()
                .map(follow -> userServiceClient.getUserById(follow.getFollowerId()))
                .collect(Collectors.toList());

        logger.info("Successfully fetched followers for User ID: {}", userId);
        return WebResponseDto.<List<UserResponseDto>>builder().data(followerDtos).build();
    }

    public WebResponseDto<List<UserResponseDto>> getFollowing(String userId) {
        logger.info("Fetching following for User ID: {}", userId);
        List<Follow> following = followRepository.findByFollowerId(userId);

        if (following.isEmpty()) {
            logger.warn("This user is not following anyone. User ID: {}", userId);
            throw new IllegalArgumentException("This user is not following anyone.");
        }

        List<UserResponseDto> followingDtos = following.stream()
                .map(follow -> userServiceClient.getUserById(follow.getUserId()))
                .collect(Collectors.toList());

        logger.info("Successfully fetched following for User ID: {}", userId);
        return WebResponseDto.<List<UserResponseDto>>builder().data(followingDtos).build();
    }

}
