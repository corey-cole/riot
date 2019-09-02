package com.redislabs.riot;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.redislabs.lettusearch.RediSearchClient;
import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.StatefulRediSearchConnection;

import picocli.CommandLine;

public class BaseTest {

	private final static String COMMAND_START = "$ riot ";

	private static RediSearchClient client;
	private static StatefulRediSearchConnection<String, String> connection;

	@BeforeAll
	public static void setup() throws IOException {
		client = RediSearchClient.create("redis://localhost");
		connection = client.connect();
	}

	@BeforeEach
	public void flushAll() {
		connection.sync().flushall();
	}

	protected RediSearchCommands<String, String> commands() {
		return connection.sync();
	}

	@AfterAll
	public static void teardown() {
		if (connection != null) {
			connection.close();
		}
		if (client != null) {
			client.shutdown();
		}
	}

	protected void runFile(String filename, Object... args) throws IOException {
		try (InputStream inputStream = getClass().getResourceAsStream("/commands/" + filename + ".txt")) {
			runCommand(new String(inputStream.readAllBytes()), args);
		}
	}

	protected void runCommand(String line, Object... args) {
		String command = line.startsWith(COMMAND_START) ? line.substring(COMMAND_START.length()) : line;
		new CommandLine(new Riot()).execute(String.format(command, args).split(" "));
	}

}