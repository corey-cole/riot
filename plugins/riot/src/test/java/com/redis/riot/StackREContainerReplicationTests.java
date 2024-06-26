package com.redis.riot;

import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import com.redis.enterprise.testcontainers.RedisEnterpriseContainer;
import com.redis.testcontainers.RedisStackContainer;

@EnabledOnOs(OS.LINUX)
class StackREContainerReplicationTests extends ReplicationTests {

	private static final RedisStackContainer source = RedisContainerFactory.stack();
	private static final RedisEnterpriseContainer target = RedisContainerFactory.enterprise();

	@Override
	protected RedisStackContainer getRedisServer() {
		return source;
	}

	@Override
	protected RedisEnterpriseContainer getTargetRedisServer() {
		return target;
	}

}
