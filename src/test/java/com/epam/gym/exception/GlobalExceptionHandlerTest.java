package com.epam.gym.exception;

import com.epam.gym.dto.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private MockHttpServletRequest request(String uri) {
        return new MockHttpServletRequest("GET", uri);
    }

    @SuppressWarnings("unused")
    private void dummyMethod(String arg) {
    }

    @Test
    void handleNotFound_shouldReturn404() {
        EntityNotFoundException ex = new EntityNotFoundException("Trainee", "Nazar.Volianskyi");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(ex, request("/api/trainees/Nazar.Volianskyi"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ex.getMessage(), response.getBody().getMessage());
        assertEquals("/api/trainees/Nazar.Volianskyi", response.getBody().getPath());
    }

    @Test
    void handleAuthentication_shouldReturn401() {
        AuthenticationException ex = new AuthenticationException("Invalid username or password");

        ResponseEntity<ErrorResponse> response = handler.handleAuthentication(ex, request("/api/auth/login"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody().getMessage());
    }

    @Test
    void handleSecurityAuthentication_shouldReturn401_withGenericMessage() {
        BadCredentialsException ex = new BadCredentialsException("some internal detail");

        ResponseEntity<ErrorResponse> response = handler.handleSecurityAuthentication(ex, request("/api/auth/login"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody().getMessage());
    }

    @Test
    void handleConflict_shouldReturn409() {
        ConflictException ex = new ConflictException("Trainee Nazar.Volianskyi is already active");

        ResponseEntity<ErrorResponse> response = handler.handleConflict(ex, request("/api/trainees/Nazar.Volianskyi/status"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(ex.getMessage(), response.getBody().getMessage());
    }

    @Test
    void handleValidation_shouldReturn400_withFieldErrors() throws NoSuchMethodException {
        MethodParameter methodParameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", String.class), 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "firstName", "must not be blank"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex, request("/api/trainees"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertNotNull(response.getBody().getFieldErrors());
        assertEquals("must not be blank", response.getBody().getFieldErrors().get("firstName"));
    }
}
