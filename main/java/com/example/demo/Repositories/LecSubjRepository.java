package com.example.demo.Repositories;

import com.example.demo.Entities.LecSubj;
import com.example.demo.Entities.LecSubjPK;
import org.springframework.data.repository.CrudRepository;

public interface LecSubjRepository extends CrudRepository<LecSubj, LecSubjPK> {
    Iterable<LecSubj> getLecSubjsByLector(long lector);
    Iterable<LecSubj> getLecSubjsBySubject(String subject);
    void deleteAllByLector(long lector_id);
}
