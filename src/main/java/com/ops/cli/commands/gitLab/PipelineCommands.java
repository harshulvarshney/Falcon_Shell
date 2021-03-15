package com.ops.cli.commands.gitLab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.ops.cli.services.PipelineService;
import com.ops.cli.util.BeanNameMapper;
import com.ops.cli.util.BeanTableModelBuilder;
import com.ops.cli.util.ShellHelper;
import org.apache.commons.lang3.StringUtils;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Pipeline;
import org.gitlab4j.api.models.Project;
import org.jline.reader.LineReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author harshul.varshney
 */
@ShellComponent
public class PipelineCommands {

    private final PipelineService pipelineService;
    private final ShellHelper shellHelper;
    private final ObjectMapper objectMapper;
    @Autowired @Lazy private LineReader reader;

    @Autowired
    public PipelineCommands(PipelineService pipelineService,
                            ShellHelper shellHelper,
                            ObjectMapper objectMapper) {
        this.pipelineService = pipelineService;
        this.shellHelper = shellHelper;
        this.objectMapper = objectMapper;
    }

    @ShellMethod("Get pipelines for project")
    public void pipelines(@ShellOption(help = "Project Name", value = {"-n", "--projectName"}) String project,
                          @ShellOption(help = "Page Number", value = {"-p", "--page"}) int page,
                          @ShellOption(help = "Num of pipelines per page", value = {"-i", "--itemsPerPage"}) int items) {

        List<Pipeline> pipelines = null;
        try {
            pipelines = pipelineService.getPipelines(project, page, items);
        } catch (GitLabApiException e) {
            shellHelper.printError(e.getMessage());
            e.printStackTrace();
            return;
        }

        assert pipelines != null;
        TableModel model = new BeanListTableModel<>(pipelines, "id", "status", "sha", "user");

        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addInnerBorder(BorderStyle.oldschool);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        shellHelper.print(tableBuilder.build().render(100));
    }

    @ShellMethod("Get latest pipeline from all available projects")
    public List<Pipeline> latestAll() {
        List<Pipeline> pipelines = null;
        try {
            pipelines = pipelineService.getLatestPipelines(null);
        } catch (GitLabApiException e) {
            shellHelper.printError(e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
        return pipelines;
    }

    @ShellMethod("Get latest pipeline from all available projects")
    public void latest(@ShellOption(defaultValue = "", value = {"-n", "--projectName"}) String project,
                       @ShellOption(defaultValue = "-1", value = {"-i", "--indexPosition"}) String index) {

        project = getProjectName(project, index);
        if(StringUtils.isEmpty(project))
            shellHelper.printError("projet not found");

        shellHelper.printInfo("fetching latest pipeline for :: " + project);
        Pipeline p = null;
        try {
            p = pipelineService.getLatestOfProject(project);
        } catch (GitLabApiException e) {
            shellHelper.printError(e.getMessage());
            e.printStackTrace();
        }
        if (p == null) {
            shellHelper.printWarning("No pipeline found for supplied project name");
            return;
        }
        displaySinglePipeline(p);
    }

    private String getProjectName(String project, String index) {
        Integer in = Integer.valueOf(index);
        if(StringUtils.isEmpty(project) && in > -1) {//index based search
            return pipelineService.projectEntries().get(in).getKey();
        }
        else if(StringUtils.isEmpty(project) && in == -1) {//show options
            String question = "Please select the project from below list, type project number and hit Enter";
            displayProjects();
            // Get Input
            int input = Integer.parseInt(this.ask(question));
            return pipelineService.getProjectName(input);
        }
        return project;
    }

    public String ask(String question) {
        return this.reader.readLine("\n" + question + " > ");
    }


    private void displaySinglePipeline(Pipeline pipeline) {
        LinkedHashMap<String, Object> labels = BeanNameMapper.getPipelineNameMap();

        String[] header = new String[] {"Property", "Value"};
        BeanTableModelBuilder builder = new BeanTableModelBuilder(pipeline, objectMapper);
        TableModel model = builder.withLabels(labels).withHeader(header).build();

        TableBuilder tableBuilder = new TableBuilder(model);

        tableBuilder.addInnerBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        tableBuilder.on(CellMatchers.column(0)).addSizer(new AbsoluteWidthSizeConstraints(10));
        tableBuilder.on(CellMatchers.column(1)).addSizer(new AbsoluteWidthSizeConstraints(90));
        shellHelper.print(tableBuilder.build().render(120));
    }

    private void displayProjects() {

        List<Project> projects = Lists.newArrayList();
        List<Map.Entry<String, String>> entries = pipelineService.projectEntries();
        for(int i=0; i<entries.size(); i++) {
            Map.Entry<String, String> e = entries.get(i);
            Project p = new Project();
            p.setStarCount(i);
            p.setId(Integer.parseInt(e.getValue()));
            p.setName(e.getKey());
            projects.add(p);
        }

        TableModel model = new BeanListTableModel<>(projects, "starCount", "id", "name");

        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addInnerBorder(BorderStyle.fancy_double);
        tableBuilder.addHeaderBorder(BorderStyle.fancy_double);
        tableBuilder.on(CellMatchers.column(0)).addSizer(new AbsoluteWidthSizeConstraints(10));
        tableBuilder.on(CellMatchers.column(1)).addSizer(new AbsoluteWidthSizeConstraints(20));
        tableBuilder.on(CellMatchers.column(2)).addSizer(new AbsoluteWidthSizeConstraints(70));
        shellHelper.print(tableBuilder.build().render(100));

    }

}
