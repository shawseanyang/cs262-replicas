package protocol;

// An enumeration of possible exceptions

public enum Exception {
  NONE, // No exception
  USER_ALREADY_EXISTS,
  USER_DOES_NOT_EXIST,
  NOT_LOGGED_IN;

  public static Exception fromByte(byte b) {
    switch (b) {
      case 0:
        return NONE;
      case 1:
        return USER_ALREADY_EXISTS;
      case 2:
        return USER_DOES_NOT_EXIST;
      case 3:
        return NOT_LOGGED_IN;
      default:
        return null;
    }
  }

  public byte toByte() {
    switch (this) {
      case NONE:
        return 0;
      case USER_ALREADY_EXISTS:
        return 1;
      case USER_DOES_NOT_EXIST:
        return 2;
      case NOT_LOGGED_IN:
        return 3;
      default:
        return -1;
    }
  }
}

