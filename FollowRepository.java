package mini_twitter.follow_service.repository;

import mini_twitter.follow_service.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, String> {

    boolean existsByUserIdAndFollowerId(String userId, String followerId);

    void deleteByUserIdAndFollowerId(String userId, String followerId);

    List<Follow> findByUserId(String userId);

    List<Follow> findByFollowerId(String userId);
}
