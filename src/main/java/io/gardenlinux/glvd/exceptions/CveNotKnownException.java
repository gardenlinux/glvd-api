package io.gardenlinux.glvd.exceptions;

public class CveNotKnownException extends RuntimeException{
    public CveNotKnownException(String message) {
        super(message);
    }
}
