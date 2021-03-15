package com.ops.cli.commands;

import com.ops.cli.util.ProgressBar;
import com.ops.cli.util.ProgressCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * @author harshul.varshney
 */
//@ShellComponent
public class EchoCommand {

    private final ProgressCounter progressCounter;
    private final ProgressBar progressBar;

    @Autowired
    public EchoCommand(ProgressCounter progressCounter,
                       ProgressBar progressBar) {
        this.progressCounter = progressCounter;
        this.progressBar = progressBar;
    }

    @ShellMethod("Displays greeting message to the user whose name is supplied")
    public String echo(@ShellOption({"-n", "--name"}) String name) {
        return String.format("Hello %s! You are running spring shell: GitLab.", name);
    }

    @ShellMethod("Displays progress spinner")
    public void progressSpinner() throws InterruptedException {
        for (int i = 1; i <=100; i++) {
            progressCounter.display();
            Thread.sleep(100);
        }
        progressCounter.reset();
    }

    @ShellMethod("Displays progress counter (with spinner)")
    public void progressCounter() throws InterruptedException {
        for (int i = 1; i <=100; i++) {
            progressCounter.display(i, "Processing");
            Thread.sleep(100);
        }
        progressCounter.reset();
    }

    @ShellMethod("Displays progress bar")
    public void progressBar() throws InterruptedException {
        for (int i = 1; i <=100; i++) {
            progressBar.display(i);
            Thread.sleep(100);
        }
        progressBar.reset();
    }


}
