package nndpa.sem_a.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nndpa.sem_a.dto.AppUser.*;
import nndpa.sem_a.entity.AppUser;
import nndpa.sem_a.entity.Device;
import nndpa.sem_a.repository.AppUserRepository;
import nndpa.sem_a.repository.DeviceRepository;
import nndpa.sem_a.utils.jwt.passwordReset.PasswordResetJwtUtil;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository userRepository;
    private final JwtAuthenticationService jwtAuthenticationService;
    private final DeviceRepository deviceRepository;

    public Optional<AppUser> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id).map(this::convertToDTO);
    }

    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToDTO);
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public UserDTO addUser(NewUserDTO newUserDTO) {
        AppUser newUser = new AppUser();
        newUser.setUsername(newUserDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(newUserDTO.getPassword()));
        newUser.setEmail(newUserDTO.getEmail());

        AppUser savedUser = userRepository.save(newUser);
        return convertToDTO(savedUser);
    }

    public UserDTO registerUser(RegisterDTO registerDTO) {
        if (userRepository.findByUsername(registerDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Uživatel s tímto uživatelským jménem již existuje.");
        }

        AppUser newUser = new AppUser();
        newUser.setUsername(registerDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        newUser.setEmail(registerDTO.getEmail());

        AppUser savedUser = userRepository.save(newUser);
        return convertToDTO(savedUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Vyhledání uživatele podle uživatelského jména v databázi
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Uživatel nenalezen s uživatelským jménem: " + username));

        // Vrácení instance UserDetails, kterou používá Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>() // Role a oprávnění uživatele
        );
    }

    @Transactional
    public UserDTO deleteUser(Long id) {
        AppUser user = findUserByIdOrThrowError(id);
        userRepository.deleteById(id);
        return convertToDTO(user);
    }

    public UserDTO updateUser(Long id, NewUserDTO updatedUserDTO) {
        AppUser user = findUserByIdOrThrowError(id);

        Optional.ofNullable(updatedUserDTO.getUsername()).ifPresent(user::setUsername);
        Optional.ofNullable(updatedUserDTO.getPassword()).ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));  // Zakódování hesla
        Optional.ofNullable(updatedUserDTO.getEmail()).ifPresent(user::setEmail);

        userRepository.save(user);
        return convertToDTO(user);
    }

    public AppUser findUserByIdOrThrowError(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Uživatel nenalezen"));
    }

    public LoginResponseDTO login(LoginDTO loginDTO) {
        Optional<AppUser> userOptional = userRepository.findByUsername(loginDTO.getUsername());

        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();

            if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
                String token = jwtAuthenticationService.generateToken(loginDTO.getUsername());

                // Vrátíme LoginResponse s tokenem a uživatelskými daty
                return new LoginResponseDTO(token, user.getId(), user.getUsername());
            } else {
                throw new RuntimeException("Neplatné heslo.");
            }
        } else {
            throw new RuntimeException("Uživatel nenalezen.");
        }
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<AppUser> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            AppUser user = userOptional.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));  // Heslo zakódováno
                userRepository.save(user);
                return true;
            } else {
                return false;  // Staré heslo neodpovídá
            }
        } else {
            throw new RuntimeException("Uživatel nebyl nalezen.");
        }
    }

    @Transactional
    public UserDTO assignDeviceToUser(Long userId, Long deviceId) {
        AppUser user = findUserByIdOrThrowError(userId);
        Device device = findDeviceByIdOrThrowError(deviceId);
        user.getDevices().add(device);
        device.getUsers().add(user);
        userRepository.save(user);
        deviceRepository.save(device);
        return convertToDTO(user);
    }

    private Device findDeviceByIdOrThrowError(Long deviceId) {
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new EntityNotFoundException("Device with ID " + deviceId + " not found"));
    }

    private UserDTO convertToDTO(AppUser user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }
}
