package mini_twitter.like_service.controller;

import mini_twitter.like_service.dto.WebResponseDto;
import mini_twitter.like_service.service.PostLikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class PostLikeController {

    private static final Logger logger = LoggerFactory.getLogger(PostLikeController.class);

    @Autowired
    private PostLikeService postLikeService;

    @PostMapping(
            path = "/api/posts/{postId}/like",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<String> likePost(@RequestHeader("X-API-TOKEN") String token,
                                           @PathVariable String postId) {
        logger.info("Request to like post ID: {} with token: {}", postId, token);

        try {
            return postLikeService.likePost(token, postId);
        } catch (Exception e) {
            logger.error("Error liking post ID: {}", postId, e);
            throw e;
        }
    }

    @DeleteMapping(
            path = "/api/posts/{postId}/like",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<String> unlikePost(@RequestHeader("X-API-TOKEN") String token,
                                             @PathVariable String postId) {
        logger.info("Request to unlike post ID: {} with token: {}", postId, token);

        try {
            return postLikeService.unlikePost(token, postId);
        } catch (Exception e) {
            logger.error("Error unliking post ID: {}", postId, e);
            throw e;
        }
    }

    @GetMapping(
            path = "/api/posts/{postId}/likes",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<Integer> getNumberOfLikesOnPost(@PathVariable String postId) {
        logger.info("Request to get number of likes for post ID: {}", postId);

        try {
            return postLikeService.getNumberOfLikesOnPost(postId);
        } catch (Exception e) {
            logger.error("Error getting number of likes for post ID: {}", postId, e);
            throw e;
        }
    }

}