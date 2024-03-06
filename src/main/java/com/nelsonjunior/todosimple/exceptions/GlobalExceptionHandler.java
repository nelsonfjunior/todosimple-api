package com.nelsonjunior.todosimple.exceptions;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.nelsonjunior.todosimple.services.exceptions.DataBindingViolationException;
import com.nelsonjunior.todosimple.services.exceptions.ObjectNotFoundException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice // avisa que essa classe deve ser inicializada junto com o spring
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${server.error.include-exception}") // nem sempre é bom printar o trace por questao de seguranca, controla no aplication
    private boolean printStackTrace; 

    
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // erro 422, caso esqueça de mandar alguma coisa
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException, HttpHeaders headers, HttpStatus status, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(),"Validation error Check 'errors' field for details.");
        for (FieldError fieldError : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableEntity().body(errorResponse); // retorna o numero do erro e os detalhes

    }

    @ExceptionHandler(Exception.class) // erro 500
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //erro interno do servidor
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request) {

        final String errorMessage = "Unknown error occurred"; // um erro que nao foi tratado, erro desconhecido, erro padrao
        log.error(errorMessage, exception);
        return buildErrorResponse(exception, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, request); // teve que adicionar a dependencia do common lang

    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // erro 409, caso queira cadastrar um usuario com mesmo nome
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException dataIntegrityViolationException, WebRequest request) {

        String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
        log.error("Failed to save entity with integrity problems: " + errorMessage, dataIntegrityViolationException);
        return buildErrorResponse(dataIntegrityViolationException, errorMessage, HttpStatus.CONFLICT, request);

    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // erro 422, criar um usuario sem passar a senha
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException constraintViolationException,WebRequest request) {

        log.error("Failed to validate element", constraintViolationException);
        return buildErrorResponse(constraintViolationException, HttpStatus.UNPROCESSABLE_ENTITY, request);

    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // erro 404, nao escontrado referido a classe ObjectNotFoundException
    public ResponseEntity<Object> handleObjectNotFoundException(ObjectNotFoundException objectNotFoundException, WebRequest request) {
        
        log.error("Failed to find the requested element", objectNotFoundException);
        return buildErrorResponse(objectNotFoundException, HttpStatus.NOT_FOUND, request);

    }

    @ExceptionHandler(DataBindingViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // erro 409, referido a classe DataBindingViolationException
    public ResponseEntity<Object> handleDataBindingViolationException(DataBindingViolationException dataBindingViolationException,WebRequest request) {

        log.error("Failed to save entity with associated data", dataBindingViolationException);
        return buildErrorResponse(dataBindingViolationException, HttpStatus.CONFLICT, request);
    
    }

    

    private ResponseEntity<Object> buildErrorResponse(Exception exception, HttpStatus httpStatus, WebRequest request) {

        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);

    }

    private ResponseEntity<Object> buildErrorResponse(Exception exception, String message, HttpStatus httpStatus, WebRequest request){

        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        if (this.printStackTrace) {
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);

    }

    



}
