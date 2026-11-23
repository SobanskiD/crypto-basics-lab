package org.example;

import org.example.randomstream.BbsGenerator;
import org.example.randomstream.BlumNumber;
import org.example.randomstream.FipsTests;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Zadanie pkt 4 i 5 - generowanie ciagu BBS o dlugosci 20 000 bitow
 * i sprawdzenie jego wlasnosci testami FIPS 140-2.
 * (Na razie 1 ciag i tylko test pojedynczych bitow; pkt 4 wymaga 3 roznych ciagow.)
 */
public class Main {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        BlumNumber blum = BlumNumber.generate(512, random);
        BigInteger a = blum.randomSeed(random);
        BbsGenerator gen = new BbsGenerator(blum.getN(), a);

        String bbsBits = gen.nextBits(20000);
        System.out.println("--- ciag z BBS");
        System.out.println(FipsTests.singleBitsTest(bbsBits));

        String zeros = "0".repeat(20000);
        System.out.println("--- same zera");
        System.out.println(FipsTests.singleBitsTest(zeros));

        String alternating = "01".repeat(10000);
        System.out.println("--- 0101... (nielosowy, a test przechodzi)");
        System.out.println(FipsTests.singleBitsTest(alternating));

        System.out.println("--- za krotki ciag");
        try {
            FipsTests.singleBitsTest("0101");
        } catch (IllegalArgumentException e) {
            System.out.println("Blad: " + e.getMessage());
        }
    }
}