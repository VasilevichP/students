package com.example.demo.Repositories;

import com.example.demo.Entities.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student,Long> {
    Optional<Student> findByLogin(String login);
    boolean existsByEmail(String email);
    boolean existsByName(String name);
    boolean existsByPassport(String passport);
    boolean existsByPhone(String phone);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByName(String name);
    Optional<Student> findByPassport(String passport);

    Optional<Student> findByPhone(String phone);

    boolean existsBylogin(String login);
    Iterable<Student> findAllByGroupnumber(long groupnumber);

    void deleteAllByGroupnumber(long groupnumber);
}
