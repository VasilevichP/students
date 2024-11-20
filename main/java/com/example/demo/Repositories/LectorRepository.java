package com.example.demo.Repositories;

import com.example.demo.Entities.Lector;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LectorRepository extends CrudRepository<Lector,Long> {

    Optional<Lector> findByLogin(String login);
    boolean existsByEmail(String email);
    boolean existsByName(String name);
    Optional<Lector> findByName(String name);
    Optional<Lector> findByEmail(String email);

    boolean existsByLogin(String login);
}
