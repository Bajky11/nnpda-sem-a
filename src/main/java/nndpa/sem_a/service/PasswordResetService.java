package nndpa.sem_a.service;

import lombok.AllArgsConstructor;
import nndpa.sem_a.entity.AppUser;
import nndpa.sem_a.repository.AppUserRepository;
import nndpa.sem_a.utils.jwt.passwordReset.PasswordResetJwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PasswordResetService {

    private final AppUserRepository userRepository;
    private final EmailService emailService;
    private final PasswordResetJwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public void sendPasswordResetToken(String email) {
        // Ověření, zda uživatel existuje
        Optional<AppUser> appUser = userRepository.findByEmail(email);

        if(appUser.isEmpty()){
            throw new RuntimeException("Uživatel s tímto e-mailem neexistuje.");
        }

        AppUser foundUser = appUser.get();

        // Vygenerování JWT tokenu pro reset hesla
        String token = jwtUtil.generatePasswordResetToken(foundUser.getEmail());

        // Odeslání e-mailu
        String emailBody = "Zde je token pro resetování hesla: " + token;
        emailService.sendMail(foundUser.getEmail(), "Resetování hesla", emailBody);
    }

    public void resetPassword(String token, String newPassword) {
        // Ověření platnosti tokenu
        if (!jwtUtil.validatePasswordResetToken(token)) {
            throw new RuntimeException("Neplatný nebo vypršelý token.");
        }

        // Extrakce e-mailu z tokenu
        String email = jwtUtil.extractEmail(token);

        // Najdeme uživatele na základě e-mailu
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Uživatel nenalezen."));

        // Nastavení nového hesla
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
