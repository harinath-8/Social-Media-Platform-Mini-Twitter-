package mini_twitter.comment_service.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import mini_twitter.comment_service.dto.CommentRequestDto;
import mini_twitter.comment_service.dto.CommentResponseDto;
import mini_twitter.comment_service.dto.WebResponseDto;
import mini_twitter.comment_service.entity.Comment;
import mini_twitter.comment_service.repository.CommentRepository;
import mini_twitter.comment_service.webclient.UserServiceClient;
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
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UserServiceClient userServiceClient;

    @Transactional
    public WebResponseDto<CommentResponseDto> createComment(String token, String postId, CommentRequestDto request) {
        logger.info("Received request to create a comment for postId: {}, with request body: {}", postId, request);

        // Validate the comment content
        if (request.getComment() == null || request.getComment().trim().isEmpty()) {
            logger.warn("Validation failed: Comment content is empty for postId: {}", postId);
            return WebResponseDto.<CommentResponseDto>builder()
                    .errors("Comment cannot be empty.")
                    .build();
        }

        // Retrieve userId from token
        String userId;
        try {
            userId = userServiceClient.getUserIdFromToken(token);
            if (userId == null) {
                logger.error("Failed to retrieve userId from token for postId: {}. Token: {}", postId, token);
                return WebResponseDto.<CommentResponseDto>builder()
                        .errors("Invalid token, could not retrieve user ID.")
                        .build();
            }
            logger.info("Successfully retrieved userId: {} from token for postId: {}", userId, postId);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving userId from token for postId: {}. Error: {}", postId, e.getMessage());
            return WebResponseDto.<CommentResponseDto>builder()
                    .errors("Token validation failed.")
                    .build();
        }

        // Create and save the new comment
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID().toString());
        comment.setUserId(userId);
        comment.setPostId(postId);
        comment.setComment(request.getComment());
        comment.setCreatedAt(LocalDateTime.now());

        try {
            commentRepository.save(comment);
            logger.info("Comment successfully created for postId: {} by userId: {}. CommentId: {}", postId, userId, comment.getId());
        } catch (Exception e) {
            logger.error("Failed to save comment for postId: {} by userId: {}. Error: {}", postId, userId, e.getMessage());
            return WebResponseDto.<CommentResponseDto>builder()
                    .errors("Unable to save comment. Please try again later.")
                    .build();
        }

        // Create response DTO
        CommentResponseDto responseDto = toCommentResponse(comment);
        logger.info("Returning response for created commentId: {} for postId: {}", comment.getId(), postId);

        return WebResponseDto.<CommentResponseDto>builder()
                .data(responseDto)
                .build();
    }

    @Transactional
    public WebResponseDto<String> deleteComment(String token, String postId, String commentId) {
        logger.info("Request to delete comment ID: {} from post ID: {}", commentId, postId);

        // Retrieve userId from token
        String userId = userServiceClient.getUserIdFromToken(token);
        if (userId == null) {
            logger.warn("Unauthorized attempt to delete comment. Invalid token.");
            return WebResponseDto.<String>builder()
                    .errors("Unauthorized access. Invalid token.")
                    .build();
        }

        logger.info("User ID retrieved from token: {}", userId);

        // Find the comment by commentId
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();

            // Check if the comment belongs to the specified post and the user is the comment owner
            if (comment.getPostId().equals(postId) && comment.getUserId().equals(userId)) {
                commentRepository.delete(comment);
                logger.info("Comment ID: {} deleted successfully from post ID: {} by user ID: {}", commentId, postId, userId);
                return WebResponseDto.<String>builder()
                        .data("Comment deleted successfully.")
                        .build();
            } else {
                logger.warn("Unauthorized access attempt to delete comment ID: {} by user ID: {}", commentId, userId);
                return WebResponseDto.<String>builder()
                        .errors("Unauthorized access. You can only delete your own comments.")
                        .build();
            }
        } else {
            logger.error("Comment ID: {} not found for post ID: {} or user ID: {} is not authorized to delete it.", commentId, postId, userId);
            return WebResponseDto.<String>builder()
                    .errors("Comment not found or you are not authorized to delete this comment.")
                    .build();
        }
    }

    public WebResponseDto<List<CommentResponseDto>> getCommentsByPost(String postId) {
        logger.info("Fetching comments for postId: {}", postId);

        List<Comment> comments;
        try {
            comments = commentRepository.findByPostId(postId);
        } catch (Exception e) {
            logger.error("Error fetching comments for postId: {}. Error: {}", postId, e.getMessage());
            return WebResponseDto.<List<CommentResponseDto>>builder()
                    .errors("Unable to fetch comments. Please try again later.")
                    .build();
        }

        if (comments.isEmpty()) {
            logger.warn("No comments found for postId: {}", postId);
            return WebResponseDto.<List<CommentResponseDto>>builder()
                    .errors("No comments found for this post.")
                    .build();
        }

        List<CommentResponseDto> responseDtos = comments.stream()
                .map(this::toCommentResponse)
                .collect(Collectors.toList());

        logger.info("Fetched {} comments for postId: {}", responseDtos.size(), postId);
        return WebResponseDto.<List<CommentResponseDto>>builder()
                .data(responseDtos)
                .build();
    }
    
    private CommentResponseDto toCommentResponse(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .postId(comment.getPostId())
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
