package com.example.demo.Repositories;

import com.example.demo.Entities.LecGroup;
import com.example.demo.Entities.LecGroupPK;
import org.springframework.data.repository.CrudRepository;

public interface LecGroupRepository extends CrudRepository<LecGroup, LecGroupPK> {
    Iterable<LecGroup> getLecGroupsByLector(long lector_id);
    Iterable<LecGroup> getLecGroupsByGrp(long grp);
    void deleteAllByLector(long lector_id);
}
