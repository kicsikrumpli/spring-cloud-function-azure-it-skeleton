package com.example.demo.thing;

import org.springframework.data.repository.CrudRepository;

public interface ThingRepository extends CrudRepository<ThingEntity, Long> {
}
