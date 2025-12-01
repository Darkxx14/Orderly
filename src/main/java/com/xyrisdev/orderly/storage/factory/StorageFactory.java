package com.xyrisdev.orderly.storage.factory;

import com.xyrisdev.orderly.storage.DatabaseProvider;
import com.xyrisdev.orderly.storage.StorageConfig;
import com.xyrisdev.orderly.storage.impl.provider.MySQLDatabaseProvider;
import com.xyrisdev.orderly.storage.impl.provider.SQLiteDatabaseProvider;
import org.jetbrains.annotations.NotNull;

public final class StorageFactory {

	private StorageFactory() {}

	public static @NotNull DatabaseProvider create(@NotNull StorageConfig config) {
		return switch (config.type()) {
			case MYSQL -> new MySQLDatabaseProvider(config);
			case SQLITE -> new SQLiteDatabaseProvider(config);
		};
	}
}
