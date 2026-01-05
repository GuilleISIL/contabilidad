package org.example.contabilidad.support;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestControllerAdvice
public class ApiErrorHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Parámetros inválidos");
    pd.setDetail(ex.getMessage());
    pd.setType(URI.create("https://example.com/errors/validation"));
    return pd;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Argumento ilegal");
    pd.setDetail(ex.getMessage());
    pd.setType(URI.create("https://example.com/errors/illegal-argument"));
    return pd;
  }
}
