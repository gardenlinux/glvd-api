package io.gardenlinux.glvd.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Could not connect to Database. Is it running and configured correctly?")
public class DbNotConnectedException extends Throwable {
    public DbNotConnectedException(Exception e) {
    }
}
