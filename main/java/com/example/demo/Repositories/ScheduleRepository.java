package com.example.demo.Repositories;

import com.example.demo.Entities.Schedule;
import com.example.demo.Entities.SchedulePK;
import org.springframework.data.repository.CrudRepository;

public interface ScheduleRepository extends CrudRepository<Schedule, SchedulePK> {
    long countByGroupnumberAndDay(long groupnumber, int day);
    long countByLectorAndDay(long lector, int day);
    Iterable<Schedule> getSchedulesByGroupnumber(long groupnumber);
    Iterable<Schedule> getSchedulesByLector(long lector);
    Iterable<Schedule> getSchedulesByLectorAndGroupnumberAndSubject(long lector,long groupnumber,String subject);
}
