package org.example.randomstream;

/**
 * Zadanie pkt 2 - wynik testu FIPS: obliczone statystyki + odpowiedz TAK/NIE
 * (SPELNIONY/NIESPELNIONY).
 */
public record TestResult(String name, String details, boolean passed) {

    @Override
    public String toString(){
        String verdict = passed ? "TAK (SPELNIONY)" : "NIE (NIESPELNIONY)";
        return name + "\n  " + details + "\n  Wynik: " + verdict;
    }
}
