package mini_twitter.comment_service.controller;

import mini_twitter.comment_service.dto.CommentRequestDto;
import mini_twitter.comment_service.dto.CommentResponseDto;
import mini_twitter.comment_service.dto.WebResponseDto;
import mini_twitter.comment_service.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @PostMapping(
            path = "/api/posts/{postId}/comments",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<CommentResponseDto> createComment(@RequestHeader("X-API-TOKEN") String token,
                                                            @PathVariable String postId,
                                                            @RequestBody CommentRequestDto request) {
        logger.info("Received request to create comment for postId: {}. Request Body: {}, Token: {}", postId, request, token);

        try {
            WebResponseDto<CommentResponseDto> response = commentService.createComment(token, postId, request);

            if (response.getErrors() == null || response.getErrors().isEmpty()) {
                logger.info("Comment created successfully for postId: {} by userId: {}", postId, response.getData().getUserId());
            } else {
                logger.warn("Comment creation failed for postId: {}. Errors: {}", postId, response.getErrors());
            }
            return response;
        } catch (Exception e) {
            logger.error("Error occurred while creating comment for postId: {}. Error: {}", postId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping(
            path = "/api/posts/{postId}/comments",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<List<CommentResponseDto>> getCommentsByPost(@PathVariable String postId) {
        logger.info("Received request to fetch comments for postId: {}", postId);

        try {
            WebResponseDto<List<CommentResponseDto>> response = commentService.getCommentsByPost(postId);

            if (response.getErrors() == null || response.getErrors().isEmpty()) {
                logger.info("Successfully fetched comments for postId: {}", postId);
            } else {
                logger.warn("Failed to fetch comments for postId: {}. Error: {}", postId, response.getErrors());
            }

            return response;
        } catch (Exception e) {
            logger.error("Error occurred while fetching comments for postId: {}. Error: {}", postId, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping(
            path = "/api/posts/{postId}/comments/{commentId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<String> deleteComment(@RequestHeader("X-API-TOKEN") String token,
                                                @PathVariable String postId,
                                                @PathVariable String commentId) {
        logger.info("Request to delete comment ID: {} for post ID: {}", commentId, postId);

        try {
            WebResponseDto<String> response = commentService.deleteComment(token, postId, commentId);

            if (response.getErrors() == null || response.getErrors().isEmpty()) {
                logger.info("Comment ID: {} successfully deleted for post ID: {}", commentId, postId);
            } else {
                logger.warn("Failed to delete comment ID: {} for post ID: {}. Error: {}", commentId, postId, response.getErrors());
            }

            return response;
        } catch (Exception e) {
            logger.error("Error occurred while deleting comment ID: {} for post ID: {}. Exception: {}", commentId, postId, e.getMessage(), e);
            throw e;
        }
    }

}
