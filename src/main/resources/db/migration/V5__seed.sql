INSERT INTO users (email, password_hash, full_name, role) VALUES
    ('admin@shop.local', '{bcrypt}$2a$10$WgP5eVzhU3cSmQwxp138oOpYqt/ED8N/RMVath6cNjl180Ya.EQsu', 'Shop Admin', 'ADMIN'),
    ('customer@shop.local', '{bcrypt}$2a$10$WgP5eVzhU3cSmQwxp138oOpYqt/ED8N/RMVath6cNjl180Ya.EQsu', 'Demo Customer', 'CUSTOMER');

INSERT INTO products (sku, name, description, price_minor, stock, active) VALUES
    ('SKU-TEA-001', 'Green Tea 100g', 'Loose leaf green tea', 12900, 50, TRUE),
    ('SKU-MUG-001', 'Ceramic Mug', '350ml ceramic mug', 24900, 30, TRUE),
    ('SKU-HNY-001', 'Honey 250g', 'Natural flower honey', 18900, 20, TRUE);
