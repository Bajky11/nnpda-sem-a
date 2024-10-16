package nndpa.sem_a.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nndpa.sem_a.dto.AppUser.LoginDTO;
import nndpa.sem_a.dto.AppUser.RegisterDTO;
import nndpa.sem_a.dto.Email.RequestResetPasswordDTO;
import nndpa.sem_a.dto.Email.ResetPasswordDTO;
import nndpa.sem_a.service.UserService;
import nndpa.sem_a.service.PasswordResetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authenticate", description = "API pro přihlašování registraci a obnovu hesla")
public class AuthenticationController {
    private final UserService appUserService;
    private final PasswordResetService passwordResetService;

    @Operation(summary = "Přihlášení uživatele", description = "Porovná údaje uživatele a vrátí token nebo unathorized")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginDTO loginDTO) {
        try {
            String token = appUserService.login(loginDTO);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Přihlášení selhalo: " + e.getMessage());
        }
    }

    @Operation(summary = "Registrace uživatele", description = "Porovná údaje uživatele")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDTO registerDTO) {
        try {
            appUserService.registerUser(registerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Uživatel byl úspěšně vytvořen.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registrace selhala.");
        }
    }

    @Operation(summary = "Žádost o změnu hesla")
    @PostMapping("/request-reset-password")
    public ResponseEntity<String> requestResetPassword(@RequestBody @Valid RequestResetPasswordDTO resetPasswordDTO) {
        try{
            passwordResetService.sendPasswordResetToken(resetPasswordDTO.getEmail());
            return ResponseEntity.ok("Byl zaslán ověřovací token na email.");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Požadavek změny hesla selhal: " + e.getMessage());
        }
    }

    @Operation(summary = "Změna hesla")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        try{
            passwordResetService.resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getNewPassword());
            return ResponseEntity.ok("Heslo bylo úspěšně změněno.");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Požadavek změny hesla selhal: " + e.getMessage());
        }
    }
}
