package org.example.randomstream;
import java.math.BigInteger;

/**
 * Zadanie pkt 1 - generator ciągów binarnych BBS (BigInteger dla długich okresów).
 */
public class BbsGenerator {

    // n = p*g
    private final BigInteger n;

    // aktualny stan generatora
    private BigInteger x;

    // punkt startowy x0 - jesli do niego wrocimy, ciag zaczyna sie powtarzac
    private final BigInteger x0;

    // true, gdy generator wrocil do punktu startowego (okres <= liczba wygenerowanych bitow)
    private boolean cycledBack = false;

    /**
     * @param n modul (liczba Bluma)
     * @param a ziarno - losowa liczba wzglednie pierwsza z n
     */

    public BbsGenerator(BigInteger n, BigInteger a){

        // a.gcd(n) - najwiekszy wspolny dzielnik
        if(!a.gcd(n).equals(BigInteger.ONE)){
            throw new IllegalArgumentException("NW(a,n) musi wynosic 1");
        }

        this.n =n;
        // x0 = a^2 mod n ( krok 1 alogrytmu)
        this.x = a.multiply(a).mod(n);
        this.x0 = this.x;
    }

    public int nextBit(){
        // xi = x(i-1)^2 mod n
        x = x.multiply(x).mod(n);

        // powrot do punktu startowego = ciag zaczyna sie powtarzac
        if(x.equals(x0)){
            cycledBack = true;
        }

        //testBit(0) = najmlodszy bit
        return x.testBit(0) ? 1 : 0;
    }

    public String nextBits(int count){
        StringBuilder sb = new StringBuilder(count);

        for(int i = 0; i < count; i++){
            sb.append(nextBit());
        }

        return sb.toString();
    }

    /**
     * true, jesli generator wrocil do punktu startowego (okres klucza <= dlugosc wygenerowanego ciagu)
     */
    public boolean hasCycledBack(){
        return cycledBack;
    }
}