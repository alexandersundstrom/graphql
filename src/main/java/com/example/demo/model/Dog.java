package com.example.demo.model;

public record Dog(String name, String ownerId, Integer id, boolean doesBark) implements Pet {

}
