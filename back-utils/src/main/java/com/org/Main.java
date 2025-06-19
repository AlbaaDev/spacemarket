package com.org;

import static com.org.Geeks.validate;

public class Main {
    public static void main(String[] args) {
        try {
            validate(17);
        } catch (InvalidAgeException e) {
            System.out.println("Caught Exception: " + e.getMessage());
        }
    }
}