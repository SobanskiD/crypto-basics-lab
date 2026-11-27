package org.example;

import org.example.randomstream.BbsGenerator;
import org.example.randomstream.BitFile;
import org.example.randomstream.BlumNumber;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.SecureRandom;

/**
 * Ta sama logika co w generatorze BBS jednak zamiast 20_000 bitów mamy milion.
 */
public class MillionBits {
    public static void main(String[] args) throws IOException {
        final int LENGTH = 1_000_000;

        SecureRandom random = new SecureRandom();
        BlumNumber blum = BlumNumber.generate(512, random);
        BigInteger a = blum.randomSeed(random);
        BbsGenerator gen = new BbsGenerator(blum.getN(), a);

        long start = System.currentTimeMillis();
        String bits = gen.nextBits(LENGTH);
        long ms = System.currentTimeMillis() - start;

        System.out.println("Wygenerowano " + bits.length() + " bitow w " + ms + " ms");
        System.out.println("Czy generator zakrecil kolko (okres <= dlugosc)? " + gen.hasCycledBack());

        Path file = Path.of("data", "million.txt");
        BitFile.write(file, bits);
        System.out.println("Zapisano do " + file);

        // FIPS dziala tylko na 20_000 bitach, wiec bierzemy pierwsze 20_000 z tego miliona
        String first20000 = BitFile.extract(bits, 0, 20000);
        Main.printAll(first20000);

    }
}