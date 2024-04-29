package com.redis.riot.core;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import com.redis.lettucemod.api.sync.RedisModulesCommands;
import com.redis.lettucemod.util.RedisModulesUtils;
import com.redis.spring.batch.RedisItemReader;
import com.redis.spring.batch.RedisItemWriter;

import io.lettuce.core.AbstractRedisClient;

public abstract class AbstractRedisCallable extends AbstractRiotCallable {

	private static final String CONTEXT_VAR_REDIS = "redis";

	private EvaluationContextOptions evaluationContextOptions = new EvaluationContextOptions();

	private RedisClientOptions redisClientOptions = new RedisClientOptions();

	private AbstractRedisClient redisClient;
	private StatefulRedisModulesConnection<String, String> redisConnection;
	protected RedisModulesCommands<String, String> redisCommands;
	private StandardEvaluationContext evaluationContext;

	@Override
	public void afterPropertiesSet() throws Exception {
		redisClient = redisClientOptions.redisClient();
		redisConnection = RedisModulesUtils.connection(redisClient);
		redisCommands = redisConnection.sync();
		evaluationContext = evaluationContext();
		super.afterPropertiesSet();
	}

	protected StandardEvaluationContext evaluationContext() {
		StandardEvaluationContext context = evaluationContextOptions.evaluationContext();
		context.setVariable(CONTEXT_VAR_REDIS, redisCommands);
		return context;
	}

	@Override
	public void close() {
		evaluationContext = null;
		redisCommands = null;
		if (redisConnection != null) {
			redisConnection.close();
			redisConnection = null;
		}
		if (redisClient != null) {
			redisClient.close();
			redisClient.getResources().shutdown();
		}
	}

	public StandardEvaluationContext getEvaluationContext() {
		return evaluationContext;
	}

	public void setEvaluationContextOptions(EvaluationContextOptions spelProcessorOptions) {
		this.evaluationContextOptions = spelProcessorOptions;
	}

	protected <K, V, T> void configure(RedisItemReader<K, V, T> reader) {
		reader.setClient(redisClient);
		reader.setDatabase(redisClientOptions.getRedisURI().getDatabase());
	}

	protected <K, V, T> void configure(RedisItemWriter<K, V, T> writer) {
		writer.setClient(redisClient);
	}

	public RedisClientOptions getRedisClientOptions() {
		return redisClientOptions;
	}

	public void setRedisClientOptions(RedisClientOptions options) {
		this.redisClientOptions = options;
	}

}