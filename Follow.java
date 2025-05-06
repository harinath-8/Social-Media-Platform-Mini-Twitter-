package mini_twitter.follow_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "follows", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "follower_id"})
})
public class Follow {

    @Id
    private String id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private String userId;  // The user being followed

    @NotNull
    @Column(name = "follower_id", nullable = false)
    private String followerId;  // The user who is following

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
