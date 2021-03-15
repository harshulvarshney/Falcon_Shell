package com.ops.cli.controller;

import com.ops.cli.services.PipelineService;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Pipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class GitlabApiTester {

    private final PipelineService pipelineService;
    private final Map<String, String> projectToIdMap;

    @Autowired
    public GitlabApiTester(PipelineService pipelineService,
                           @Qualifier("projectToIdMap") Map<String, String> projectToIdMap) {
        this.pipelineService = pipelineService;
        this.projectToIdMap = projectToIdMap;
    }


    @GetMapping(value = "/{projectName}/pipelines")
    public List<Pipeline> getPipelnes(@PathVariable("projectName") String projectName) throws GitLabApiException {
        return pipelineService.getPipelines(projectName, 1, 10);
    }
}
