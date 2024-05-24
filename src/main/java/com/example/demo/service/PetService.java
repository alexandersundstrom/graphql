package com.example.demo.service;

import com.example.demo.model.Cat;
import com.example.demo.model.Dog;
import com.example.demo.model.Pet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PetService {

    private List<Pet> petsList = new ArrayList<>(
            List.of(new Cat("Molly", "1", 1, true),
                    new Cat("Smulan", "2", 2, false),
                    new Dog("Fido", "1", 3, false)));

    public List<Pet> getAllPets() {
        return petsList;
    }
}
