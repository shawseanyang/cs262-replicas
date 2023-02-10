package protocol;

// An enumeration of possible operations

public enum Operation {
  CREATE_ACCOUNT,
  CREATE_ACCOUNT_RESPONSE,
  LIST_ACCOUNTS,
  LIST_ACCOUNTS_RESPONSE,
  LOG_IN,
  LOG_IN_RESPONSE,
  LOG_OUT,
  LOG_OUT_RESPONSE,
  SEND_MESSAGE,
  SEND_MESSAGE_RESPONSE,
  DISTRIBUTE_MESSAGE,
  DISTRIBUTE_MESSAGE_RESPONSE,
  DELETE_ACCOUNT,
  DELETE_ACCOUNT_RESPONSE;

  public static Operation fromByte(byte b) {
    switch (b) {
      case 0:
        return CREATE_ACCOUNT;
      case 1:
        return CREATE_ACCOUNT_RESPONSE;
      case 2:
        return LIST_ACCOUNTS;
      case 3:
        return LIST_ACCOUNTS_RESPONSE;
      case 4:
        return LOG_IN;
      case 5:
        return LOG_IN_RESPONSE;
      case 6:
        return LOG_OUT;
      case 7:
        return LOG_OUT_RESPONSE;
      case 8:
        return SEND_MESSAGE;
      case 9:
        return SEND_MESSAGE_RESPONSE;
      case 10:
        return DISTRIBUTE_MESSAGE;
      case 11:
        return DISTRIBUTE_MESSAGE_RESPONSE;
      case 12:
        return DELETE_ACCOUNT;
      case 13:
        return DELETE_ACCOUNT_RESPONSE;
      default:
        return null;
    }
  }

  public byte toByte() {
    switch (this) {
      case CREATE_ACCOUNT:
        return 0;
      case CREATE_ACCOUNT_RESPONSE:
        return 1;
      case LIST_ACCOUNTS:
        return 2;
      case LIST_ACCOUNTS_RESPONSE:
        return 3;
      case LOG_IN:
        return 4;
      case LOG_IN_RESPONSE:
        return 5;
      case LOG_OUT:
        return 6;
      case LOG_OUT_RESPONSE:
        return 7;
      case SEND_MESSAGE:
        return 8;
      case SEND_MESSAGE_RESPONSE:
        return 9;
      case DISTRIBUTE_MESSAGE:
        return 10;
      case DISTRIBUTE_MESSAGE_RESPONSE:
        return 11;
      case DELETE_ACCOUNT:
        return 12;
      case DELETE_ACCOUNT_RESPONSE:
        return 13;
      default:
        return -1;
    }
  }
}

