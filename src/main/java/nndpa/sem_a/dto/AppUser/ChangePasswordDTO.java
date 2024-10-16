package nndpa.sem_a.dto.AppUser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDTO {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}
