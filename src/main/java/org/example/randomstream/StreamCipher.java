package org.example.randomstream;

import java.math.BigInteger;

/**
 * Zadanie pkt 3 - szyfrator strumieniowy: klucz z generatora BBS, szyfrowanie
 * i deszyfrowanie to XOR bit po bicie (c = m XOR k, m = c XOR k).
 */
public final class StreamCipher {

    private StreamCipher() {
    }

    public static String encrypt(String plainBits, BigInteger n, BigInteger a) {
        return crypt(plainBits, n, a);
    }

    public static String decrypt(String cipherBits, BigInteger n, BigInteger a) {
        return crypt(cipherBits, n, a);
    }

    private static String crypt(String bits, BigInteger n, BigInteger a) {
        BbsGenerator gen = new BbsGenerator(n, a);

        // klucz o dokladnie takiej dlugosci jak wiadomosc
        String key = gen.nextBits(bits.length());

        if (gen.hasCycledBack()) {
            throw new IllegalStateException(
                    "Okres klucza jest nie dluzszy niz wiadomosc (" + bits.length()
                            + " bitow) - klucz by sie powtarzal. Uzyj wiekszego n.");
        }

        return xor(bits, key);
    }

    public static String xor(String x, String y) {
        if (x.length() != y.length()) {
            throw new IllegalArgumentException(
                    "XOR wymaga ciagow o tej samej dlugosci: " + x.length() + " vs " + y.length());
        }
        StringBuilder sb = new StringBuilder(x.length());
        for (int i = 0; i < x.length(); i++) {
            sb.append(x.charAt(i) == y.charAt(i) ? '0' : '1'); // XOR na znakach
        }
        return sb.toString();
    }
}