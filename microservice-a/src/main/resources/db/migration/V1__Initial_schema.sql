-- =================================================================
-- Initial Database Schema for Microservice A - Product Service
-- =================================================================

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_products_active ON products(active);
CREATE INDEX IF NOT EXISTS idx_products_price ON products(price);
CREATE INDEX IF NOT EXISTS idx_products_quantity ON products(quantity);
CREATE INDEX IF NOT EXISTS idx_products_created_at ON products(created_at);

-- Insert sample data for testing
INSERT INTO products (name, description, price, quantity, active) VALUES
('Laptop Pro 15"', 'High-performance laptop for professionals', 1299.99, 50, true),
('Wireless Mouse', 'Ergonomic wireless mouse with USB receiver', 29.99, 200, true),
('Mechanical Keyboard', 'RGB backlit mechanical keyboard', 129.99, 75, true),
('USB-C Hub', '7-in-1 USB-C hub with HDMI and Ethernet', 79.99, 120, true),
('Webcam HD', '1080p HD webcam with auto-focus', 89.99, 80, true)
ON CONFLICT DO NOTHING;