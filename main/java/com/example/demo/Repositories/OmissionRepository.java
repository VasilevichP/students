package com.example.demo.Repositories;

import com.example.demo.Entities.Omission;
import org.springframework.data.repository.CrudRepository;

public interface OmissionRepository extends CrudRepository<Omission, Long> {
    Iterable<Omission> findAllByStudent(long student);
    Iterable<Omission> findAllByStudentAndStatus(long student,int status);
    Iterable<Omission> findAllByStatus(int status);
    long countAllByStudent(long student);
    void deleteAllByStudent(long student);
}
