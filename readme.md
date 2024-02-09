# Notes

## todo!
- add integration test folder to test sources: build-helper-maven-plugin
- exclude integration test folder for surefire plugin
- include integration test folder for failsafe plugin
  - NB! source patterns: 
    - `IT*.java`, 
    - `*IT.java`, 
    - `*ITCase.java`

## Project Skeleton
- [spring starter](https://start.spring.io) with dependencies:
  - Lombok
  - Spring Cloud Function
  - Azure Support
  - Azure Cosmos DB

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

> when running integration tests, you should invoke Maven with the (shorter to type too) `mvn verify` rather than trying to invoke the integration-test phase directly, as otherwise the post-integration-test phase will not be executed.

Add Failsafe Maven plugin:
```xml
    <plugin>
        <artifactId>maven-failsafe-plugin</artifactId>
        <version>3.2.5</version>
        <executions>
            <execution>
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


