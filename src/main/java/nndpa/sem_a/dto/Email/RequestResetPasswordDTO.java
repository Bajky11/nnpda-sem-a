package nndpa.sem_a.dto.Email;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestResetPasswordDTO {
    @NotEmpty(message = "Email nesmí být prázdný.")
    private String email;
}
