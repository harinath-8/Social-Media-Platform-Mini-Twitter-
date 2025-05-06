package mini_twitter.like_service.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import mini_twitter.like_service.dto.WebResponseDto;
import mini_twitter.like_service.entity.PostLike;
import mini_twitter.like_service.repository.PostLikeRepository;
import mini_twitter.like_service.webclient.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PostLikeService {

    private static final Logger logger = LoggerFactory.getLogger(PostLikeService.class);

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Transactional
    public WebResponseDto<String> likePost(String token, String postId) {
        logger.info("Received request to like post ID: {} with token: {}", postId, token);

        String userId = userServiceClient.getUserIdFromToken(token);

        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isPresent()) {
            logger.warn("User ID: {} already liked post ID: {}", userId, postId);
            return WebResponseDto.<String>builder().errors("You have already liked this post.").build();
        }

        PostLike postLike = new PostLike();
        postLike.setId(UUID.randomUUID().toString());
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLikeRepository.save(postLike);

        logger.info("User ID: {} successfully liked post ID: {}", userId, postId);
        return WebResponseDto.<String>builder().data("Post liked successfully.").build();
    }

    @Transactional
    public WebResponseDto<String> unlikePost(String token, String postId) {
        log.info("Received request to unlike post ID: {} with token: {}", postId, token);

        String userId = userServiceClient.getUserIdFromToken(token);

        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isEmpty()) {
            log.warn("User ID: {} has not liked post ID: {}", userId, postId);
            return WebResponseDto.<String>builder().errors("You have not liked this post.").build();
        }

        postLikeRepository.delete(existingLike.get());

        log.info("User ID: {} successfully unliked post ID: {}", userId, postId);
        return WebResponseDto.<String>builder().data("Post unliked successfully.").build();
    }

    public WebResponseDto<Integer> getNumberOfLikesOnPost(String postId) {
        log.info("Fetching like count for post ID: {}", postId);
        int likeCount = postLikeRepository.countByPostId(postId);
        log.info("Found {} likes for post ID: {}", likeCount, postId);
        return WebResponseDto.<Integer>builder().data(likeCount).build();
    }

}
