package com.chatapp.protocol;

public enum Constant {
  // port
  PORT(8080);

  public final int value;

  private Constant(int value) {
      this.value = value;
  }
}
