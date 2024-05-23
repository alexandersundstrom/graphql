package com.example.demo.controller;

import com.example.demo.model.*;
import graphql.GraphQLContext;
import graphql.execution.directives.QueryAppliedDirective;
import graphql.execution.directives.QueryAppliedDirectiveArgument;
import graphql.execution.directives.QueryDirectives;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class PetsController {

    private List<Pet> petsList = new ArrayList<>(
            List.of(new Cat("Molly", "1", 1, true),
                    new Cat("Smulan", "2", 2, false),
                    new Dog("Fido", "1", 3, false)));

    private List<Person> owners = new ArrayList<>(
            List.of(new Person("1", "Alexander", "Sundström"),
                    new Person("2", "Anna", "Sundström")));

    @QueryMapping
    public List<Pet> pets(GraphQLContext context, @ContextValue String accessKey, DataFetchingEnvironment env) {
        QueryDirectives queryDirectives= env.getQueryDirectives();
        List<QueryAppliedDirective> cacheDirectives = queryDirectives.getImmediateAppliedDirective("cache");
        if (cacheDirectives.size() > 0) {
            QueryAppliedDirective cache = cacheDirectives.get(0);
            QueryAppliedDirectiveArgument maxArgument = cache.getArgument("maxAge");
            int maxAge = maxArgument.getValue();
        }

        return petsList;
    }

    @QueryMapping
    public Pet pet(@Argument Integer id) {
        return petsList.stream()
                .filter(pet -> pet.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    @QueryMapping
    public List<Pet> findPets(@Argument SearchFilter filter) {
        return petsList.stream()
                .filter(pet -> (Objects.isNull(filter.name()) || filter.name().equals(pet.name())) &&
                        (Objects.isNull(filter.ownerId()) || filter.ownerId().equals(pet.ownerId())))
                .toList();
    }

    @MutationMapping
    public PetPayload changePetName(@Argument Integer id, @Argument String newName) {
        Pet petById = petsList.stream()
                .filter(pet -> pet.id().equals(id))
                .findFirst()
                .orElseThrow();
        Cat updatedPet = new Cat(newName, petById.ownerId(), petById.id(), true);
        petsList.add(petsList.indexOf(petById), updatedPet);
        petsList.remove(petById);

        return new PetPayload(updatedPet);
    }

    @SubscriptionMapping
    Flux<String> hello() {
        Flux<Integer> interval = Flux.fromIterable(List.of(0,1,2))
                .delayElements(Duration.ofSeconds(1));
        return interval.map(integer -> "Hello"  + integer);
    }

    @SchemaMapping(typeName = "Dog", field = "owner")
    public Person owner(Dog dog) {
        return owners.stream()
                .filter(owner -> dog.ownerId().equals(owner.id()))
                .findAny()
                .orElse(null);
    }

    @SchemaMapping(typeName = "Cat", field = "owner")
    public Person owner(Cat cat) {
        return owners.stream()
                .filter(owner -> cat.ownerId().equals(owner.id()))
                .findAny()
                .orElse(null);
    }
}
