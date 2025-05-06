package mini_twitter.post_service.webclient;

import mini_twitter.post_service.dto.CommentResponseDto;
import mini_twitter.post_service.dto.WebResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class CommentServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceClient.class);
    private static final String BASE_URL = "http://localhost:8084/api/posts";
    private final WebClient webClient;

    @Autowired
    public CommentServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    public List<CommentResponseDto> getCommentsByPostId(String postId) {
        String url = "/" + postId + "/comments";

        try {
            logger.info("Fetching comments for postId: {}", postId);

            WebResponseDto<List<CommentResponseDto>> response = webClient.method(HttpMethod.GET)
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<WebResponseDto<List<CommentResponseDto>>>() {})
                    .block();

            return handleResponse(response, postId);
        } catch (Exception e) {
            logger.error("Error fetching comments for postId: {}", postId, e);
            throw new RuntimeException("Failed to get comments for post", e);
        }
    }

    private List<CommentResponseDto> handleResponse(WebResponseDto<List<CommentResponseDto>> response, String postId) {
        if (response == null || response.getData() == null) {
            logger.error("Null response or no comments found for postId: {}", postId);
            throw new RuntimeException("Failed to get comments for post: Null response");
        }

        logger.info("Successfully fetched comments for postId: {}", postId);
        return response.getData();
    }
}
