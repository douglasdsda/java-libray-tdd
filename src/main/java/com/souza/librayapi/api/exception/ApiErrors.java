package com.souza.librayapi.api.exception;

import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import sun.security.provider.certpath.OCSPResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiErrors {
    private List<String> errors;

    public ApiErrors(BindingResult bindResult) {
        this.errors = new ArrayList<>();
        bindResult.getAllErrors().forEach( error -> this.errors.add(error.getDefaultMessage()));
    }

    public ApiErrors(BusinessException ex) {
        this.errors = Arrays.asList(ex.getMessage());
    }

    public ApiErrors(ResponseStatusException ex){
        this.errors = Arrays.asList(ex.getReason());
    }

    public List<String> getErrors() {
        return errors;
    }
}
