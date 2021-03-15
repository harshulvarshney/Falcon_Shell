package com.ops.cli;

import lombok.extern.slf4j.Slf4j;
import org.fusesource.jansi.AnsiConsole;
import org.jline.console.SystemRegistry;
import org.jline.console.impl.Builtins;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.keymap.KeyMap;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.widget.TailTipWidgets;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;
import picocli.shell.jline3.PicocliCommands;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * @author harshul.varshney
 *
 */
@Slf4j
@SpringBootApplication
public class CliApplication {

	public static void main(String[] args) {
		log.info("Starting Application:::::::");
		term();
		SpringApplication.run(CliApplication.class, args);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.info("@@@@@@@@@@@@@@@@@@@@@@@@@ APP IS SHUTTING DOWN @@@@@@@@@@@@@@@@@@@@@@@@@");
			}
		});
	}

	private static Path workDir() {
		return Paths.get(System.getProperty("user.dir"));
	}

	private static void term() {
		AnsiConsole.systemInstall();
		try {
			// set up JLine built-in commands
			Builtins builtins = new Builtins(CliApplication::workDir, null, null);
			builtins.rename(Builtins.Command.TTOP, "top");
			builtins.alias("zle", "widget");
			builtins.alias("bindkey", "keymap");
			// set up picocli commands
			CliCommands commands = new CliCommands();
			CommandLine cmd = new CommandLine(commands);
			PicocliCommands picocliCommands = new PicocliCommands(CliApplication::workDir, cmd);

			Parser parser = new DefaultParser();
			try (Terminal terminal = TerminalBuilder.builder().build()) {
				SystemRegistry systemRegistry = new SystemRegistryImpl(parser, terminal, CliApplication::workDir, null);
				systemRegistry.setCommandRegistries(builtins, picocliCommands);

				LineReader reader = LineReaderBuilder.builder()
						.terminal(terminal)
						.completer(systemRegistry.completer())
						.parser(parser)
						.variable(LineReader.LIST_MAX, 50)   // max tab completion candidates
						.build();
				builtins.setLineReader(reader);
				commands.setReader(reader);
				new TailTipWidgets(reader, systemRegistry::commandDescription, 5, TailTipWidgets.TipType.COMPLETER.COMPLETER);
				KeyMap<Binding> keyMap = reader.getKeyMaps().get("main");
				keyMap.bind(new Reference("tailtip-toggle"), KeyMap.alt("s"));

				String prompt = "FALCON> ";
				String rightPrompt = null;

				// start the shell and process input until the user quits with Ctrl-D
				String line;
				while (true) {
					try {
						systemRegistry.cleanUp();
						line = reader.readLine(prompt, rightPrompt, (MaskingCallback) null, null);
						systemRegistry.execute(line);
					} catch (UserInterruptException e) {
						// Ignore
					} catch (EndOfFileException e) {
						return;
					} catch (Exception e) {
						systemRegistry.trace(e);
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			AnsiConsole.systemUninstall();
		}
	}




	/**
	 * Top-level command that just prints help.
	 */
	@CommandLine.Command(name = "",
			description = {
					"Example interactive shell with completion and autosuggestions. " +
							"Hit @|magenta <TAB>|@ to see available commands.",
					"Hit @|magenta ALT-S|@ to toggle tailtips.",
					""},
			footer = {"", "Press Ctrl-D to exit."},
			subcommands = {
					MyCommand.class, CommandLine.HelpCommand.class})
	static class CliCommands implements Runnable {
		PrintWriter out;
		LineReaderImpl reader;

		CliCommands() {}
		public void setReader(LineReader reader){
			this.reader = (LineReaderImpl)reader;
			out = reader.getTerminal().writer();
		}

		public void run() {
			out.println(new CommandLine(this).getUsageMessage());
		}
	}

	/**
	 * A command with some options to demonstrate completion.
	 */
	@CommandLine.Command(name = "cmd", mixinStandardHelpOptions = true, version = "1.0",
			description = {"Command with some options to demonstrate TAB-completion.",
					" (Note that enum values also get completed.)"},
			subcommands = {Nested.class, CommandLine.HelpCommand.class})
	static class MyCommand implements Runnable {
		@CommandLine.Option(names = {"-v", "--verbose"},
				description = { "Specify multiple -v options to increase verbosity.",
						"For example, `-v -v -v` or `-vvv`"})
		private boolean[] verbosity = {};

		@CommandLine.ArgGroup(exclusive = false)
		private MyDuration myDuration = new MyDuration();

		static class MyDuration {
			@CommandLine.Option(names = {"-d", "--duration"},
					description = "The duration quantity.",
					required = true)
			private int amount;

			@CommandLine.Option(names = {"-u", "--timeUnit"},
					description = "The duration time unit.",
					required = true)
			private TimeUnit unit;
		}

		@CommandLine.ParentCommand
		CliCommands parent;

		public void run() {
			if (verbosity.length > 0) {
				parent.out.printf("Hi there. You asked for %d %s.%n",
						myDuration.amount, myDuration.unit);
			} else {
				parent.out.println("hi!");
			}
		}
	}

	@CommandLine.Command(name = "nested", mixinStandardHelpOptions = true, subcommands = {CommandLine.HelpCommand.class},
			description = "Hosts more sub-subcommands")
	static class Nested implements Runnable {
		public void run() {
			System.out.println("I'm a nested subcommand. I don't do much, but I have sub-subcommands!");
		}

		@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = {CommandLine.HelpCommand.class},
				description = "Multiplies two numbers.")
		public void multiply(@CommandLine.Option(names = {"-l", "--left"}, required = true) int left,
							 @CommandLine.Option(names = {"-r", "--right"}, required = true) int right) {
			System.out.printf("%d * %d = %d%n", left, right, left * right);
		}

		@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = {CommandLine.HelpCommand.class},
				description = "Adds two numbers.")
		public void add(@CommandLine.Option(names = {"-l", "--left"}, required = true) int left,
						@CommandLine.Option(names = {"-r", "--right"}, required = true) int right) {
			System.out.printf("%d + %d = %d%n", left, right, left + right);
		}

		@CommandLine.Command(mixinStandardHelpOptions = true, subcommands = {CommandLine.HelpCommand.class},
				description = "Subtracts two numbers.")
		public void subtract(@CommandLine.Option(names = {"-l", "--left"}, required = true) int left,
							 @CommandLine.Option(names = {"-r", "--right"}, required = true) int right) {
			System.out.printf("%d - %d = %d%n", left, right, left - right);
		}
	}

}
