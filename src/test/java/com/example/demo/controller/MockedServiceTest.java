package com.example.demo.controller;

import com.example.demo.model.Cat;
import com.example.demo.model.Dog;
import com.example.demo.service.PersonService;
import com.example.demo.service.PetService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;

@GraphQlTest(PetsController.class) //Only test the GraphQL layer itself, which the service layer doesn't belong to
public class MockedServiceTest {

   @Autowired
    GraphQlTester graphQlTester;
   @MockBean
    PetService petService;
    @MockBean
    PersonService personService;

   @Test
    void testPets() {
       Mockito.when(petService.getAllPets())
               .thenReturn(List.of(new Cat("Test cat", "3", 1, true),
                       new Dog("Test dog", "4", 2, false)));

       graphQlTester.document("query myPets  { pets { name }}")
               .execute()
               .path("pets[*].name")
               .entityList(String.class)
               .isEqualTo(List.of("Test cat", "Test dog"));
   }
}
