package com.redis.riot.cli;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import com.redis.riot.core.AbstractRedisCallable;
import com.redis.riot.redis.GeneratorImport;
import com.redis.spring.batch.common.DataType;
import com.redis.spring.batch.gen.CollectionOptions;
import com.redis.spring.batch.gen.GeneratorItemReader;
import com.redis.spring.batch.gen.MapOptions;
import com.redis.spring.batch.gen.Range;
import com.redis.spring.batch.gen.StreamOptions;
import com.redis.spring.batch.gen.StringOptions;
import com.redis.spring.batch.gen.TimeSeriesOptions;
import com.redis.spring.batch.gen.ZsetOptions;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "generate", description = "Generate data structures.")
public class GenerateCommand extends AbstractRedisCommand {

	private static final String TASK_NAME = "Generating";

	@Option(names = "--count", description = "Number of items to generate (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	int count = GeneratorImport.DEFAULT_COUNT;

	@Option(names = "--keyspace", description = "Keyspace prefix for generated data structures (default: ${DEFAULT-VALUE}).", paramLabel = "<str>")
	String keyspace = GeneratorItemReader.DEFAULT_KEYSPACE;

	@Option(names = "--keys", description = "Range of keys to generate in the form '<start>:<end>' (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range keyRange = GeneratorItemReader.DEFAULT_KEY_RANGE;

	@Option(arity = "1..*", names = "--types", description = "Types of data structures to generate: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE}).", paramLabel = "<type>")
	List<DataType> types = Arrays.asList(DataType.HASH, DataType.SET, DataType.STRING, DataType.ZSET);

	@Option(names = "--expiration", description = "TTL in seconds.", paramLabel = "<secs>")
	Range expiration;

	@Option(names = "--hash-size", description = "Number of fields in hashes (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range hashFieldCount = MapOptions.DEFAULT_FIELD_COUNT;

	@Option(names = "--hash-value", description = "Value size for hash fields (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range hashFieldLength = MapOptions.DEFAULT_FIELD_LENGTH;

	@Option(names = "--json-size", description = "Number of fields in JSON docs (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range jsonFieldCount = MapOptions.DEFAULT_FIELD_COUNT;

	@Option(names = "--json-value", description = "Value size for JSON fields (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range jsonFieldLength = MapOptions.DEFAULT_FIELD_LENGTH;

	@Option(names = "--list-size", description = "Number of elements in lists (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range listMemberCount = CollectionOptions.DEFAULT_MEMBER_COUNT;

	@Option(names = "--list-value", description = "Value size for list elements (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range listMemberRange = CollectionOptions.DEFAULT_MEMBER_RANGE;

	@Option(names = "--set-size", description = "Number of elements in sets (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range setMemberCount = CollectionOptions.DEFAULT_MEMBER_COUNT;

	@Option(names = "--set-value", description = "Value size for set elements (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range setMemberLength = CollectionOptions.DEFAULT_MEMBER_RANGE;

	@Option(names = "--stream-size", description = "Number of messages in streams (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range streamMessageCount = StreamOptions.DEFAULT_MESSAGE_COUNT;

	@Option(names = "--stream-fields", description = "Number of fields in stream messages (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range streamFieldCount = MapOptions.DEFAULT_FIELD_COUNT;

	@Option(names = "--stream-value", description = "Value size for fields in stream messages (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range streamFieldLength = MapOptions.DEFAULT_FIELD_LENGTH;

	@Option(names = "--string-value", description = "Length of strings (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range stringLength = StringOptions.DEFAULT_LENGTH;

	@Option(names = "--ts-size", description = "Number of samples in timeseries (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range timeseriesSampleCount = TimeSeriesOptions.DEFAULT_SAMPLE_COUNT;

	@Option(names = "--ts-time", description = "Start time for samples in timeseries, e.g. 2007-12-03T10:15:30.00Z (default: now).", paramLabel = "<epoch>")
	Instant timeseriesStartTime;

	@Option(names = "--zset-size", description = "Number of elements in sorted sets (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range zsetMemberCount = CollectionOptions.DEFAULT_MEMBER_COUNT;

	@Option(names = "--zset-value", description = "Value size for sorted-set elements (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range zsetMemberLength = CollectionOptions.DEFAULT_MEMBER_RANGE;

	@Option(names = "--zset-score", description = "Score of sorted sets (default: ${DEFAULT-VALUE}).", paramLabel = "<int>")
	Range zsetScore = ZsetOptions.DEFAULT_SCORE;

	@ArgGroup(exclusive = false, heading = "Redis writer options%n")
	private RedisWriterArgs redisWriterArgs = new RedisWriterArgs();

	@Override
	protected String taskName(String stepName) {
		return TASK_NAME;
	}

	@Override
	protected AbstractRedisCallable redisCallable() {
		GeneratorImport callable = new GeneratorImport();
		callable.setCount(count);
		callable.setExpiration(expiration);
		callable.setHashOptions(hashOptions());
		callable.setJsonOptions(jsonOptions());
		callable.setKeyRange(keyRange);
		callable.setKeyspace(keyspace);
		callable.setListOptions(listOptions());
		callable.setSetOptions(setOptions());
		callable.setStreamOptions(streamOptions());
		callable.setStringOptions(stringOptions());
		callable.setTimeSeriesOptions(timeseriesOptions());
		callable.setTypes(types);
		callable.setZsetOptions(zsetOptions());
		callable.setWriterOptions(redisWriterArgs.writerOptions());
		return callable;
	}

	private ZsetOptions zsetOptions() {
		ZsetOptions options = new ZsetOptions();
		options.setMemberCount(zsetMemberCount);
		options.setMemberRange(zsetMemberLength);
		options.setScore(zsetScore);
		return options;
	}

	private TimeSeriesOptions timeseriesOptions() {
		TimeSeriesOptions options = new TimeSeriesOptions();
		options.setSampleCount(timeseriesSampleCount);
		if (timeseriesStartTime != null) {
			options.setStartTime(timeseriesStartTime);
		}
		return options;
	}

	private StringOptions stringOptions() {
		StringOptions options = new StringOptions();
		options.setLength(stringLength);
		return options;
	}

	private StreamOptions streamOptions() {
		StreamOptions options = new StreamOptions();
		options.setBodyOptions(mapOptions(streamFieldCount, streamFieldLength));
		options.setMessageCount(streamMessageCount);
		return options;
	}

	private CollectionOptions setOptions() {
		return collectionOptions(setMemberCount, setMemberLength);
	}

	private CollectionOptions listOptions() {
		return collectionOptions(listMemberCount, listMemberRange);
	}

	private CollectionOptions collectionOptions(Range memberCount, Range memberRange) {
		CollectionOptions options = new CollectionOptions();
		options.setMemberCount(memberCount);
		options.setMemberRange(memberRange);
		return options;
	}

	private MapOptions jsonOptions() {
		return mapOptions(jsonFieldCount, jsonFieldLength);
	}

	private MapOptions hashOptions() {
		return mapOptions(hashFieldCount, hashFieldLength);
	}

	private MapOptions mapOptions(Range fieldCount, Range fieldLength) {
		MapOptions options = new MapOptions();
		options.setFieldCount(fieldCount);
		options.setFieldLength(fieldLength);
		return options;
	}

	public RedisWriterArgs getRedisWriterArgs() {
		return redisWriterArgs;
	}

	public void setRedisWriterArgs(RedisWriterArgs args) {
		this.redisWriterArgs = args;
	}

}
