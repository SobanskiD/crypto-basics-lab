package org.example;

import org.example.randomstream.BbsGenerator;
import org.example.randomstream.BitFile;
import org.example.randomstream.BlumNumber;
import org.example.randomstream.FipsTests;
import org.example.randomstream.TestResult;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.List;

/**
 * Zadanie pkt 4 i 5 - 3 rozne ciagi BBS po 20 000 bitow i sprawdzenie ich
 * czterema testami FIPS 140-2.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        SecureRandom random = new SecureRandom();

        // kazdy ciag z innego n i innego ziarna
        for (int k = 1; k <= 3; k++) {
            BlumNumber blum = BlumNumber.generate(512, random);
            BigInteger a = blum.randomSeed(random);
            BbsGenerator gen = new BbsGenerator(blum.getN(), a);
            String bits = gen.nextBits(20000);

            BitFile.write(Path.of("data", "seq" + k + ".txt"), bits);

            System.out.println("========== CIAG " + k + " (BBS) ==========");
            printAll(bits);
        }

        // dla porownania: ciagi "zle", ktore maja oblac testy
        System.out.println("========== same zera (oczekiwane: oblane) ==========");
        printAll("0".repeat(20000));

        System.out.println("========== 0101... (oczekiwane: 2 z 4) ==========");
        printAll("01".repeat(10000));
    }

    /** Uruchamia 4 testy FIPS, wypisuje wyniki i podsumowanie (uzywane tez przez inne klasy). */
    static void printAll(String bits) {
        List<TestResult> results = FipsTests.runAll(bits);
        int passedCount = 0;

        for (TestResult r : results) {
            System.out.println(r);
            if (r.passed()) {
                passedCount++;
            }
        }
        System.out.println(">>> zdane testy: " + passedCount + " z " + results.size());
    }
}