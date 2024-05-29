package com.modules.base.jpa;

import com.modules.base.domain.AggregateRoot;
import com.modules.base.domain.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class BaseRepository <AR extends AggregateRoot<AR, ARID>, ARID, R extends JpaRepository<AR, ARID>> implements Repository<AR, ARID> {
    protected final R repository;

    public BaseRepository(R repository) {
        this.repository = repository;
    }

    public void add (AR entity){
        repository.save(entity);
    }

    public AR find(ARID id){
        return repository.findById(id).orElse(null);
    }


}
