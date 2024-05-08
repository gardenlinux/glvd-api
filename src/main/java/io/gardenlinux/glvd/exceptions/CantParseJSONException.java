package io.gardenlinux.glvd.exceptions;

public class CantParseJSONException extends RuntimeException{

    public CantParseJSONException(String message) {
        super(message);
    }
}
