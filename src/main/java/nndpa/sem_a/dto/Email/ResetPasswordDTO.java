package nndpa.sem_a.dto.Email;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDTO {

    @NotEmpty(message = "Token nesmí být prázdný.")
    private String token;

    @NotEmpty(message = "Nové heslo nesmí být prázdné.")
    private String newPassword;
}
