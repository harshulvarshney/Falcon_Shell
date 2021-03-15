package com.ops.cli.util;

import java.util.LinkedHashMap;

public class BeanNameMapper {

    private BeanNameMapper(){}

    public static LinkedHashMap<String, Object> getPipelineNameMap() {
        LinkedHashMap<String, Object> labels = new LinkedHashMap<>();
        labels.put("id", "Id");
        labels.put("status", "Status");
        labels.put("ref", "Branch");
        labels.put("sha", "sha");
        labels.put("beforeSha", "BeforeSha");
        labels.put("tag", "Tag");
        labels.put("yamlErrors", "Errors");
        labels.put("user", "User");
        labels.put("createdAt", "CreatedAt");
        labels.put("updatedAt", "UpdatedAt");
        labels.put("startedAt", "StartedAt");
        labels.put("finishedAt", "CompletedAt");
        labels.put("committedAt", "CommittedAt");
        labels.put("coverage", "Coverage");
        labels.put("duration", "Duration");
        labels.put("webUrl", "URL");
        labels.put("detailedStatus", "DetailedStatus");
        return labels;
    }
}
