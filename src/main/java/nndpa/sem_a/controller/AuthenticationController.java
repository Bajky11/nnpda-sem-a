package nndpa.sem_a.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nndpa.sem_a.dto.AppUser.LoginDTO;
import nndpa.sem_a.dto.AppUser.LoginResponseDTO;
import nndpa.sem_a.dto.AppUser.RegisterDTO;
import nndpa.sem_a.dto.Email.RequestResetPasswordDTO;
import nndpa.sem_a.dto.Email.ResetPasswordDTO;
import nndpa.sem_a.entity.AppUser;
import nndpa.sem_a.service.JwtAuthenticationService;
import nndpa.sem_a.service.UserService;
import nndpa.sem_a.service.PasswordResetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Authenticate", description = "API pro přihlašování registraci a obnovu hesla")
public class AuthenticationController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final JwtAuthenticationService jwtAuthenticationService;

    @Operation(summary = "Přihlášení uživatele pomocí token", description = "Ověři platnost tokenu a existenci uživatele vrátí uživatelská data nebo unathorized jestli token vypršel, nebo uživatel neexistuje")
    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody String token) {
        try {
            // Ověříme, zda token není vypršelý
            if (jwtAuthenticationService.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Extrahujeme uživatelské jméno z tokenu
            String username = jwtAuthenticationService.extractUsername(token);

            // Získáme uživatelská data na základě username
            Optional<AppUser> optionalUser = userService.getUserByUsername(username);

            // Pokud uživatel neexistuje, vrátíme 404 Not Found
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Pokud uživatel existuje, získáme data
            AppUser user = optionalUser.get();

            // Připravíme odpověď obsahující uživatelská data
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // Pokud token není platný
        }
    }

    @Operation(summary = "Přihlášení uživatele", description = "Porovná údaje uživatele a vrátí token nebo unathorized")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO) {
        try {
            LoginResponseDTO loggedUser = userService.login(loginDTO);
            return ResponseEntity.ok(loggedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Přihlášení selhalo: " + e.getMessage());
        }
    }

    @Operation(summary = "Registrace uživatele", description = "Porovná údaje uživatele")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDTO registerDTO) {
        try {
            userService.registerUser(registerDTO);
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
        try {
            passwordResetService.sendPasswordResetToken(resetPasswordDTO.getEmail());
            return ResponseEntity.ok("Byl zaslán ověřovací token na email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Požadavek změny hesla selhal: " + e.getMessage());
        }
    }

    @Operation(summary = "Změna hesla")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        try {
            passwordResetService.resetPassword(resetPasswordDTO.getToken(), resetPasswordDTO.getNewPassword());
            return ResponseEntity.ok("Heslo bylo úspěšně změněno.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Požadavek změny hesla selhal: " + e.getMessage());
        }
    }
}
