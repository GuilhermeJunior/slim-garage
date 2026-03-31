CREATE DATABASE IF NOT EXISTS slim_garage;
USE slim_garage;

-- 1. Tabela Garage
CREATE TABLE IF NOT EXISTS garage (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      des_name VARCHAR(255) NOT NULL,
                                      dat_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      INDEX idx_garage_creation (dat_creation)
) ENGINE=InnoDB;

-- 2. Tabela Sector (Relacionamento ManyToOne com Garage)
CREATE TABLE IF NOT EXISTS sector (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      des_name VARCHAR(255) NOT NULL,
                                      garage_id BIGINT NOT NULL,
                                      base_price DECIMAL(19, 2) NOT NULL,
                                      max_capacity INT NOT NULL,
                                      dat_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      CONSTRAINT fk_sector_garage FOREIGN KEY (garage_id) REFERENCES garage(id),
                                      INDEX idx_sector_garage (garage_id),
                                      INDEX idx_sector_creation (dat_creation)
) ENGINE=InnoDB;

-- 3. Tabela Spot (Relacionamento ManyToOne com Sector)
CREATE TABLE IF NOT EXISTS spot (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    sector_id BIGINT NOT NULL,
                                    nr_latitude BIGINT NOT NULL,
                                    nr_longitude BIGINT NOT NULL,
                                    ie_taken BOOLEAN NOT NULL DEFAULT FALSE,
                                    dat_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    CONSTRAINT fk_spot_sector FOREIGN KEY (sector_id) REFERENCES sector(id),
                                    INDEX idx_spot_sector (sector_id),
                                    INDEX idx_spot_creation (dat_creation),
                                    INDEX idx_spot_taken (ie_taken)
) ENGINE=InnoDB;

-- 4. Tabela Parking Registry
CREATE TABLE IF NOT EXISTS parking_registry (
                                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                spot_id BIGINT NOT NULL,
                                                dat_start DATETIME NOT NULL,
                                                dat_end DATETIME,
                                                des_license_plate VARCHAR(20) NOT NULL,
                                                qty_revenue DECIMAL(19, 2) NOT NULL,
                                                pct_price_discount DECIMAL(5, 2) NOT NULL DEFAULT 0,
                                                pct_price_increase DECIMAL(5, 2) NOT NULL DEFAULT 0,
                                                dat_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                CONSTRAINT fk_parking_spot FOREIGN KEY (spot_id) REFERENCES spot(id),
                                                INDEX idx_parking_registry_spot (spot_id),
                                                INDEX idx_parking_registry_creation (dat_creation),
                                                INDEX idx_parking_registry_start_date (dat_start)
) ENGINE=InnoDB;
