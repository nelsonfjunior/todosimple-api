package com.nelsonjunior.todosimple.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.persistence.EntityNotFoundException;

@ResponseStatus(HttpStatus.NOT_FOUND) //409, caso nao encontre um usuário
public class ObjectNotFoundException extends EntityNotFoundException {
    
    //construtor
    public ObjectNotFoundException(String message) {
        super(message);
    }

}
