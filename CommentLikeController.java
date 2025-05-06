package mini_twitter.like_service.controller;

import mini_twitter.like_service.dto.WebResponseDto;
import mini_twitter.like_service.service.CommentLikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentLikeController {

    private static final Logger logger = LoggerFactory.getLogger(CommentLikeController.class);

    @Autowired
    private CommentLikeService commentLikeService;

    @PostMapping(
            path = "/api/comments/{commentId}/like",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<String> likeComment(@RequestHeader("X-API-TOKEN") String token,
                                              @PathVariable String commentId) {
        logger.info("Request to like comment ID: {} with token: {}", commentId, token);

        try {
            return commentLikeService.likeComment(token, commentId);
        } catch (Exception e) {
            logger.error("Error liking comment ID: {}", commentId, e);
            throw e;
        }
    }

    @DeleteMapping(
            path = "/api/comments/{commentId}/like",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<String> unlikeComment(@RequestHeader("X-API-TOKEN") String token,
                                                @PathVariable String commentId) {
        logger.info("Request to unlike comment ID: {} with token: {}", commentId, token);

        try {
            return commentLikeService.unlikeComment(token, commentId);
        } catch (Exception e) {
            logger.error("Error unliking comment ID: {}", commentId, e);
            throw e;
        }
    }

    @GetMapping(
            path = "/api/comments/{commentId}/likes",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<Integer> getNumberOfLikesOnComment(@PathVariable String commentId) {
        logger.info("Request to get number of likes for comment ID: {}", commentId);

        try {
            return commentLikeService.getNumberOfLikesOnComment(commentId);
        } catch (Exception e) {
            logger.error("Error getting number of likes for comment ID: {}", commentId, e);
            throw e;
        }
    }

}
