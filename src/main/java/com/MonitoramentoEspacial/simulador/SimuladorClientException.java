package com.MonitoramentoEspacial.simulador;

public class SimuladorClientException extends RuntimeException {

    public SimuladorClientException(String message) {
        super(message);
    }

    public SimuladorClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
