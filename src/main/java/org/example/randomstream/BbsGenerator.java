package org.example.randomstream;
import java.math.BigInteger;

public class BbsGenerator {

    // n = p*g
    private final BigInteger n;

    // aktualny stan generatora
    private BigInteger x;

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
    }

    public int nextBit(){
        // xi = x(i-1)^2 mod n
        x = x.multiply(x).mod(n);

        //testBit(0) = najmlodszy nit
        return x.testBit(0) ? 1 : 0;
    }

    /**
     * zwraca liczbe kolejny bitów jako napis
     */
    public String nextBits(int count){
        StringBuilder sb = new StringBuilder(count);

        for(int i = 0; i < count; i++){
            sb.append(nextBit());
        }

        return sb.toString();
    }
}
