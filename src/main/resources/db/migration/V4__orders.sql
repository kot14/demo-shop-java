CREATE TABLE orders (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id BIGINT      NOT NULL,
    status      VARCHAR(20) NOT NULL,
    total_minor BIGINT      NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at     TIMESTAMP WITH TIME ZONE
);
CREATE INDEX idx_orders_status_created ON orders(status, created_at);

CREATE TABLE order_items (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id         BIGINT       NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id       BIGINT       NOT NULL,
    product_name     VARCHAR(255) NOT NULL,
    unit_price_minor BIGINT       NOT NULL,
    quantity         INT          NOT NULL CHECK (quantity > 0)
);
