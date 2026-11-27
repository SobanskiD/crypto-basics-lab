package org.example;

import org.example.randomstream.BitConverter;
import org.example.randomstream.BitFile;
import org.example.randomstream.BlumNumber;
import org.example.randomstream.StreamCipher;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.SecureRandom;

/**
 * Zadanie pkt 7 - szyfrowanie wiadomosci szyfrem strumieniowym z BBS
 * i badanie wlasnosci losowych szyfrogramu.
 */
public class CipherDemo {
    public static void main(String[] args) throws IOException {
        SecureRandom random = new SecureRandom();
        BlumNumber blum = BlumNumber.generate(512, random);
        BigInteger n = blum.getN();
        BigInteger a = blum.randomSeed(random);

        // 2500 znakow ASCII = 2500 bajtow = dokladnie 20000 bitow (dlugosc dla FIPS)
        String text = "Ala ma kota, a kot ma Ale. ".repeat(100).substring(0, 2500);
        String plainBits = BitConverter.textToBits(text);
        System.out.println("Wiadomosc: " + text.length() + " znakow = "
                + plainBits.length() + " bitow");

        System.out.println("========== WIADOMOSC JAWNA (oczekiwane: oblane) ==========");
        Main.printAll(plainBits);

        String cipherBits = StreamCipher.encrypt(plainBits, n, a);
        System.out.println("========== SZYFROGRAM (oczekiwane: 4 z 4) ==========");
        Main.printAll(cipherBits);

        BitFile.write(Path.of("data", "plain.txt"), plainBits);
        BitFile.write(Path.of("data", "cipher.txt"), cipherBits);

        // deszyfrowanie poprawnym kluczem
        String decryptedText = BitConverter.bitsToText(StreamCipher.decrypt(cipherBits, n, a));
        System.out.println("Deszyfrowanie poprawnym kluczem daje oryginal? "
                + decryptedText.equals(text));

        // deszyfrowanie zlym ziarnem
        BigInteger wrongA = blum.randomSeed(random);
        System.out.println("Deszyfrowanie zlym ziarnem daje oryginal? "
                + StreamCipher.decrypt(cipherBits, n, wrongA).equals(plainBits));

        // maly modul: okres 4, wiec klucz do 20000 bitow by sie powtarzal
        try {
            StreamCipher.encrypt(plainBits, BigInteger.valueOf(77), BigInteger.valueOf(3));
        } catch (IllegalStateException e) {
            System.out.println("Maly modul - oczekiwany blad: " + e.getMessage());
        }
    }
}