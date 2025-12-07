package com.example;

public class InheritanceExample extends BaseClass {
    private String field;

    public void method() {
        field = "test";
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
