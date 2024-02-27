# Notes
This is an experimental setup for running integration tests for Azure Function in a development environment that lacks Docker (don't ask...).

## todo!
- [x] add an http trigger to function app (eg echo)
- [x] start function app locally with `azure-functions:run`
- [x] make request manually w/ httpie
- [x] programmatically start function app for tests
  - in pre-integration test phase
  - ~~in integration test phase with pre-class hook~~
- [x] add spring data repository 

---

- [ ] make an http request to function app from integration test suite
- [ ] add event grid trigger to function app
- [ ] invoke function app via event grid trigger with http request
- [ ] use spring data repo in test code
- [ ] programmatically stop function app 
  - in post-integration test phase
  - in integration test phase with post-class hook
  - maybe: `<asyncDestroyOnShutdown>true</asyncDestroyOnShutdown>`

notes:
- manually run non-http trigger function app: https://learn.microsoft.com/en-us/azure/azure-functions/functions-manually-run-non-http?tabs=azure-portal
- 

## Project Skeleton
- [spring starter](https://start.spring.io) with dependencies:
  - Lombok
  - Spring Cloud Function
  - Azure Support
  - ~~Azure Cosmos DB~~
    - there is no cosmosdb emulator available for M1 mac :/

## Azure Function App dependencies
Add Azure Adapter dependency
```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-function-adapter-azure</artifactId>
    </dependency>
```

Add Azure Functions Maven plugin:
```xml
    <plugin>
        <groupId>com.microsoft.azure</groupId>
        <artifactId>azure-functions-maven-plugin</artifactId>
        <version>1.22.0</version>

        <configuration>
            <appName>arbitrary-app-name</appName>
            <resourceGroup>YOUR-AZURE-FUNCTION-RESOURCE-GROUP</resourceGroup>
            <region>YOUR-AZURE-FUNCTION-APP-REGION</region>
            <appServicePlanName>YOUR-AZURE-FUNCTION-APP-SERVICE-PLANE-NAME</appServicePlanName>
            <pricingTier>YOUR-AZURE-FUNCTION-PRICING-TIER</pricingTier>

            <hostJson>${project.basedir}/src/main/resources/host.json</hostJson>

            <runtime>
                <os>linux</os>
                <javaVersion>11</javaVersion>
            </runtime>

            <appSettings>
                <property>
                    <name>FUNCTIONS_EXTENSION_VERSION</name>
                    <value>~4</value>
                </property>
            </appSettings>
        </configuration>
        <executions>
            <execution>
                <id>package-functions</id>
                <goals>
                    <goal>package</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
```

## Integration Test setup
see [Failsafe Plugin documentation](https://maven.apache.org/surefire/maven-failsafe-plugin/)

### integration test directory
- add integration tests in separate source folder: `src/it/test`
- add integration test directory to sources with `build-helper-maven-plugin`:
```xml
  <plugin>
      <groupId>org.codehaus.mojo</groupId>
      <artifactId>build-helper-maven-plugin</artifactId>
      <version>3.5.0</version>
      <executions>
          <execution>
              <id>add-test-source</id>
              <phase>generate-sources</phase>
              <goals>
                  <goal>add-test-source</goal>
              </goals>
              <configuration>
                  <sources>
                      <source>src/it/java</source>
                  </sources>
              </configuration>
          </execution>
      </executions>
  </plugin>
```

### Failsafe Plugin
Add Failsafe Maven plugin:
```xml
    <plugin>
        <artifactId>maven-failsafe-plugin</artifactId>
        <version>3.2.5</version>
        <executions>
            <execution>
                <configuration>
                      <testSourceDirectory>src/it/java</testSourceDirectory>
                      <includes>
                          <include>**/*IT.java</include>
                      </includes>
                </configuration>
                <goals>
                      <goal>integration-test</goal>
                      <goal>verify</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
```

**NB!**
- pre-integration-test phase should set up dependencies
- post-integration-test phase should tear down dependencies
- verify checks the outcome of the integration tests

## Reports

> The Failsafe Plugin generates reports in two different file formats:

- Plain text files (*.txt)
- XML files (*.xml)

>By default, these files are generated in `${basedir}/target/failsafe-reports/TEST-*.xml.`
The schema for the Failsafe XML reports is available at Failsafe XML Report Schema.
For an HTML format of the report, please see the Maven Surefire Report Plugin.
By default this plugin generates summary XML file at ${basedir}/target/failsafe-reports/failsafe-summary.xml and the schema is available at Failsafe XML Summary Schema.

# Test Run Function App Locally

1. run function app with `./mvnw package azure-functions:run`
2. make test http request to function app: `http POST 'http://localhost:7071/api/echo' --raw foobarblablup`

response:
```
HTTP/1.1 200 OK
Content-Type: text/plain;charset=UTF-8
Date: Fri, 09 Feb 2024 14:15:28 GMT
Server: Kestrel
Transfer-Encoding: chunked

foobarblablup
```

## Caveats
### mvn package
will not run without package, azure functions plugin executes from local build cache

### Main Class
must declare main class, eg. with pom property:
```xml
	<properties>
		<start-class>com.example.demo.DemoApplication</start-class>
	</properties>
```

Otherwise receive error when making request to started app: 
> java.lang.IllegalStateException: Failed to discover main class. An attempt was made to discover main class as 'MAIN_CLASS' environment variable, system property as well as entry in META-INF/MANIFEST.MF

And receive 500 response to http call

### Packaging
Spring Boot Maven Plugin packaging is incompatible, add additional dependency:

```xml
  <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
      <dependencies>
          <dependency>
              <groupId>org.springframework.boot.experimental</groupId>
              <artifactId>spring-boot-thin-layout</artifactId>
              <version>1.0.31.RELEASE</version>
          </dependency>
      </dependencies>
  </plugin>
```

---

<build>
    <plugins>
        <!-- Other plugins and configurations -->
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>3.0.0</version>
            <executions>
                <execution>
                    <id>start-web-app</id>
                    <phase>pre-integration-test</phase>
                    <goals>
                        <goal>exec</goal>
                    </goals>
                    <configuration>
                        <executable>java</executable>
                        <arguments>
                            <argument>-jar</argument>
                            <argument>path/to/your/web-app.jar</argument>
                            <!-- Add other configuration options as needed -->
                        </arguments>
                        <detach>true</detach>
                    </configuration>
                </execution>
                <!-- Add more executions as needed -->
            </executions>
        </plugin>
    </plugins>
</build>

## Embedded H2 db
- https://www.baeldung.com/spring-boot-h2-database
- https://docs.spring.io/spring-boot/docs/current/reference/html/data.html
- 
