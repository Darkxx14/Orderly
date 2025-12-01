CREATE TABLE IF NOT EXISTS orders (
    id VARCHAR(36) PRIMARY KEY,
    owner VARCHAR(36) NOT NULL,
    delivered INT NOT NULL,
    required INT NOT NULL,
    price DOUBLE NOT NULL,
    item BLOB NOT NULL,
    created_at BIGINT NOT NULL
);