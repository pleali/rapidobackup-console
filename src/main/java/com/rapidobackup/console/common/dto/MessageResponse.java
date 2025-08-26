package com.rapidobackup.console.common.dto;

public class MessageResponse {

  private String message;
  private String type = "info";
  private long timestamp = System.currentTimeMillis();

  public MessageResponse() {}

  public MessageResponse(String message) {
    this.message = message;
  }

  public MessageResponse(String message, String type) {
    this.message = message;
    this.type = type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}