package com.mmanchola.blog.exception;

import org.springframework.util.StringUtils;

public enum ExceptionMessage {
  INVALID(" is invalid"),
  MISSING(" is missing"),
  UNAVAILABLE(" has already been taken"),
  MISSING_INVALID(" is missing or invalid"),
  MISSING_UNAVAILABLE(" is missing or has been already taken"),
  INVALID_UNAVAILABLE(" is invalid or has already been taken"),
  ALL(" is missing, invalid or unavailable"),
  NOT_FOUND(" was not found");

  private String message;
  ExceptionMessage(String message) {
    this.message = message;
  }

  public String getMsg(String tableField) {
    return StringUtils.capitalize(tableField) + this.message;
  }
}
