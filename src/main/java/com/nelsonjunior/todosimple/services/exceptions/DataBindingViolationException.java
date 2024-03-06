package com.nelsonjunior.todosimple.services.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;


@ResponseStatus(HttpStatus.NOT_FOUND) //404, caso tenha integracao
public class DataBindingViolationException extends DataIntegrityViolationException {
    
    //construtor
    public DataBindingViolationException(String message) {
        super(message);
    }

}