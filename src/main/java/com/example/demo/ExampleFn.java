package com.example.demo;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.stereotype.Component;

@Component
public class ExampleFn {
    @FunctionName("echo")
    public HttpResponseMessage upperUser(
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
