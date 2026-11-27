# crypto-basics

Laboratorium z przedmiotu **Podstawy Kryptografii** (PUT), moduł 1:
**Badanie własności pseudolosowych ciągów binarnych**.

Projekt: generuje bity generatorem **BBS**, bada ich losowość
testami **FIPS 140-2** i **NIST**, a potem używa ich jako klucza w **szyfrze strumieniowym**.

---


## 1. Struktura

```
src/main/java/org/example/
├── App.java            menu aplikacji (pokaz, pliki zewnętrzne)
├── Main.java           3 ciągi BBS + FIPS (+ printAll)
├── CipherDemo.java     szyfr strumieniowy: demonstracja
├── MillionBits.java    ciąg 1 000 000 bitów
└── randomstream/
    ├── BbsGenerator.java   generator bitów BBS
    ├── BlumNumber.java     losowanie p, q (≡ 3 mod 4), n = p·q, ziarna a
    ├── BitFile.java        odczyt/zapis plików 0/1, wycinanie podciągu
    ├── BitConverter.java   tekst <-> bity (UTF-8, 8 bitów na bajt)
    ├── FipsTests.java      4 testy FIPS 140-2
    ├── TestResult.java     wynik testu: nazwa, statystyki, TAK/NIE
    └── StreamCipher.java   szyfr strumieniowy (XOR z kluczem z BBS)
```

---


### 2. Losowość i pseudolosowość

- Komputer nie rzuca monetą, tylko liczy. Generator **pseudolosowy** z ziarna (seed) deterministycznie
  produkuje długi ciąg, który ma *wyglądać* losowo.
- Dobry ciąg: zer i jedynek po równo, brak widocznych wzorów, serie zgodne z rozkładem losowym,
  długi **okres** (zanim się powtórzy).
- **Testy statystyczne**  zakładamy "ciąg jest losowy", liczymy statystykę i sprawdzamy,
  czy mieści się w przedziale typowym dla ciągów losowych.
- Zdanie testu **nie dowodzi** losowości, test wykrywa
  konkretną wadę, dlatego używa się kilku. Prawdziwie losowy ciąg oblewa pojedynczy test FIPS
  z prawdopodobieństwem rzędu 10⁻⁴.

### 3. Generator BBS (Blum–Blum–Shub)

- **Liczba Bluma:** `n = p·q`, gdzie `p`, `q` są pierwsze i `p mod 4 = 3`, `q mod 4 = 3`.
- Algorytm: `x₀ = a² mod n` (a losowe,