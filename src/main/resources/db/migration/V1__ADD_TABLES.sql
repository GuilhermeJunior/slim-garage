-- Criação do banco de dados (opcional, caso ainda não exista)
CREATE DATABASE IF NOT EXISTS slim_garage;
USE slim_garage;

-- 1. Tabela Garage
CREATE TABLE garage (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- 2. Tabela Sector (Relacionamento ManyToOne com Garage)
CREATE TABLE sector (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        garage_id BIGINT NOT NULL,
                        CONSTRAINT fk_sector_garage FOREIGN KEY (garage_id) REFERENCES garage(id)
) ENGINE=InnoDB;

-- 3. Tabela Spot (Relacionamento ManyToOne com Sector)
CREATE TABLE spot (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      sector_id BIGINT NOT NULL,
                      latitude BIGINT NOT NULL,
                      longitude BIGINT NOT NULL,
                      CONSTRAINT fk_spot_sector FOREIGN KEY (sector_id) REFERENCES sector(id)
) ENGINE=InnoDB;

-- 4. Tabela Parking Registry
CREATE TABLE parking_registry (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  spot_id BIGINT NOT NULL,
                                  dat_start DATETIME NOT NULL,
                                  dat_end DATETIME NOT NULL,
                                  license_plate VARCHAR(20) NOT NULL,
                                  revenue DECIMAL(19, 2) NOT NULL,
                                  CONSTRAINT fk_parking_spot FOREIGN KEY (spot_id) REFERENCES spot(id)
) ENGINE=InnoDB;

-- 5. Criação do Índice solicitado
-- Índices em chaves estrangeiras ajudam muito na performance de JOINS
CREATE INDEX idx_parking_registry_spot ON parking_registry(spot_id);