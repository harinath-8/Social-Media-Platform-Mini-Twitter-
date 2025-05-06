package mini_twitter.like_service.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import mini_twitter.like_service.dto.WebResponseDto;
import mini_twitter.like_service.entity.CommentLike;
import mini_twitter.like_service.repository.CommentLikeRepository;
import mini_twitter.like_service.webclient.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CommentLikeService {

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Transactional
    public WebResponseDto<String> likeComment(String token, String commentId) {
        log.info("Received request to like comment ID: {} with token: {}", commentId, token);

        String userId = userServiceClient.getUserIdFromToken(token);

        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);
        if (existingLike.isPresent()) {
            log.warn("User ID: {} already liked comment ID: {}", userId, commentId);
            return WebResponseDto.<String>builder().errors("You have already liked this comment.").build();
        }

        CommentLike commentLike = new CommentLike();
        commentLike.setId(UUID.randomUUID().toString());
        commentLike.setCommentId(commentId);
        commentLike.setUserId(userId);
        commentLikeRepository.save(commentLike);

        log.info("User ID: {} successfully liked comment ID: {}", userId, commentId);
        return WebResponseDto.<String>builder().data("Comment liked successfully.").build();
    }

    @Transactional
    public WebResponseDto<String> unlikeComment(String token, String commentId) {
        log.info("Received request to unlike comment ID: {} with token: {}", commentId, token);

        String userId = userServiceClient.getUserIdFromToken(token);

        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);
        if (existingLike.isEmpty()) {
            log.warn("User ID: {} has not liked comment ID: {}", userId, commentId);
            return WebResponseDto.<String>builder().errors("You have not liked this comment.").build();
        }

        commentLikeRepository.delete(existingLike.get());

        log.info("User ID: {} successfully unliked comment ID: {}", userId, commentId);
        return WebResponseDto.<String>builder().data("Comment unliked successfully.").build();
    }

    public WebResponseDto<Integer> getNumberOfLikesOnComment(String commentId) {
        log.info("Fetching like count for comment ID: {}", commentId);
        int likeCount = commentLikeRepository.countByCommentId(commentId);
        log.info("Found {} likes for comment ID: {}", likeCount, commentId);
        return WebResponseDto.<Integer>builder().data(likeCount).build();
    }

}
