package nndpa.sem_a.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nndpa.sem_a.dto.AppUser.ChangePasswordDTO;
import nndpa.sem_a.dto.AppUser.NewUserDTO;
import nndpa.sem_a.dto.AppUser.UserDTO;
import nndpa.sem_a.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Uživatelé", description = "API pro správu uživatelů")
public class UserController {

    private final UserService appUserService;

    @Operation(summary = "Získání všech uživatelů", description = "Vrátí všechny uživatelé.")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = appUserService.findAll();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Získání uživatele podle ID", description = "Vrátí informace o uživateli na základě zadaného ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        Optional<UserDTO> user = appUserService.findById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Uživatel s ID " + id + " nebyl nalezen.");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Vytvoření nového uživatele", description = "Vytvoří nového uživatele a vrátí jeho data.")
    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody @Valid NewUserDTO newUserDTO) {
        try {
            UserDTO createdUser = appUserService.addUser(newUserDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Uživatel nebyl vytvořen: " + e.getMessage());
        }
    }

    @Operation(summary = "Aktualizace uživatele", description = "Aktualizuje údaje uživatele podle zadaného ID.")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody @Valid NewUserDTO userDTO) {
        try {
            UserDTO updatedUser = appUserService.updateUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Uživatel nebyl aktualizován: " + e.getMessage());
        }
    }

    @Operation(summary = "Smazání uživatele", description = "Smaže uživatele na základě zadaného ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            UserDTO user = appUserService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Uživatel nebyl odstraněn: " + e.getMessage());
        }
    }

    @Operation(summary = "Změna hesla uživatele", description = "Změní heslo uživatele s daným id.")
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        try {
            boolean result = appUserService.changePassword(changePasswordDTO.getUserId(), changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword());
            if (result) {
                return ResponseEntity.status(HttpStatus.OK).body("Heslo bylo změněno.");
            }
            return ResponseEntity.status(HttpStatus.OK).body("Chyba heslo něbylo změněno");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chyba heslo nebylo změněno: " + e.getMessage());
        }
    }

    @Operation(summary = "Přiřazení zařízení uživateli", description = "Přiřadí existující zařízení k uživateli podle jejich ID.")
    @PostMapping("/{userId}/devices/{deviceId}")
    public ResponseEntity<UserDTO> assignDeviceToUser(@PathVariable Long userId, @PathVariable Long deviceId) {
        UserDTO userDTO = appUserService.assignDeviceToUser(userId, deviceId);
        return ResponseEntity.ok(userDTO);
    }
}
