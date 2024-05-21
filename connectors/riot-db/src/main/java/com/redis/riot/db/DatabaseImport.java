package com.redis.riot.db;

import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.AbstractCursorItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import com.redis.riot.core.AbstractImport;

public class DatabaseImport extends AbstractImport {

	public static final int DEFAULT_FETCH_SIZE = AbstractCursorItemReader.VALUE_NOT_SET;
	public static final int DEFAULT_MAX_RESULT_SET_ROWS = AbstractCursorItemReader.VALUE_NOT_SET;
	public static final int DEFAULT_QUERY_TIMEOUT = AbstractCursorItemReader.VALUE_NOT_SET;

	private String sql;
	private DataSourceOptions dataSourceOptions = new DataSourceOptions();
	private int maxItemCount;
	private int fetchSize = DEFAULT_FETCH_SIZE;
	private int maxResultSetRows = DEFAULT_MAX_RESULT_SET_ROWS;
	private int queryTimeout = DEFAULT_QUERY_TIMEOUT;
	private boolean useSharedExtendedConnection;
	private boolean verifyCursorPosition;

	@Override
	protected Job job() {
		JdbcCursorItemReaderBuilder<Map<String, Object>> builder = new JdbcCursorItemReaderBuilder<>();
		builder.name(sql);
		builder.saveState(false);
		builder.dataSource(dataSourceOptions.dataSource());
		builder.rowMapper(new ColumnMapRowMapper());
		builder.sql(sql);
		builder.fetchSize(fetchSize);
		builder.maxRows(maxResultSetRows);
		builder.queryTimeout(queryTimeout);
		builder.useSharedExtendedConnection(useSharedExtendedConnection);
		builder.verifyCursorPosition(verifyCursorPosition);
		if (maxItemCount > 0) {
			builder.maxItemCount(maxItemCount);
		}
		JdbcCursorItemReader<Map<String, Object>> reader = builder.build();
		ItemProcessor<Map<String, Object>, Map<String, Object>> processor = mapProcessor();
		ItemWriter<Map<String, Object>> writer = mapWriter();
		return jobBuilder().start(step(getName(), reader, writer).processor(processor).build()).build();
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public DataSourceOptions getDataSourceOptions() {
		return dataSourceOptions;
	}

	public void setDataSourceOptions(DataSourceOptions dataSourceOptions) {
		this.dataSourceOptions = dataSourceOptions;
	}

	public int getMaxItemCount() {
		return maxItemCount;
	}

	public int getFetchSize() {
		return fetchSize;
	}

	public int getMaxResultSetRows() {
		return maxResultSetRows;
	}

	public int getQueryTimeout() {
		return queryTimeout;
	}

	public boolean isUseSharedExtendedConnection() {
		return useSharedExtendedConnection;
	}

	public boolean isVerifyCursorPosition() {
		return verifyCursorPosition;
	}

	public void setMaxItemCount(int maxItemCount) {
		this.maxItemCount = maxItemCount;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public void setMaxResultSetRows(int rows) {
		this.maxResultSetRows = rows;
	}

	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}

	public void setUseSharedExtendedConnection(boolean useSharedExtendedConnection) {
		this.useSharedExtendedConnection = useSharedExtendedConnection;
	}

	public void setVerifyCursorPosition(boolean verifyCursorPosition) {
		this.verifyCursorPosition = verifyCursorPosition;
	}

}
