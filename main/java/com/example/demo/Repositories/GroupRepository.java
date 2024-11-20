package com.example.demo.Repositories;

import com.example.demo.Entities.StudGroup;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<StudGroup,Long> {
    Iterable<StudGroup> findAllBySpecialty(String specialty);
}
