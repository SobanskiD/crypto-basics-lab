package org.example.randomstream;

import java.nio.charset.StandardCharsets;

/**
 * Tekst <-> ciag bitow (8 bitow na bajt, kodowanie UTF-8).
 */
public final class BitConverter {

    private BitConverter() {
    }

    /** Tekst -> ciag bitow, od najstarszego bitu kazdego bajtu. */
    public static String textToBits(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder(bytes.length * 8);
        for (byte b : bytes) {
            for (int i = 7; i >= 0; i--) {
                sb.append((b >> i) & 1);
            }
        }
        return sb.toString();
    }

    /** Ciag bitow (dlugosc podzielna przez 8) -> tekst. */
    public static String bitsToText(String bits) {
        if (bits.length() % 8 != 0) {
            throw new IllegalArgumentException(
                    "Dlugosc ciagu bitow musi byc wielokrotnoscia 8, a jest " + bits.length());
        }
        byte[] bytes = new byte[bits.length() / 8];
        for (int i = 0; i < bytes.length; i++) {
            String eight = bits.substring(i * 8, i * 8 + 8);
            bytes[i] = (byte) Integer.parseInt(eight, 2);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}