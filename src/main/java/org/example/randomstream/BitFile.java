package org.example.randomstream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Zadanie pkt 2 - wczytywanie ciągów (0/1) z plików .txt i sprawdzanie ich długości
 * (informacja o zbyt krotkim ciagu, obciecie / wybranie podciagu - extract).
 */
public final class BitFile {

    private BitFile(){}

    /**
     * Wczytanie ciagu bitow z pliku
     * @param path
     * @return
     * @throws IOException
     */
    public static String read(Path path) throws IOException
    {
        String raw = Files.readString(path);
        String bits = raw.replaceAll("[\\s\\uFEFF]+", "");

        for (int i = 0; i < bits.length(); i++) {
            char c = bits.charAt(i);
            if (c != '0' && c != '1') {
                throw new IllegalArgumentException(
                        "Niedozwolony znak '" + c + "' na pozycji " + (i + 1)
                                + " (dozwolone tylko 0 i 1)");
            }
        }
        return bits;
    }

    /**
     * Zapis ciagu bitów do pliku
     * @param path
     * @param bits
     * @throws IOException
     */
    public static void write(Path path, String bits) throws IOException {
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(path, bits);
    }


    public static String extract(String bits, int offset, int length) {
        if (offset < 0 || length < 0) {
            throw new IllegalArgumentException("offset i length nie moga byc ujemne");
        }
        if (offset + length > bits.length()) {
            throw new IllegalArgumentException(
                    "Ciag za krotki: potrzeba " + length + " bitow od pozycji "
                            + offset + ", a ciag ma tylko " + bits.length() + " bitow");
        }
        return bits.substring(offset, offset + length);
    }
}
