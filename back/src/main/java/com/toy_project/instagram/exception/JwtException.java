package com.toy_project.instagram.exception;

public class JwtException extends RuntimeException {
    public JwtException(String message) {
        super(message);
    }
}
