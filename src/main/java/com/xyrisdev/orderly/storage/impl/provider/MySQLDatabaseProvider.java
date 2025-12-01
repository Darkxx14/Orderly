package com.xyrisdev.orderly.storage.impl.provider;

import com.xyrisdev.orderly.storage.DatabaseProvider;
import com.xyrisdev.orderly.storage.StorageConfig;
import com.xyrisdev.orderly.storage.impl.migration.MigrationUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class MySQLDatabaseProvider implements DatabaseProvider {

	private final StorageConfig config;
	private HikariDataSource source;

	public MySQLDatabaseProvider(StorageConfig config) {
		this.config = config;
	}

	@Override
	public void connect() {
		final HikariConfig hikari = new HikariConfig();

		hikari.setPoolName("Orderly-MySQL");
		hikari.setJdbcUrl("jdbc:mysql://" + config.host() + ":" + config.port() + "/" + config.database());
		hikari.setUsername(config.username());
		hikari.setPassword(config.password());

		hikari.setMaximumPoolSize(config.maximumPoolSize());
		hikari.setMinimumIdle(config.minimumIdle());
		hikari.setConnectionTimeout(config.connectionTimeout());
		hikari.setIdleTimeout(config.idleTimeout());
		hikari.setMaxLifetime(config.maxLifetime());
		hikari.setAutoCommit(true);

		this.source = new HikariDataSource(hikari);
		migrate();
	}

	@Override
	public Connection connection() {
		try {
			return source.getConnection();
		} catch (SQLException exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	public void disconnect() {
		if (source != null) {
			source.close();
		}
	}

	@Override
	public void migrate() {
		MigrationUtil.apply("database/mysql_schema.sql", this);
	}
}
