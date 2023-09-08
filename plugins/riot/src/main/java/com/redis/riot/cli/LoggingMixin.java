package com.redis.riot.cli;

import static java.lang.System.setProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.event.Level;
import org.slf4j.impl.SimpleLogger;

import picocli.CommandLine.ExecutionException;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Spec.Target;

public class LoggingMixin {

    private static final String JLINE = "org.jline";

    private static final String AWS = "com.amazonaws";

    private static final String LETTUCE = "io.lettuce";

    private static final String NETTY = "io.netty";

    @Spec(Target.MIXEE)
    private CommandSpec mixee;

    boolean debug;

    boolean info;

    boolean warn;

    boolean error;

    String logFile;

    boolean showDateTime;

    String dateTimeFormat;

    boolean showThreadId;

    boolean showThreadName;

    boolean showLogName;

    boolean showShortLogName;

    boolean levelInBrackets;

    @Option(arity = "1..*", names = "--log-levels", description = "Min log levels (default: ${DEFAULT-VALUE}).", paramLabel = "<lvl>")
    Map<String, Level> logLevels = defaultLogLevels();

    @Option(names = "--log-file", description = "Log output target which can be a file path or special values System.out and System.err (default: System.err).", paramLabel = "<file>")
    public void setLogFile(String file) {
        getTopLevelCommandLoggingMixin(mixee).logFile = file;
    }

    @Option(names = "--log-time", description = "Include current date and time in log messages.")
    public void setShowDateTime(boolean show) {
        getTopLevelCommandLoggingMixin(mixee).showDateTime = show;
    }

    @Option(names = "--log-time-format", description = "Date and time format to be used in log messages (default: ms since startup).", paramLabel = "<fmt>")
    public void setDateTimeFormat(String format) {
        getTopLevelCommandLoggingMixin(mixee).dateTimeFormat = format;
    }

    @Option(names = "--log-thread-id", description = "Include current thread ID in log messages.")
    public void setShowThreadId(boolean show) {
        getTopLevelCommandLoggingMixin(mixee).showThreadId = show;
    }

    @Option(names = "--log-thread-name", description = "Include current thread name in log messages.")
    public void setShowThreadName(boolean show) {
        getTopLevelCommandLoggingMixin(mixee).showThreadName = show;
    }

    @Option(names = "--log-name", description = "Include logger instance name in log messages.")
    public void setShowLogName(boolean show) {
        getTopLevelCommandLoggingMixin(mixee).showLogName = show;
    }

    @Option(names = "--log-short", description = "Include last component of logger instance name in log messages.")
    public void setShowShortLogName(boolean show) {
        getTopLevelCommandLoggingMixin(mixee).showShortLogName = show;
    }

    @Option(names = "--log-level-brackets", description = "Output log level string in brackets.")
    public void setLevelInBrackets(boolean enable) {
        getTopLevelCommandLoggingMixin(mixee).levelInBrackets = enable;
    }

    @Option(names = { "-d", "--debug" }, description = "Log in debug mode (includes normal stacktrace).")
    public void setDebug(boolean debug) {
        getTopLevelCommandLoggingMixin(mixee).debug = debug;
    }

    @Option(names = { "-i", "--info" }, description = "Set log level to info.")
    public void setInfo(boolean info) {
        getTopLevelCommandLoggingMixin(mixee).info = info;
    }

    @Option(names = { "-w", "--warn" }, description = "Set log level to warn.")
    public void setWarn(boolean warn) {
        getTopLevelCommandLoggingMixin(mixee).warn = warn;
    }

    @Option(names = { "-q", "--quiet" }, description = "Log errors only.")
    public void setError(boolean error) {
        getTopLevelCommandLoggingMixin(mixee).error = error;
    }

    public static int executionStrategy(ParseResult parseResult) throws ExecutionException, ParameterException {
        getTopLevelCommandLoggingMixin(parseResult.commandSpec()).configureLogging();
        return ExitCode.OK;
    }

    private static LoggingMixin getTopLevelCommandLoggingMixin(CommandSpec commandSpec) {
        return ((Main) commandSpec.root().userObject()).loggingMixin;
    }

    private static Map<String, Level> defaultLogLevels() {
        Map<String, Level> logs = new HashMap<>();
        logs.put(JLINE, Level.INFO);
        logs.put(AWS, Level.ERROR);
        logs.put(LETTUCE, Level.INFO);
        logs.put(NETTY, Level.INFO);
        return logs;
    }

    public void configureLogging() {
        configureLogging(getTopLevelCommandLoggingMixin(mixee));
    }

    private static void configureLogging(LoggingMixin mixin) {
        Level logLevel = mixin.logLevel();
        if (mixin.logFile != null) {
            setProperty(SimpleLogger.LOG_FILE_KEY, mixin.logFile);
        }
        setBoolean(SimpleLogger.SHOW_DATE_TIME_KEY, mixin.showDateTime);
        if (mixin.dateTimeFormat != null) {
            setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, mixin.dateTimeFormat);
        }
        setBoolean(SimpleLogger.SHOW_THREAD_ID_KEY, mixin.showThreadId);
        setBoolean(SimpleLogger.SHOW_THREAD_NAME_KEY, mixin.showThreadName);
        setBoolean(SimpleLogger.SHOW_LOG_NAME_KEY, mixin.showLogName);
        setBoolean(SimpleLogger.SHOW_SHORT_LOG_NAME_KEY, mixin.showShortLogName);
        setBoolean(SimpleLogger.LEVEL_IN_BRACKETS_KEY, mixin.levelInBrackets);
        setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, logLevel.name());
        for (Entry<String, Level> entry : mixin.logLevels.entrySet()) {
            if (entry.getValue().compareTo(logLevel) < 0) {
                System.setProperty(org.slf4j.impl.SimpleLogger.LOG_KEY_PREFIX + entry.getKey(), entry.getValue().name());
            }
        }
    }

    private static void setBoolean(String property, boolean value) {
        setProperty(property, String.valueOf(value));
    }

    private Level logLevel() {
        if (debug) {
            return Level.DEBUG;
        }
        if (info) {
            return Level.INFO;
        }
        if (warn) {
            return Level.WARN;
        }
        if (error) {
            return Level.ERROR;
        }
        return Level.WARN;
    }

}
