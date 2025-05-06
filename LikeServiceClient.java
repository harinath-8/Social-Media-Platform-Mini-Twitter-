package mini_twitter.post_service.webclient;

import mini_twitter.post_service.dto.WebResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class LikeServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(LikeServiceClient.class);
    private static final String BASE_URL = "http://localhost:8085/api/posts";
    private final WebClient webClient;

    @Autowired
    public LikeServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    public int getLikesByPostId(String postId) {
        String url = "/" + postId + "/likes";

        try {
            logger.info("Fetching likes for postId: {}", postId);

            WebResponseDto<Integer> response = webClient.method(HttpMethod.GET)
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<WebResponseDto<Integer>>() {})
                    .block();

            return handleResponse(response, postId);
        } catch (Exception e) {
            logger.error("Error fetching likes for postId: {}", postId, e);
            throw new RuntimeException("Failed to get likes for post", e);
        }
    }

    private int handleResponse(WebResponseDto<Integer> response, String postId) {
        if (response == null || response.getData() == null) {
            logger.error("Null response or no likes found for postId: {}", postId);
            throw new RuntimeException("Failed to get likes for post: Null response");
        }

        logger.info("Successfully fetched likes for postId: {}", postId);
        return response.getData();
    }
}
