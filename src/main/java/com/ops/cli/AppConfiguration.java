package com.ops.cli;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ops.cli.util.ProgressBar;
import com.ops.cli.util.ProgressCounter;
import com.ops.cli.util.ShellHelper;
import org.gitlab4j.api.GitLabApi;
import org.jline.terminal.Terminal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author harshul.varshney
 */
@Configuration
public class AppConfiguration implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public GitLabApi gitLabApi() {
        GitLabApi gitLabApi = new GitLabApi(
                environment.getRequiredProperty("gitlab.server.url"),
                environment.getRequiredProperty("gitlab.personal.token")
        );

        // To turn off sudo mode
        gitLabApi.unsudo();

        // Set the connect timeout to 1 second and the read timeout to 5 seconds
        gitLabApi.setRequestTimeout(1000, 5000);

        // Log using the shared logger and default level of FINE
        gitLabApi.enableRequestResponseLogging();

        return gitLabApi;
    }

    @Bean("projectToIdMap")
    @ConfigurationProperties(prefix="gitlab.project")
    public Map<String, String> projectNameIdMap() {
        return new HashMap<>();
    }

    @Bean
    public ShellHelper shellHelper(@Lazy Terminal terminal) {
        return new ShellHelper(terminal);
    }

    @Bean
    public ProgressCounter progressCounter(@Lazy Terminal terminal) {
        return new ProgressCounter(terminal);
    }

    @Bean
    public ProgressBar progressBar(ShellHelper shellHelper) {
        return new ProgressBar(shellHelper);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

}
