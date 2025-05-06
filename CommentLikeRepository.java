package mini_twitter.like_service.repository;

import mini_twitter.like_service.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, String> {

    Optional<CommentLike> findByCommentIdAndUserId(String commentId, String userId);

    int countByCommentId(String commentId);

}
