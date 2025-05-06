package mini_twitter.post_service.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import mini_twitter.post_service.dto.PostDetailResponseDto;
import mini_twitter.post_service.dto.PostRequestDto;
import mini_twitter.post_service.dto.PostResponseDto;
import mini_twitter.post_service.dto.WebResponseDto;
import mini_twitter.post_service.entity.Post;
import mini_twitter.post_service.repository.PostRepository;
import mini_twitter.post_service.webclient.CommentServiceClient;
import mini_twitter.post_service.webclient.LikeServiceClient;
import mini_twitter.post_service.webclient.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private CommentServiceClient commentServiceClient;

    @Autowired
    private LikeServiceClient likeServiceClient;

    @Transactional
    public WebResponseDto<PostResponseDto> createPost(String token, PostRequestDto request) {
        logger.info("Create Post Request: {}, Token: {}", request, token);

        // Validate the post content
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            logger.error("Failed to create post: Content cannot be empty.");
            return WebResponseDto.<PostResponseDto>builder()
                    .errors("Content cannot be empty.")
                    .build();
        }

        // Retrieve userId from token
        String userId = userServiceClient.getUserIdFromToken(token);
        logger.info("User ID retrieved from token: {}", userId);

        // Create and save the new post
        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setUserId(userId);
        post.setContent(request.getContent());
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

        logger.info("Post created successfully: {}", post);

        // Create response DTO
        PostResponseDto responseDto = toPostResponse(post);
        return WebResponseDto.<PostResponseDto>builder()
                .data(responseDto)
                .build();
    }

    @Transactional
    public WebResponseDto<String> deletePost(String postId, String token) {
        logger.info("Request to delete post ID: {}", postId);

        String userId = userServiceClient.getUserIdFromToken(token);
        logger.info("User ID retrieved from token: {}", userId);

        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            if (post.getUserId().equals(userId)) {
                postRepository.delete(post);
                logger.info("Post ID: {} deleted successfully by user ID: {}", postId, userId);
                return WebResponseDto.<String>builder()
                        .data("Post deleted successfully.")
                        .build();
            } else {
                logger.warn("Unauthorized access attempt to delete post ID: {} by user ID: {}", postId, userId);
                return WebResponseDto.<String>builder()
                        .errors("Unauthorized access. You can only delete your own posts.")
                        .build();
            }
        } else {
            logger.error("Post ID: {} not found or user ID: {} is not authorized to delete this post.", postId, userId);
            return WebResponseDto.<String>builder()
                    .errors("Post not found or you are not authorized to delete this post.")
                    .build();
        }
    }

    public WebResponseDto<PostDetailResponseDto> getPostDetail(String postId) {
        logger.info("Fetching post details for postId: {}", postId);
        try {
            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isPresent()) {
                Post post = postOptional.get();
                logger.info("Post found for postId: {}", postId);

                var comments = commentServiceClient.getCommentsByPostId(postId);
                logger.info("Fetched {} comments for postId: {}", comments.size(), postId);

                int likeCount = likeServiceClient.getLikesByPostId(postId);
                logger.info("Fetched {} likes for postId: {}", likeCount, postId);

                PostDetailResponseDto postDetailResponse = PostDetailResponseDto.builder()
                        .id(post.getId())
                        .userId(post.getUserId())
                        .content(post.getContent())
                        .createdAt(post.getCreatedAt())
                        .comments(comments)
                        .likes(likeCount)
                        .build();

                return WebResponseDto.<PostDetailResponseDto>builder()
                        .data(postDetailResponse)
                        .build();
            } else {
                logger.warn("Post not found for postId: {}", postId);
                return WebResponseDto.<PostDetailResponseDto>builder()
                        .errors("Post not found.")
                        .build();
            }
        } catch (Exception e) {
            logger.error("Error fetching post details for postId: {}. Exception: {}", postId, e.getMessage());
            return WebResponseDto.<PostDetailResponseDto>builder()
                    .errors("An error occurred while fetching post details.")
                    .build();
        }
    }

    public WebResponseDto<List<PostResponseDto>> getUserPosts(String userId) {
        logger.info("Fetching posts for userId: {}", userId);
        try {
            List<Post> userPosts = postRepository.findAllByUserId(userId);
            if (userPosts.isEmpty()) {
                logger.warn("No posts found for userId: {}", userId);
                return WebResponseDto.<List<PostResponseDto>>builder()
                        .errors("No posts found for this user.")
                        .build();
            }

            List<PostResponseDto> postResponses = userPosts.stream()
                    .map(this::toPostResponse)
                    .collect(Collectors.toList());

            logger.info("Successfully fetched {} posts for userId: {}", postResponses.size(), userId);
            return WebResponseDto.<List<PostResponseDto>>builder()
                    .data(postResponses)
                    .build();
        } catch (Exception e) {
            logger.error("Error fetching posts for userId: {}. Exception: {}", userId, e.getMessage());
            return WebResponseDto.<List<PostResponseDto>>builder()
                    .errors("An error occurred while fetching posts.")
                    .build();
        }
    }

    private PostDetailResponseDto toPostDetailResponse(Post post) {
        return PostDetailResponseDto.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private PostResponseDto toPostResponse(Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
