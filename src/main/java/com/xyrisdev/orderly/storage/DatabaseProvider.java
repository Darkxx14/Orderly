package com.xyrisdev.orderly.storage;

import java.sql.Connection;

public interface DatabaseProvider {

	void connect();
	void disconnect();
	Connection connection();
	void migrate();
}
