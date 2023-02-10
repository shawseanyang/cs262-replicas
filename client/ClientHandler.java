package client;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ClientHandler {


    // Needed to convert UUID to byte array
    private static byte[] asBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
      }

}
