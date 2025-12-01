package com.xyrisdev.orderly.storage;

import org.jetbrains.annotations.NotNull;

public record StorageConfig(
		@NotNull DatabaseType type,
		@NotNull String host,
		int port,
		@NotNull String database,
		@NotNull String username,
		@NotNull String password,
		@NotNull String sqliteFile,

		int maximumPoolSize,
		int minimumIdle,
		long connectionTimeout,
		long idleTimeout,
		long maxLifetime
) {}