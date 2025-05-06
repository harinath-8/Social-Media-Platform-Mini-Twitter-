package mini_twitter.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {

    @NotNull
    @Size(max = 255)
    private String username;

    @NotNull
    @Email
    @Size(max = 255)
    private String email;

    @NotNull
    private String password;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 500)
    private String bio;
}
