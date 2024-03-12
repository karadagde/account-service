package account.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            {BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ResponseEntity<CustomErrorResponse> handleAuthenticationException(
            Exception ex, WebRequest request) {
        CustomErrorResponse response = handleExceptions(ex, request);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setError("Bad credentials");

        return new ResponseEntity<>(response,
                HttpStatus.UNAUTHORIZED);


    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ResponseEntity<CustomErrorResponse> handleInsufficientAuthenticationException(
            InsufficientAuthenticationException ex, WebRequest request) {
        CustomErrorResponse response = handleExceptions(ex, request);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setError("Login credentials are not sufficient.");

        return new ResponseEntity<>(response,
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountStatusException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ResponseEntity<CustomErrorResponse> handleAccountStatusException(
            AccountStatusException ex, WebRequest request) {
        CustomErrorResponse response = handleExceptions(ex, request);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        response.setError("Unauthorized");

        return new ResponseEntity<>(response,
                HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public final ResponseEntity<CustomErrorResponse> handleUsernameNotFoundException(
            UsernameNotFoundException ex, WebRequest request) {
        CustomErrorResponse response = handleExceptions(ex, request);
        response.setError("Not Found");
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(
            UserAlreadyExistsException.class)
    public final ResponseEntity<CustomErrorResponse> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, WebRequest request) {
        CustomErrorResponse customErrorResponse = handleExceptions(ex, request);

        return new ResponseEntity<>(customErrorResponse,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordBreachedException.class)
    public final ResponseEntity<CustomErrorResponse> handlePasswordBreachedException(
            PasswordBreachedException ex, WebRequest request) {
        CustomErrorResponse customErrorResponse = handleExceptions(ex, request);

        return new ResponseEntity<>(customErrorResponse,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(
            PasswordIsSameAsOldException.class)
    public final ResponseEntity<CustomErrorResponse> handlePasswordIsSameAsOldException(
            PasswordIsSameAsOldException ex, WebRequest request) {
        CustomErrorResponse customErrorResponse = handleExceptions(ex, request);

        return new ResponseEntity<>(customErrorResponse,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public final ResponseEntity<CustomErrorResponse> handleDataNotFoundException(
            DataNotFoundException ex, WebRequest request) {
        CustomErrorResponse customErrorResponse = handleExceptions(ex, request);
        customErrorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        customErrorResponse.setError("Not Found");
        return new ResponseEntity<>(customErrorResponse,
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SalaryInfoNotMatchException.class)
    public final ResponseEntity<CustomErrorResponse> handleSalaryInfoNotMatchException(
            SalaryInfoNotMatchException ex, WebRequest request) {
        CustomErrorResponse customErrorResponse = handleExceptions(ex, request);

        return new ResponseEntity<>(customErrorResponse,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<CustomErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        CustomErrorResponse customErrorResponse = handleExceptions(ex, request);
        customErrorResponse.setStatus(HttpStatus.FORBIDDEN.value());
        customErrorResponse.setError("Forbidden");
        return new ResponseEntity<>(customErrorResponse,
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity<CustomErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        System.out.println("MethodArgumentNotValidException");
        System.out.println(request.toString());
        System.out.println(request.getDescription(true));

        StringBuilder errors = new StringBuilder();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> errors.append(error.getDefaultMessage()));
        System.out.println(errors);
        CustomErrorResponse customErrorResponse = handleExceptions(ex, request);
        customErrorResponse.setMessage(errors.toString());

        return new ResponseEntity<>(customErrorResponse,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public final ResponseEntity<CustomErrorResponse> handleValidationException(

            ValidationException ex, WebRequest request) {
        System.out.println("ValidationException");
        CustomErrorResponse customErrorResponse = handleExceptions(ex, request);

        return new ResponseEntity<>(customErrorResponse,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<CustomErrorResponse> handleAllExceptions(
            Exception ex, WebRequest request) {

        CustomErrorResponse exceptionResponse = handleExceptions(ex, request);


        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    public CustomErrorResponse handleExceptions(Exception ex,
                                                WebRequest request) {

        CustomErrorResponse customErrorResponse = new CustomErrorResponse();
        customErrorResponse.setTimestamp(LocalDateTime.now());
        customErrorResponse.setMessage(ex.getMessage());
        customErrorResponse.setError("Bad Request");
        customErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        customErrorResponse.setPath(
                request.getDescription(false).replace("uri=", ""));

        return customErrorResponse;
    }


}

