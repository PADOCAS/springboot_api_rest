package com.ldsystems.api.rest.springbootapirest.exception;

import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@ControllerAdvice
public class ControleExcecoes {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();
        ObjetoErro objetoErro = new ObjetoErro("Token expirado, faça Login ou informe um Token válido para autenticação!", requestUri, HttpStatus.UNAUTHORIZED.value() + " ==> " + HttpStatus.UNAUTHORIZED.getReasonPhrase(), LocalDateTime.now());
        return new ResponseEntity<>(objetoErro, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<?> handleSignatureJwtException(SignatureException ex, WebRequest request) {
        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();
        ObjetoErro objetoErro = new ObjetoErro("Token é inválido.", requestUri, HttpStatus.UNAUTHORIZED.value() + " ==> " + HttpStatus.UNAUTHORIZED.getReasonPhrase(), LocalDateTime.now());
        return new ResponseEntity<>(objetoErro, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<?> handleMalformedJwtException(MalformedJwtException ex, WebRequest request) {
        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();
        //Ocorre quando o token não está formatado corretamente. Isso pode ser causado por caracteres inválidos, comprimento incorreto ou falta de componentes essenciais.
        ObjetoErro objetoErro = new ObjetoErro("Token não está formatado corretamente. Contém caracteres inválidos, comprimento incorreto ou falta de componentes essenciais.", requestUri, HttpStatus.UNAUTHORIZED.value() + " ==> " + HttpStatus.UNAUTHORIZED.getReasonPhrase(), LocalDateTime.now());
        return new ResponseEntity<>(objetoErro, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ClaimJwtException.class)
    public ResponseEntity<?> handleClaimJwtException(ClaimJwtException ex, WebRequest request) {
        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();
        //Ocorre quando o token não está formatado corretamente. Isso pode ser causado por caracteres inválidos, comprimento incorreto ou falta de componentes essenciais.
        ObjetoErro objetoErro = new ObjetoErro("Reivindicação do token JWT inválida.", requestUri, HttpStatus.UNAUTHORIZED.value() + " ==> " + HttpStatus.UNAUTHORIZED.getReasonPhrase(), LocalDateTime.now());
        return new ResponseEntity<>(objetoErro, HttpStatus.UNAUTHORIZED);
    }

    //Tratamento de exceções a nível de banco de dados (maioria deles)
    @ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class, SQLException.class})
    protected ResponseEntity<Object> handleExceptionDataIntegry(Exception ex, WebRequest request) {
        StringBuilder msg = new StringBuilder();

        if (ex instanceof DataIntegrityViolationException
                || ex instanceof ConstraintViolationException
                || ex instanceof SQLException) {
            msg.append(ex.getCause().getCause().getMessage());
        } else {
            msg.append(ex.getMessage());
        }

        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();
        ObjetoErro objetoErro = new ObjetoErro(msg.toString(), requestUri, HttpStatus.INTERNAL_SERVER_ERROR.value() + " ==> " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), LocalDateTime.now());
        return new ResponseEntity<>(objetoErro, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Vai interceptar qualquer exceptions que seja dessas classes declaradas (Exception, RunTimeException ou Throwable)
    @ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, WebRequest request) {
        StringBuilder msg = new StringBuilder();

        if (ex instanceof MethodArgumentNotValidException) {
            //Pode ter vários erros, vamos varrer e setar todos os erros na mensagem:
            List<ObjectError> listObjetoErro = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();

            for (ObjectError error : listObjetoErro) {
                msg.append(error.getDefaultMessage()).append("\n");
            }
        } else {
            msg = new StringBuilder(ex.getMessage());
        }

        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();
        ObjetoErro objetoErro = new ObjetoErro(msg.toString(), requestUri, HttpStatus.INTERNAL_SERVER_ERROR.value() + " ==> " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), LocalDateTime.now());
        return new ResponseEntity<>(objetoErro, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
