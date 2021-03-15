package com.ops.cli.commands.gitLab;

import com.ops.cli.util.ShellHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.Map;

/**
 * @author harshul.varshney
 */
@ShellComponent
public class ProjectCommands {

    private final ShellHelper shellHelper;
    private final Map<String, String> projectToIdMap;

    @Autowired
    public ProjectCommands(@Qualifier("projectToIdMap") Map<String, String> projectToIdMap,
                           ShellHelper shellHelper) {
        this.projectToIdMap = projectToIdMap;
        this.shellHelper = shellHelper;
    }

    @ShellMethod("Add a Project & Id to access through GitLab CLI")
    public void add(@ShellOption(help = "Project Name", value = {"-n", "--projectName"}) String project,
                    @ShellOption(help = "Project Id",   value = {"-id", "--projectId"}) Integer projectId) {
        projectToIdMap.put(project, projectId.toString());
    }

    @ShellMethod("List of all available projects with CLI <project-name : project-id>")
    public Map<String, String> list() {
        return projectToIdMap;
    }

    @ShellMethod("Remove a project from CLI configuration")
    public void remove(@ShellOption(help = "Project Name", value = {"-n", "--projectName"}) String project) {
        if(projectToIdMap.remove(project) != null)
            shellHelper.printSuccess("Project Removed");
        else
            shellHelper.printError("project not found in configuration");
    }

}
