package org.example.randomstream;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Zadanie pkt 1 - generator BBS (liczba Bluma dla generatora).
 * Implementacja liczby Bluma n  = p*q
 *  p i q mają po 512 bitow
 *  Używamy SecureRandom bo bierze on losowość z systemu operacyjnego
 */

public class BlumNumber {

    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger n;

    private BlumNumber(BigInteger p, BigInteger q){
        this.p = p;
        this.q = q;
        this.n = p.multiply(q);
    }

    /**
     * Losowanie p iq
     * @param bitsPerPrime - ile bitów ma mieć każda z liczb p i q
     * @param random
     */

    public static BlumNumber generate(int bitsPerPrime, SecureRandom random){
        BigInteger p = randomBlumPrime(bitsPerPrime, random);
        BigInteger q = randomBlumPrime(bitsPerPrime, random);

        while(q.equals(p)){
            q = randomBlumPrime(bitsPerPrime, random);
        }

        return new BlumNumber(p,q);
    }

    private static BigInteger randomBlumPrime(int bits,SecureRandom random){
        while(true){
            BigInteger candidate = BigInteger.probablePrime(bits, random);


            if(candidate.mod(BigInteger.valueOf(4)).intValue() == 3){
                return  candidate;
            }
        }
    }

    /**
     *  a : 1 < a < n i NWD(a ,n ) = 1
     */
    public BigInteger randomSeed(SecureRandom random){
        while(true){

            BigInteger a = new BigInteger(n.bitLength(), random);

            // compareTo zwraca >0, 0 lub <0 (a > b, a == b, a < b).
            boolean biggerThanOne = a.compareTo(BigInteger.ONE) > 0;
            boolean smallerThanN = a.compareTo(n) < 0;
            boolean coprime = a.gcd(n).equals(BigInteger.ONE);

            if (biggerThanOne && smallerThanN && coprime) {
                return a;
            }
        }
    }

    public BigInteger getN() { return n; }
    public BigInteger getP() { return p; }
    public BigInteger getQ() { return q; }

}
