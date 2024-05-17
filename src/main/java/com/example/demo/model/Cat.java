package com.example.demo.model;

public record Cat(String name, String ownerId, Integer id, boolean doesMeow) implements Pet {

}
