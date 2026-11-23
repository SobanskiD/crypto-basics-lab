package org.example.randomstream;

/**
 * Zadanie pkt 2 - 4 testy statystyczne wg FIPS 140-2
 * (pojedynczych bitow, serii, dlugiej serii, pokerowy).
 * Uzywane tez w pkt 5 (badanie wygenerowanych ciagow) i pkt 7 (badanie szyfrogramu).
 *
 * Test pojedynczych bitów - liczymy ile jest jedynek w ciągu 20 000 bitów
 * Więcej niz 9725 i mniej niż 10275 jedynek jest normalna
 */
public final class FipsTests {
    public static final int REQUIRED_LENGTH = 20_000;

    private FipsTests(){}

    private static void requireLength(String bits) {
        if (bits.length() != REQUIRED_LENGTH) {
            throw new IllegalArgumentException(
                    "Testy FIPS wymagaja dokladnie " + REQUIRED_LENGTH
                            + " bitow, a podano " + bits.length()
                            + ". Uzyj BitFile.extract(...), zeby obciac lub wybrac podciag.");
        }
    }

    public static TestResult singleBitsTest(String bits) {
        requireLength(bits);

        int ones = 0;
        for (int i = 0; i < bits.length(); i++) {
            if (bits.charAt(i) == '1') {
                ones++;
            }
        }

        boolean passed = ones > 9725 && ones < 10275;

        String details = "n1 (liczba jedynek) = " + ones
                + ", wymagane: 9725 < n1 < 10275";

        return new TestResult("Test pojedynczych bitow", details, passed);
    }

}
