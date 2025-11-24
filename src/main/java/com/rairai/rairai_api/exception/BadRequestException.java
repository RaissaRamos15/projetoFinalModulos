package com.rairai.rairai_api.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class BadRequestException extends RuntimeException {

    private BindingResult bindingResult;

    public BadRequestException(String message, BindingResult bindingResult) {
        super(message);
        this.bindingResult = bindingResult;
    }

    public BadRequestException(String message) {
        super(message);
    }
}
