package com.mmanchola.blog.exception;

import org.springframework.util.StringUtils;

public enum ExceptionMessage {
    INVALID("es inválido (valor mal escrito)"),
    MISSING("hace falta (valor obligatorio)"),
    UNAVAILABLE("ya se encuentra registrado"),
    MISSING_INVALID("es inválido o inexistente"),
    MISSING_UNAVAILABLE("está faltando o ya está ocupado"),
    INVALID_UNAVAILABLE("es inválido o no está disponible"),
    ALL("está faltando, es inválido o no está disponible"),
    NOT_FOUND("no se encuentra registrado");

    private String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMsg(String tableField) {
        return StringUtils.capitalize(tableField) + " " + this.message;
    }
}
