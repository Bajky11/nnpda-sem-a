package nndpa.sem_a.dto.AppUser;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;

    @NotEmpty(message = "Username is required")
    @Size(min = 3, message = "Username must be at least 3 characters long")
    private String username;

    @NotEmpty(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotEmpty(message = "Email is required")
    private String email;
}
