package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.PersonService;
import com.example.demo.service.PetService;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static com.example.demo.util.DirectiveUtil.getDirective;

@Controller
public class PetsController {

   private final PersonService personService;
   private final PetService petService;

    public PetsController(PersonService personService, PetService petService) {
        this.personService = personService;
        this.petService = petService;
    }

    @QueryMapping
    public List<Pet> pets() {
        return petService.getAllPets();
    }

    //Not used, but shows something you can get automatically, as well as how to get the directives
    @QueryMapping
    public List<Pet> petsWithExtra(GraphQLContext context, @ContextValue String accessKey, DataFetchingEnvironment env) {
        Integer cache = getDirective(env, "cache", "maxAge");
        String instruction = getDirective(env, "instruction", "instruction");
        return petService.getAllPets();
    }

    @QueryMapping
    public Pet pet(@Argument Integer id) {
        return petService.getAllPets().stream()
                .filter(pet -> pet.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    @QueryMapping
    public List<Pet> findPets(@Argument SearchFilter filter) {
        return petService.getAllPets().stream()
                .filter(pet -> (Objects.isNull(filter.name()) || filter.name().equals(pet.name())) &&
                        (Objects.isNull(filter.ownerId()) || filter.ownerId().equals(pet.ownerId())))
                .toList();
    }

    @MutationMapping
    public PetPayload changePetName(@Argument Integer id, @Argument String newName) {
        Pet petById = petService.getAllPets().stream()
                .filter(pet -> pet.id().equals(id))
                .findFirst()
                .orElseThrow();
        Cat updatedPet = new Cat(newName, petById.ownerId(), petById.id(), true);
        petService.getAllPets().add(petService.getAllPets().indexOf(petById), updatedPet);
        petService.getAllPets().remove(petById);

        return new PetPayload(updatedPet);
    }

    @SubscriptionMapping
    Flux<String> hello() {
        Flux<Integer> interval = Flux.fromIterable(List.of(0, 1, 2))
                .delayElements(Duration.ofSeconds(1));
        return interval.map(integer -> "Hello" + integer);
    }

    @SchemaMapping(typeName = "Dog", field = "owner")
    public Person owner(Dog dog) {
        return personService.getAllPersons().stream()
                .filter(owner -> dog.ownerId().equals(owner.id()))
                .findAny()
                .orElse(null);
    }

    @SchemaMapping(typeName = "Cat", field = "owner")
    public Person owner(Cat cat) {
        return personService.getAllPersons().stream()
                .filter(owner -> cat.ownerId().equals(owner.id()))
                .findAny()
                .orElse(null);
    }

    @BatchMapping
    List<Person> bestFriend(List<Person> peoples) {
       return peoples.stream()
            .map(people -> personService.getAllPersons().stream()
                    .filter(owner -> people.bestFriendId().equals(owner.id()))
                    .findFirst()
                    .orElseThrow(() ->new RuntimeException("Person could not be found!")))
                .toList();
    }
}
