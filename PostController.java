package mini_twitter.post_service.controller;

import mini_twitter.post_service.dto.PostDetailResponseDto;
import mini_twitter.post_service.dto.PostRequestDto;
import mini_twitter.post_service.dto.PostResponseDto;
import mini_twitter.post_service.dto.WebResponseDto;
import mini_twitter.post_service.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @PostMapping(
            path = "/api/posts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<PostResponseDto> createPost(@RequestHeader("X-API-TOKEN") String token,
                                                      @RequestBody PostRequestDto request) {
        logger.info("Request to create post with token: {}", token);

        try {
            WebResponseDto<PostResponseDto> response = postService.createPost(token, request);
            logger.info("Post created successfully with user ID: {}", response.getData().getUserId());
            return response;
        } catch (Exception e) {
            logger.error("Error creating post: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping(
            path = "/api/posts/{postId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<PostDetailResponseDto> getPostById(@PathVariable String postId) {
        logger.info("Request to fetch post with ID: {}", postId);

        try {
            WebResponseDto<PostDetailResponseDto> response = postService.getPostDetail(postId);
            logger.info("Successfully fetched post details for post ID: {}", postId);
            return response;
        } catch (Exception e) {
            logger.error("Error fetching post ID: {}: {}", postId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping(
            path = "/api/users/{userId}/posts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponseDto<List<PostResponseDto>>> getUserPosts(@PathVariable String userId) {
        logger.info("Request to fetch posts for userId: {}", userId);

        try {
            WebResponseDto<List<PostResponseDto>> response = postService.getUserPosts(userId);
            if (response.getErrors() != null) {
                logger.warn("No posts found for userId: {}", userId);
                return ResponseEntity.badRequest().body(response);
            }

            logger.info("Successfully fetched posts for userId: {}", userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching posts for userId: {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping(
            path = "/api/posts/{postId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponseDto<String> deletePost(@RequestHeader("X-API-TOKEN") String token,
                                             @PathVariable String postId) {
        logger.info("Request to delete post ID: {}", postId);

        try {
            WebResponseDto<String> response = postService.deletePost(postId, token);
            logger.info("Response for deleting post ID: {}: {}", postId, response);
            return response;
        } catch (Exception e) {
            logger.error("Error deleting post ID: {}: {}", postId, e.getMessage(), e);
            throw e;
        }
    }

}