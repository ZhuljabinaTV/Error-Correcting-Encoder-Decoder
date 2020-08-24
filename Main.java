package correcter;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.print("Write a mode: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        String send = "send.txt";
        String encoded = "encoded.txt";
        String received = "received.txt";
        String decoded = "decoded.txt";

        switch (input) {
            case "encode": {
                writeMessage(encoded, encodeHamming(readMessage(send)));
                break;
            }
            case "send": {
                writeMessage(received, addBitErrors(readMessage(encoded)));
                break;
            }
            case "decode": {
                writeMessage(decoded, decodeHamming(readMessage(received)));
                break;
            }
            default: {
                break;
            }
        }
    }

    static byte[] readMessage(String fileName) throws IOException {
        FileInputStream reader = new FileInputStream(fileName);
        byte[] bytes = reader.readAllBytes();
        reader.close();
        return bytes;
    }

    static void writeMessage(String fileName, byte[] bytes) throws IOException {
        FileOutputStream writer = new FileOutputStream(fileName);
        writer.write(bytes);
        writer.close();
    }

    static byte[] addBitErrors(byte[] bytes) {
        Random random = new Random();
        for (int i = 0; i < bytes.length; i++) {
            int n = random.nextInt(8);
            byte mask = (byte) (1 << n);
            bytes[i] = (byte) (bytes[i]^mask);
        }
        return bytes;
    }

    static byte[] encodeHamming(byte[] bytes) {
        byte[] bytesOut = new byte[bytes.length * 2];
        for (int i = 0; i < bytesOut.length; i++) {
            int b = i % 2 == 0 ?  (bytes[i / 2] >> 4) : bytes[i / 2];
            int b3 = (b & 0x08) >> 3;
            int b5 = (b & 0x04) >> 2;
            int b6 = (b & 0x02) >> 1;
            int b7 = b & 0x01;
            int b1 = (b3 + b5 + b7) % 2;
            int b2 = (b3 + b6 + b7) % 2;
            int b4 = (b5 + b6 + b7) % 2;
            b = (b1 << 7) + (b2 << 6) + (b3 << 5) + (b4 << 4) + ( b5 << 3) + (b6 << 2) + (b7 << 1);
            bytesOut[i] = (byte) b;
        }

        return bytesOut;
    }

    static byte[] decodeHamming(byte[] bytes) {
        byte[] bytesOut = new byte[bytes.length / 2];
        for (int i = 0; i < bytesOut.length; i++) {
            for (int j = 0; j < 2; j++) {
                int b = j == 0 ? bytes[2 * i] : bytes[2 * i + 1];
                int[] bits = new int[8];
                bits[0] = (b & 0x80) >> 7; //
                bits[1] = (b & 0x40) >> 6; //
                bits[2] = (b & 0x20) >> 5;
                bits[3] = (b & 0x10) >> 4; //
                bits[4] = (b & 0x08) >> 3;
                bits[5] = (b & 0x04) >> 2;
                bits[6] = (b & 0x02) >> 1;
                bits[7] = b & 0x01;
                if (bits[7] == 0) {
                    int b1 = (bits[2] + bits[4] + bits[6]) % 2;
                    int b2 = (bits[2] + bits[5] + bits[6]) % 2;
                    int b4 = (bits[4] + bits[5] + bits[6]) % 2;
                    int n1 = b1 == bits[0] ? 0 : 1;
                    int n2 = b2 == bits[1] ? 0 : 2;
                    int n3 = b4 == bits[3] ? 0 : 4;
                    int n = n1 + n2 + n3;
                    bits[n - 1] = bits[n - 1] == 1 ? 0 : 1;
                }
                b = (bits[2] << 3) + (bits[4] << 2) + (bits[5] << 1) + bits[6];
                bytesOut[i] += j == 0 ?  b << 4 : b;
            }
        }
        return bytesOut;
    }
}

