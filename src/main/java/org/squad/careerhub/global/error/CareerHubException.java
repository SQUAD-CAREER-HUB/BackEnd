package org.squad.careerhub.global.error;

import lombok.Getter;

@Getter
public class CareerHubException extends RuntimeException {

    private final ErrorStatus errorStatus;

    public CareerHubException(ErrorStatus status) {
        super(status.getMessage());
        this.errorStatus = status;
    }

    public CareerHubException(String message) {
        super(message);
        this.errorStatus = null;
    }

    public CareerHubException(ErrorStatus status, Throwable cause) {
        super(status.getMessage(), cause);
        this.errorStatus = status;
    }

}