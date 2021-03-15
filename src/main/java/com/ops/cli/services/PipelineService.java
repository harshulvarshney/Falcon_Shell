package com.ops.cli.services;

import com.ops.cli.observer.ProgressUpdateEvent;
import com.ops.cli.observer.ProgressUpdateObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Pipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author harshul.varshney
 */
@Slf4j
@Service
public class PipelineService {

    private final GitLabApi gitLabApi;
    private final Map<String, String> projectToIdMap;
    private final ProgressUpdateObserver progressUpdateObserver;

    @Autowired
    public PipelineService(GitLabApi gitLabApi,
                           @Qualifier("projectToIdMap") Map<String, String> projectToIdMap,
                           ProgressUpdateObserver progressUpdateObserver) {
        this.gitLabApi = gitLabApi;
        this.projectToIdMap = projectToIdMap;
        this.progressUpdateObserver = progressUpdateObserver;
    }

    /**
     * fetch pipelines from GitLab for passed project.
     */
    public List<Pipeline> getPipelines(String projectName, int page, int items) throws GitLabApiException {

        if(!projectToIdMap.containsKey(projectName))
            return Collections.emptyList();

        Integer projectId = Integer.parseInt(projectToIdMap.get(projectName));
        return gitLabApi.getPipelineApi().getPipelines(projectId, page, items);
    }

    /**
     * return list of latest pipeline for each of configured project
     */
    public List<Pipeline> getLatestPipelines(List<String> projects) throws GitLabApiException {
        if(CollectionUtils.isEmpty(projects)) {
            projects = new ArrayList<>(projectToIdMap.keySet());
        }

        List<Pipeline> resp = new ArrayList<>();
        for(int i=0; i<projects.size(); i++) {
            String p = projects.get(i);
            Pipeline pipeline = getLatestOfProject(p);

            if(pipeline != null)
                resp.add(pipeline);

            progressUpdateObserver.update(null, new ProgressUpdateEvent(i, projects.size(), "Fetching"));
        }

        return resp;
    }

    public Pipeline getLatestOfProject(String project) throws GitLabApiException {
        Integer id = Integer.parseInt(projectToIdMap.get(project));
        List<Pipeline> pipelines = gitLabApi.getPipelineApi().getPipelines(id, 1, 1);
        if(CollectionUtils.isEmpty(pipelines)) {
            return null;
        }
        return pipelines.get(0);
    }

    public String getProjectName(int index) {
        List<Map.Entry<String, String>> projects = new ArrayList<>(projectToIdMap.entrySet());
        return projects.get(index).getKey();
    }

    public List<Map.Entry<String, String>> projectEntries() {
        return new ArrayList<>(projectToIdMap.entrySet());
    }


}
