CREATE DATABASE db_product;
USE db_product;

-- Membuat tabel product
CREATE TABLE product (
  id VARCHAR(255) PRIMARY KEY,
  nama VARCHAR(255) NOT NULL,
  harga DOUBLE NOT NULL,
  kategori VARCHAR(255) NOT NULL,
  seri VARCHAR(255) NOT NULL
);

-- Memasukkan data sesuai contoh listProduct.add(...)
INSERT INTO product (id, nama, harga, kategori, seri) VALUES
('P001', 'Budew', 45000.0, 'Pokemon', 'SVS'),
('T001', 'Arvin', 40000.0, 'Trainer', 'SVS'),
('I001', 'Nest Ball', 25000.0, 'Item', 'SVS'),
('TO001', 'Jimat Keberanian', 15000.0, 'Tool', 'SVS'),
('S001', 'Kota Mukun', 15000.0, 'Stadium', 'SVS'),
('E001', 'Energi Lumian', 25000.0, 'Energi', 'SVS');
