package com.example.demo.Repositories;

import com.example.demo.Entities.Secretary;
import org.springframework.data.repository.CrudRepository;

public interface SecretaryRepositary extends CrudRepository<Secretary,String> {
    Iterable<Secretary> getAllByStatus(int status);
    int countAllByStatus(int status);
}
