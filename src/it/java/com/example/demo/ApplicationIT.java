package com.example.demo;

import com.example.demo.thing.ThingEntity;
import com.example.demo.thing.ThingRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SpringBootTest
@Slf4j
public class ApplicationIT {
    @Autowired
    private ThingRepository repo;

    @Test
    void testRepo() {
        System.out.println("##it");
        repo.save(ThingEntity.builder().name("test").timestamp(new Date().getTime()).build());

        Set<ThingEntity> all = StreamSupport
                .stream(repo.findAll().spliterator(), false)
                .collect(Collectors.toSet());

        log.info("Found entities: {}", all);
    }

}
