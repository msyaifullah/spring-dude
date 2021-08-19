package com.yyggee.eggs.constants;

import org.springframework.http.HttpStatus;

public enum ErrorCodes {
    /* ---- common ---- */
    NOT_IMPLEMENTED_YET(101010, "Not Implemented Yet!", HttpStatus.BAD_REQUEST);


    private final int errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;

    ErrorCodes(int errorCode, String errorMessage, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
