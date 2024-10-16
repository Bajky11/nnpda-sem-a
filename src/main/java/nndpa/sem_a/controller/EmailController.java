package nndpa.sem_a.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import nndpa.sem_a.dto.Email.EmailRequestDTO;
import nndpa.sem_a.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Email", description = "API pro posílání emailů")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @Operation(summary = "Posílání emailů")
    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody @Valid EmailRequestDTO emailRequestDTO) {
        emailService.sendMail(emailRequestDTO.getTo(), emailRequestDTO.getSubject(), emailRequestDTO.getBody());
        return ResponseEntity.ok("E-mail byl úspěšně odeslán.");
    }
}
