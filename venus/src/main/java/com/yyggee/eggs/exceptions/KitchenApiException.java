package com.yyggee.eggs.exceptions;

import com.yyggee.eggs.constants.ErrorCodes;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class KitchenApiException extends RuntimeException {
  /** */
  private static final long serialVersionUID = 4483625353027891720L;

  private final ErrorCodes errorCode;
  HttpStatus status;

  public KitchenApiException(ErrorCodes errorCode) {
    super(errorCode.getErrorMessage());
    this.errorCode = errorCode;
    this.status = errorCode.getHttpStatus();
  }

  public KitchenApiException(
      ErrorCodes errorCode,
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.errorCode = errorCode;
  }

  public KitchenApiException(ErrorCodes errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public KitchenApiException(ErrorCodes errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public KitchenApiException(ErrorCodes errorCode, Throwable cause) {
    super(cause);
    this.errorCode = errorCode;
  }
}
