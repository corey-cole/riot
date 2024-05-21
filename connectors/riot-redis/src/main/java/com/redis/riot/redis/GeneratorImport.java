package com.redis.riot.redis;

import java.util.List;

import org.springframework.batch.core.Job;

import com.redis.riot.core.AbstractRedisCallable;
import com.redis.riot.core.RedisWriterOptions;
import com.redis.spring.batch.RedisItemWriter;
import com.redis.spring.batch.common.DataType;
import com.redis.spring.batch.common.KeyValue;
import com.redis.spring.batch.gen.CollectionOptions;
import com.redis.spring.batch.gen.GeneratorItemReader;
import com.redis.spring.batch.gen.MapOptions;
import com.redis.spring.batch.gen.Range;
import com.redis.spring.batch.gen.StreamOptions;
import com.redis.spring.batch.gen.StringOptions;
import com.redis.spring.batch.gen.TimeSeriesOptions;
import com.redis.spring.batch.gen.ZsetOptions;

public class GeneratorImport extends AbstractRedisCallable {

	public static final int DEFAULT_COUNT = 1000;

	private int count = DEFAULT_COUNT;
	private String keyspace = GeneratorItemReader.DEFAULT_KEYSPACE;
	private Range keyRange = GeneratorItemReader.DEFAULT_KEY_RANGE;
	private List<DataType> types = GeneratorItemReader.defaultTypes();
	private Range expiration;
	private MapOptions hashOptions = new MapOptions();
	private StreamOptions streamOptions = new StreamOptions();
	private TimeSeriesOptions timeSeriesOptions = new TimeSeriesOptions();
	private MapOptions jsonOptions = new MapOptions();
	private CollectionOptions listOptions = new CollectionOptions();
	private CollectionOptions setOptions = new CollectionOptions();
	private StringOptions stringOptions = new StringOptions();
	private ZsetOptions zsetOptions = new ZsetOptions();
	private RedisWriterOptions writerOptions = new RedisWriterOptions();

	@Override
	protected Job job() {
		GeneratorItemReader reader = new GeneratorItemReader();
		reader.setExpiration(expiration);
		reader.setHashOptions(hashOptions);
		reader.setJsonOptions(jsonOptions);
		reader.setKeyRange(keyRange);
		reader.setKeyspace(keyspace);
		reader.setListOptions(listOptions);
		reader.setMaxItemCount(count);
		reader.setSetOptions(setOptions);
		reader.setStreamOptions(streamOptions);
		reader.setStringOptions(stringOptions);
		reader.setTimeSeriesOptions(timeSeriesOptions);
		reader.setTypes(types);
		reader.setZsetOptions(zsetOptions);
		RedisItemWriter<String, String, KeyValue<String, Object>> writer = RedisItemWriter.struct();
		writerOptions.configure(writer);
		configure(writer);
		return jobBuilder().start(step(getName(), reader, writer).build()).build();
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Range getKeyRange() {
		return keyRange;
	}

	public void setKeyRange(Range keyRange) {
		this.keyRange = keyRange;
	}

	public Range getExpiration() {
		return expiration;
	}

	public void setExpiration(Range expiration) {
		this.expiration = expiration;
	}

	public MapOptions getHashOptions() {
		return hashOptions;
	}

	public void setHashOptions(MapOptions hashOptions) {
		this.hashOptions = hashOptions;
	}

	public StreamOptions getStreamOptions() {
		return streamOptions;
	}

	public void setStreamOptions(StreamOptions streamOptions) {
		this.streamOptions = streamOptions;
	}

	public TimeSeriesOptions getTimeSeriesOptions() {
		return timeSeriesOptions;
	}

	public void setTimeSeriesOptions(TimeSeriesOptions timeSeriesOptions) {
		this.timeSeriesOptions = timeSeriesOptions;
	}

	public MapOptions getJsonOptions() {
		return jsonOptions;
	}

	public void setJsonOptions(MapOptions jsonOptions) {
		this.jsonOptions = jsonOptions;
	}

	public CollectionOptions getListOptions() {
		return listOptions;
	}

	public void setListOptions(CollectionOptions listOptions) {
		this.listOptions = listOptions;
	}

	public CollectionOptions getSetOptions() {
		return setOptions;
	}

	public void setSetOptions(CollectionOptions setOptions) {
		this.setOptions = setOptions;
	}

	public StringOptions getStringOptions() {
		return stringOptions;
	}

	public void setStringOptions(StringOptions stringOptions) {
		this.stringOptions = stringOptions;
	}

	public ZsetOptions getZsetOptions() {
		return zsetOptions;
	}

	public void setZsetOptions(ZsetOptions zsetOptions) {
		this.zsetOptions = zsetOptions;
	}

	public String getKeyspace() {
		return keyspace;
	}

	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}

	public List<DataType> getTypes() {
		return types;
	}

	public void setTypes(List<DataType> types) {
		this.types = types;
	}

	public RedisWriterOptions getWriterOptions() {
		return writerOptions;
	}

	public void setWriterOptions(RedisWriterOptions writerOptions) {
		this.writerOptions = writerOptions;
	}
}
