package nndpa.sem_a.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        // Zpracuje všechny ostatní výjimky
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nastala chyba při zpracování požadavku.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicitní uživatelské jméno nebo e-mail.");
    }
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<String> handleSQLException(SQLException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Nastala integritní chyba z databáze.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        // Vytvoříme mapu pro uložení chybových zpráv
        Map<String, String> errors = new HashMap<>();

        // Pro každou chybu z validace získáme pole a odpovídající zprávu
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Vrátíme chyby jako odpověď s HTTP status kódem 400 (Bad Request)
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
