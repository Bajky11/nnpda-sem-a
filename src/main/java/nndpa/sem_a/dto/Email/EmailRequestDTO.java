package nndpa.sem_a.dto.Email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequestDTO {

    @NotEmpty(message = "Příjemce nesmí být prázdný.")
    @Email(message = "Neplatný formát e-mailu.")
    private String to;

    @NotEmpty(message = "Předmět nesmí být prázdný.")
    private String subject;

    @NotEmpty(message = "Tělo e-mailu nesmí být prázdné.")
    private String body;
}
