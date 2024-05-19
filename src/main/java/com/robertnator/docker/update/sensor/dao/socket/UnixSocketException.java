package com.robertnator.docker.update.sensor.dao.socket;

public class UnixSocketException extends Exception {

    public UnixSocketException(String message) {
        super(message);
    }

    public UnixSocketException(String message, Throwable cause) {
        super(message, cause);
    }
}
