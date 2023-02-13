package utility;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class ByteConverter {
  public static String byteArrayToString(byte[] bytes) {
    return new String(bytes);
  }

  public static byte[] stringToByteArray(String string) {
    return string.getBytes();
  }

  public static byte[] ByteArrayListToByteArray(ArrayList<Byte> bytes) {
    byte[] result = new byte[bytes.size()];
    for (int i = 0; i < bytes.size(); i++) {
      result[i] = bytes.get(i);
    }
    return result;
  }

  public static byte[][] splitByteArray(byte[] array, byte separator) {
      int separatorCount = 0;
      for (byte b : array) {
          if (b == separator) {
              separatorCount++;
          }
      }

      byte[][] result = new byte[separatorCount + 1][];
      int index = 0;
      int startIndex = 0;
      for (int i = 0; i < array.length; i++) {
          if (array[i] == separator) {
              result[index] = Arrays.copyOfRange(array, startIndex, i);
              startIndex = i + 1;
              index++;
          }
      }
      result[index] = Arrays.copyOfRange(array, startIndex, array.length);
      return result;
  }

  public static UUID byteArrayToUUID(byte[] bytes) {
      ByteBuffer bb = ByteBuffer.wrap(bytes);
      long firstLong = bb.getLong();
      long secondLong = bb.getLong();
      return new UUID(firstLong, secondLong);
  }

  public static byte[] UUIDToByteArray(UUID uuid) {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
    return bb.array();
  }

}
