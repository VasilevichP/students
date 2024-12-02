package com.example.demo.Repositories;

import com.example.demo.Entities.Skip;
import org.springframework.data.repository.CrudRepository;

public interface SkipRepository extends CrudRepository<Skip,Long> {
    Iterable<Skip> findAllBySchedule (long schedule);
    Iterable<Skip> findAllByScheduleAndLegimateFalse (long schedule);
    Iterable<Skip> findAllByStudent (long student);
    long countAllByStudent(long student);
    long countAllByStudentAndSchedule(long student, long schedule);
    void deleteAllByStudent(long student);
    void deleteAllBySchedule(long schedule);
}
