package mini_twitter.like_service.webclient;

import mini_twitter.like_service.dto.WebResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);
    private static final String BASE_URL = "http://localhost:8081/api/users";
    private final WebClient webClient;

    @Autowired
    public UserServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
    }

    public String getUserIdFromToken(String token) {
        String url = "/me";

        try {
            logger.info("Fetching user ID from token: {}", token);

            WebResponseDto<String> response = webClient.method(HttpMethod.GET)
                    .uri(url)
                    .header("X-API-TOKEN", token)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<WebResponseDto<String>>() {})
                    .block();

            return handleResponse(response, token);
        } catch (Exception e) {
            logger.error("Error fetching user ID from token: {}", token, e);
            throw new RuntimeException("Failed to get user ID from token", e);
        }
    }

    private String handleResponse(WebResponseDto<String> response, String token) {
        if (response == null || response.getData() == null) {
            logger.error("Null response or no user ID found for token: {}", token);
            throw new RuntimeException("Failed to get user ID from token: Null response");
        }

        logger.info("Successfully fetched user ID: {}", response.getData());
        return response.getData();
    }

}