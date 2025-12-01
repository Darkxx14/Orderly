package com.xyrisdev.orderly.storage.impl.migration;

import com.xyrisdev.library.logger.XLogger;
import com.xyrisdev.orderly.storage.DatabaseProvider;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;

public final class MigrationUtil {

	private MigrationUtil() {}

	public static void apply(@NotNull String path, @NotNull DatabaseProvider provider) {
		try (InputStream stream = MigrationUtil.class.getClassLoader().getResourceAsStream(path)) {

			if (stream == null) {
				return;
			}

			final String sql = new String(stream.readAllBytes());

			try (Connection connection = provider.connection();
			     Statement statement = connection.createStatement()) {

				statement.executeUpdate(sql);
			}

		} catch (Exception e) {
			XLogger.stackTrace(e);
		}
	}
}
