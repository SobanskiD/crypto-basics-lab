package org.example;

import org.example.randomstream.BbsGenerator;
import org.example.randomstream.BitConverter;
import org.example.randomstream.BitFile;
import org.example.randomstream.BlumNumber;
import org.example.randomstream.StreamCipher;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App {

    private final Scanner scanner = new Scanner(System.in);
    private final SecureRandom random = new SecureRandom();

    private String current = null;
    private String currentLabel = "(brak)";

    private BigInteger cipherN;
    private BigInteger cipherA;
    private String lastCipherBits;

    public static void main(String[] args) {
        new App().run();
    }

    /** Glowna petla: pokaz menu, wczytaj wybor, wykonaj akcje, powtorz. */
    private void run() {
        try {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = scanner.nextLine().trim();

                try {
                    switch (choice) {
                        case "1" -> generate();
                        case "2" -> load();
                        case "3" -> save();
                        case "4" -> cut();
                        case "5" -> runFips();
                        case "6" -> encrypt();
                        case "7" -> decrypt();
                        case "0" -> running = false;
                        default -> System.out.println("Nieznana opcja: '" + choice + "'");
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("BLAD: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("BLAD PLIKU: " + e.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("(koniec wejscia)");
        }
        System.out.println("Koniec.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("================ MENU ================");
        System.out.println("Biezacy ciag: " + currentLabel
                + (current == null ? "" : ", dlugosc " + current.length() + " bitow"));
        System.out.println("1. Generuj ciag BBS");
        System.out.println("2. Wczytaj ciag z pliku .txt");
        System.out.println("3. Zapisz biezacy ciag do pliku .txt");
        System.out.println("4. Wytnij podciag (obciecie / wybor fragmentu)");
        System.out.println("5. Testy FIPS 140-2 na biezacym ciagu");
        System.out.println("6. Zaszyfruj tekst szyfrem strumieniowym (BBS)");
        System.out.println("7. Odszyfruj ostatni szyfrogram");
        System.out.println("0. Wyjscie");
        System.out.print("Wybierz opcje: ");
    }

    //  generowanie ciagu BBS (pkt 1, 4, 8)
    private void generate() {
        int length = readInt("Dlugosc ciagu w bitach [20000]: ", 20000);
        if (length <= 0) {
            throw new IllegalArgumentException("Dlugosc musi byc dodatnia.");
        }
        System.out.println("Generuje... (dla milionow bitow potrwa to kilka sekund)");

        BlumNumber blum = BlumNumber.generate(512, random);
        BigInteger a = blum.randomSeed(random);
        BbsGenerator gen = new BbsGenerator(blum.getN(), a);

        current = gen.nextBits(length);
        currentLabel = "BBS";
        System.out.println("Wygenerowano " + length + " bitow (n ma "
                + blum.getN().bitLength() + " bitow).");

        if (gen.hasCycledBack()) {
            System.out.println("UWAGA: generator wrocil do punktu startowego - okres krotszy niz ciag!");
        }
    }

    // wczytanie z pliku (pkt 2: ciagi zewnetrzne, pkt 6: kolega)
    private void load() throws IOException {
        String path = readLine("Sciezka do pliku .txt z ciagiem 0/1: ").trim().replace("\"", "");
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Nie podano sciezki.");
        }
        current = BitFile.read(Path.of(path));
        currentLabel = "plik " + path;
        System.out.println("Wczytano " + current.length() + " bitow.");
    }

    //  zapis biezacego ciagu
    private void save() throws IOException {
        requireCurrent();
        String path = readLine("Sciezka zapisu [data/ciag.txt]: ").trim();
        if (path.isEmpty()) {
            path = "data/ciag.txt";
        }
        BitFile.write(Path.of(path), current);
        System.out.println("Zapisano " + current.length() + " bitow do " + path);
    }

    // obciecie / wybor podciagu (wymog z pkt 2)
    // podciag ZASTEPUJE biezacy ciag. Zeby wrocic do calosci, wczytaj plik jeszcze raz
    private void cut() {
        requireCurrent();
        int offset = readInt("Pozycja startowa (liczona od 0) [0]: ", 0);
        int length = readInt("Dlugosc podciagu [20000]: ", 20000);
        current = BitFile.extract(current, offset, length);
        currentLabel = currentLabel + " (podciag)";
        System.out.println("Biezacy ciag ma teraz " + current.length() + " bitow.");
    }

    // testy FIPS (pkt 2, 5)
    private void runFips() {
        requireCurrent();
        Main.printAll(current);
    }

    // szyfrowanie (pkt 3, 7)
    private void encrypt() {
        String text = readLine("Tekst do zaszyfrowania (Enter = przykladowy tekst 2500 znakow): ");
        if (text.isBlank()) {
            text = "Ala ma kota, a kot ma Ale. ".repeat(100).substring(0, 2500);
        }
        String plainBits = BitConverter.textToBits(text);

        BlumNumber blum = BlumNumber.generate(512, random);
        cipherN = blum.getN();
        cipherA = blum.randomSeed(random);

        lastCipherBits = StreamCipher.encrypt(plainBits, cipherN, cipherA);

        current = lastCipherBits;
        currentLabel = "szyfrogram";
        System.out.println("Zaszyfrowano " + plainBits.length() + " bitow. Szyfrogram jest teraz biezacym ciagiem"
                + (plainBits.length() == 20000 ? " (mozesz uruchomic testy FIPS: opcja 5)."
                : " (do testow FIPS potrzeba dokladnie 20000 bitow = 2500 znakow ASCII)."));
    }

    //  deszyfrowanie
    private void decrypt() {
        if (lastCipherBits == null) {
            throw new IllegalStateException("Brak szyfrogramu - najpierw opcja 6.");
        }
        String bits = StreamCipher.decrypt(lastCipherBits, cipherN, cipherA);
        String text = BitConverter.bitsToText(bits);
        String preview = text.length() > 100 ? text.substring(0, 100) + "..." : text;
        System.out.println("Odszyfrowany tekst: " + preview);
    }


    private void requireCurrent() {
        if (current == null) {
            throw new IllegalStateException("Najpierw wygeneruj (opcja 1) albo wczytaj (opcja 2) ciag.");
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /** Czyta liczbe calkowita; Enter bez wpisywania = wartosc domyslna; zle dane = pytanie od nowa. */
    private int readInt(String prompt, int defaultValue) {
        while (true) {
            String line = readLine(prompt).trim();
            if (line.isEmpty()) {
                return defaultValue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("To nie jest liczba, sprobuj jeszcze raz.");
            }
        }
    }
}