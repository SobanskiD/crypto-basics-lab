package org.example;

import org.example.randomstream.BbsGenerator;

import java.math.BigInteger;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        BbsGenerator gen = new BbsGenerator(
                BigInteger.valueOf(77),
                BigInteger.valueOf(3));

        System.out.println(gen.nextBits(8));
    }
}