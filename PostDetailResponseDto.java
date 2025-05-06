package mini_twitter.post_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostDetailResponseDto {

    private String id;

    private String userId;

    private String content;

    private LocalDateTime createdAt;

    private int likes;

    private List<CommentResponseDto> comments;

}

