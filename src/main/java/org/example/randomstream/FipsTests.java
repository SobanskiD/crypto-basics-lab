package org.example.randomstream;

import java.util.List;

/**
 * Zadanie pkt 2 - 4 testy statystyczne wg FIPS 140-2
 * (pojedynczych bitow, serii, dlugiej serii, pokerowy).
 * Uzywane tez w pkt 5 (badanie wygenerowanych ciagow) i pkt 7 (badanie szyfrogramu).

 * Test pojedynczych bitów - liczymy ile jest jedynek w ciągu 20 000 bitów
 * Więcej niz 9725 i mniej niż 10275 jedynek jest normalna
 */
public final class FipsTests {

    public static final int REQUIRED_LENGTH = 20000;

    /*
     * Dolne i gorne granice dla testu serii, indeksowane DLUGOSCIA serii.
     * Tablica ma 7 miejsc (indeksy 0..6): indeks 0 jest nieuzywany (=0), zeby
     * RUN_MIN[3] znaczylo "dolna granica dla serii dlugosci 3". Indeks 6 to
     * "6 i wiecej" (serie dluzsze niz 6 liczymy jako dlugosc 6).
     */
    private static final int[] RUN_MIN = {0, 2315, 1114, 527, 240, 103, 103};
    private static final int[] RUN_MAX = {0, 2685, 1386, 723, 384, 209, 209};

    // Seria dluga to seria o dlugosci 26 lub wiecej.
    private static final int LONG_RUN = 26;

    private FipsTests() {
    }

    private static void requireLength(String bits) {
        if (bits.length() != REQUIRED_LENGTH) {
            throw new IllegalArgumentException(
                    "Testy FIPS wymagaja dokladnie " + REQUIRED_LENGTH
                            + " bitow, a podano " + bits.length()
                            + ". Uzyj BitFile.extract(...), zeby obciac lub wybrac podciag.");
        }
    }

    /**
     * 1. Pojedyncze bity
     */
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

    /**
     * 2.  Test serii
     * Oczekiwana liczba serii zer (i osobno jedynek) o dlugosci k to ok. 20000 / 2^(k+2): 2500,1250,625,312,156
     * a dla dlugosci 6 i wiecej ok 156
     */

    public static TestResult runsTest(String bits) {
        requireLength(bits);

        int[] zeroRuns = new int[7];
        int[] oneRuns = new int[7];

        int runLength = 1;
        for (int i = 1; i <= bits.length(); i++) {
            boolean runEnds = (i == bits.length())
                    || bits.charAt(i) != bits.charAt(i - 1);

            if (runEnds) {
                int index = Math.min(runLength, 6);

                if (bits.charAt(i - 1) == '0') {
                    zeroRuns[index]++;
                } else {
                    oneRuns[index]++;
                }
                runLength = 1;
            } else {
                runLength++;
            }
        }

        StringBuilder sb = new StringBuilder("Liczba serii (zera / jedynki) i wymagany przedzial:");
        boolean passed = true;
        for (int len = 1; len <= 6; len++) {
            boolean zerosOk = zeroRuns[len] >= RUN_MIN[len] && zeroRuns[len] <= RUN_MAX[len];
            boolean onesOk = oneRuns[len] >= RUN_MIN[len] && oneRuns[len] <= RUN_MAX[len];
            if (!zerosOk || !onesOk) {
                passed = false;
            }
            String label = (len == 6) ? "6+" : String.valueOf(len);
            sb.append(String.format("%n  dlugosc %-2s: %4d / %4d   (%d-%d)   %s",
                    label, zeroRuns[len], oneRuns[len],
                    RUN_MIN[len], RUN_MAX[len],
                    (zerosOk && onesOk) ? "OK" : "BLAD"));
        }

        return new TestResult("Test serii", sb.toString(), passed);
    }

    /**
     * 3. Test długiej serii
     * Szukamy najdluzszego bloku identycznych bitow, jesli jest gdzies blok o ciagu 26 jedynek lub zer - ciąg odpada.
     * Najdluzsza seria ma ok 14-17 bitów.
     */
    public static TestResult longRunTest(String bits) {
        requireLength(bits);

        int longest = 1;
        int current = 1;
        for (int i = 1; i < bits.length(); i++) {
            if (bits.charAt(i) == bits.charAt(i - 1)) {
                current++;
                if (current > longest) {
                    longest = current;
                }
            } else {
                current = 1;
            }
        }

        boolean passed = longest < LONG_RUN;

        String details = "najdluzsza seria = " + longest
                + ", wymagane: brak serii o dlugosci >= " + LONG_RUN;

        return new TestResult("Test dlugiej serii", details, passed);
    }

    /**
     * Test 4. Pokerowy
     * Tniemy ciag na paczki po 4 bity: 5000 paczek
     * Kazda paczka to liczba od 0000 do 1111, liczymy ile razy wypadla jedna karta
     * w losowym ciagu 5000/16 = 312,5 jesli ktoras wypada zdecydowanie czesciej wzorzec jest widoczny
     */

    public static TestResult pokerTest(String bits) {
        requireLength(bits);

        int[] counts = new int[16];
        for (int i = 0; i < bits.length(); i += 4) {
            int value = Integer.parseInt(bits.substring(i, i + 4), 2);
            counts[value]++;
        }

        long sumSquares = 0;
        for (int c : counts) {
            sumSquares += (long) c * c;
        }

        int blocks = REQUIRED_LENGTH / 4;
        double x = 16.0 / blocks * sumSquares - blocks;

        boolean passed = x > 2.16 && x < 46.17;

        String details = String.format("X = %.2f, wymagane: 2.16 < X < 46.17", x);

        return new TestResult("Test pokerowy", details, passed);
    }


    public static List<TestResult> runAll(String bits) {
        return List.of(
                singleBitsTest(bits),
                runsTest(bits),
                longRunTest(bits),
                pokerTest(bits));
    }
}