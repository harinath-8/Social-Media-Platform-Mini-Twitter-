package mini_twitter.like_service.configuration;

import lombok.extern.slf4j.Slf4j;
import mini_twitter.like_service.entity.PostLike;
import mini_twitter.like_service.entity.CommentLike;
import mini_twitter.like_service.repository.PostLikeRepository;
import mini_twitter.like_service.repository.CommentLikeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Bean
    public CommandLineRunner initLikeData() {
        return args -> {
            try {
                LocalDateTime now = LocalDateTime.now();

                postLikeRepository.save(new PostLike(
                        "1",
                        "1",
                        "1",
                        now
                ));

                postLikeRepository.save(new PostLike(
                        "2",
                        "2",
                        "2",
                        now
                ));

                postLikeRepository.save(new PostLike(
                        "3",
                        "3",
                        "3",
                        now
                ));

                postLikeRepository.save(new PostLike(
                        "4",
                        "4",
                        "4",
                        now
                ));

                postLikeRepository.save(new PostLike(
                        "5",
                        "5",
                        "5",
                        now
                ));
                
                commentLikeRepository.save(new CommentLike(
//                        UUID.randomUUID(),
                        "1",
                        "1",
                        "1",
                        now
                ));

                commentLikeRepository.save(new CommentLike(
                        "2",
                        "2",
                        "2",
                        now
                ));

                commentLikeRepository.save(new CommentLike(
                        "3",
                        "3",
                        "3",
                        now
                ));

                commentLikeRepository.save(new CommentLike(
                        "4",
                        "4",
                        "4",
                        now
                ));

                commentLikeRepository.save(new CommentLike(
                        "5",
                        "5",
                        "5",
                        now
                ));

                logger.info("Like data initialized successfully");
            } catch (Exception e) {
                logger.error("Failed to initialize like data", e);
            }
        };
    }
}
