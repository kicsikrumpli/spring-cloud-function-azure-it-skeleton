package com.example.demo;

import com.example.demo.thing.ThingEntity;
import com.example.demo.thing.ThingRepository;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class ExampleFn {
    private final ThingRepository repo;

    public ExampleFn(ThingRepository repo) {
        this.repo = repo;
    }

    @FunctionName("save_and_find_all")
    public HttpResponseMessage findAll(
            @HttpTrigger(
                    name = "req",
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    methods = {HttpMethod.POST}
            ) HttpRequestMessage<String> request,
            ExecutionContext context
    ) {
        String str = request.getBody();
        context.getLogger().info("Received request: " + str);

        if (Strings.isNotEmpty(str)) {
            ThingEntity savedThing = repo.save(
                    ThingEntity.builder()
                            .timestamp(new Date().getTime()).name(str)
                            .build()
            );
            context.getLogger().info("Saved entity: " + savedThing);
        } else {
            context.getLogger().info("Empty payload, skip save");
        }

        Iterable<ThingEntity> things = repo.findAll();
        Set<ThingEntity> thingsCollection = StreamSupport
                .stream(things.spliterator(), false)
                .collect(Collectors.toSet());

        return request
                .createResponseBuilder(HttpStatus.OK)
                .body(thingsCollection)
                .header("Content-Type", "application/json")
                .build();
    }

    @FunctionName("echo")
    public HttpResponseMessage echo(
            @HttpTrigger(
                    name = "req",
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    methods = {HttpMethod.GET, HttpMethod.POST}
            ) HttpRequestMessage<String> request,
            ExecutionContext context
    ) {
        String str = request.getBody();

        context.getLogger().info("Received request: " + str);

        return request.createResponseBuilder(HttpStatus.OK)
                .body(str)
                .header("Content-Type", "text/plain;charset=UTF-8")
                .build();
    }
}
