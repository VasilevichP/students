package com.example.demo.Repositories;

import com.example.demo.Entities.Mark;
import org.springframework.data.repository.CrudRepository;

public interface MarkRepository extends CrudRepository<Mark,Long> {
    Iterable<Mark> findAllBySchedule(long schedule);
    long countAllByStudent(long student);
    Iterable<Mark> findAllByStudent(long student);
    Iterable<Mark> findAllByStudentAndSchedule(long student,long schedule);
    void deleteAllByStudent(long student);
    void deleteAllBySchedule(long schedule);
}
