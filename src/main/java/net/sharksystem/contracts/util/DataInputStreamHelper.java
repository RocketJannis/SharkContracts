package net.sharksystem.contracts.util;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

public class DataInputStreamHelper {

    /**
     * This method is part of DataInputStream in higher Java-Versions. To support older Android-versions only Java 8
     * methods are available so this helper method replaces it.
     * <br/>
     * It reads a specified number of bytes from a DataInputStream and returns them as a byte array.
     *
     * @param dis Inputs stream to read from
     * @param len How many bytes to read, also specifies the length of the returned array
     * @return Array of the read bytes
     * @throws IOException if reading fails or reaches the end of stream before len
     *
     * @see DataInputStream
     */
    public static byte[] readNBytes(DataInputStream dis, int len) throws IOException {
        byte[] buffer = new byte[len];
        int bytesRead = 0;

        while (bytesRead < len) {
            int count = Math.min(len - bytesRead, 1024);
            int result = dis.read(buffer, bytesRead, count);

            if (result == -1) {
                throw new EOFException();
            }

            bytesRead += result;
        }

        return buffer;
    }

}
