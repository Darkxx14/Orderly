CREATE TABLE IF NOT EXISTS orders (
    id TEXT PRIMARY KEY,
    owner TEXT NOT NULL,
    delivered INTEGER NOT NULL,
    required INTEGER NOT NULL,
    price REAL NOT NULL,
    item BLOB NOT NULL,
    created_at INTEGER NOT NULL
);