package com.example.demo.service;

import com.example.demo.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {

    private List<Person> owners = new ArrayList<>(
            List.of(new Person("1", "Alexander", "Sundström", "2"),
                    new Person("2", "Anna", "Sundström", "1")));

    public List<Person> getAllPersons() {
        return owners;
    }
}
