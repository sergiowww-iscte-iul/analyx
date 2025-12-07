package com.example;

public class ComplexClass {
    private int value;

    public int complexMethod(int input) {
        if (input > 0) {
            for (int i = 0; i < input; i++) {
                while (value < 10) {
                    value++;
                    if (value % 2 == 0) {
                        break;
                    }
                }
            }
        } else {
            try {
                value = input;
            } catch (Exception e) {
                return -1;
            }
        }
        return value > 5 ? 1 : 0;
    }

    public void anotherMethod() {
        value = 0;
    }

    public int getValue() {
        return value;
    }
}
