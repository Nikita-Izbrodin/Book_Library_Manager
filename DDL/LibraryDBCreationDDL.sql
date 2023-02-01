-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema booklibrary
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema booklibrary
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `booklibrary` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `booklibrary` ;

-- -----------------------------------------------------
-- Table `booklibrary`.`books`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booklibrary`.`books` (
  `book_id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(256) CHARACTER SET 'utf8mb3' NOT NULL,
  `author` VARCHAR(256) CHARACTER SET 'utf8mb3' NOT NULL,
  `isbn` VARCHAR(30) NULL DEFAULT NULL,
  `quantity` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`book_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 33
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `booklibrary`.`members`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booklibrary`.`members` (
  `member_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) CHARACTER SET 'utf8mb3' NOT NULL,
  `surname` VARCHAR(45) CHARACTER SET 'utf8mb3' NOT NULL,
  `phone` VARCHAR(45) NULL DEFAULT NULL,
  `email` VARCHAR(100) NULL DEFAULT NULL,
  `address` VARCHAR(256) CHARACTER SET 'utf8mb3' NOT NULL,
  `postcode` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`member_id`))
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `booklibrary`.`borrowed_books`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booklibrary`.`borrowed_books` (
  `book_id` INT NOT NULL,
  `member_id` INT NOT NULL,
  `return_date` DATE NOT NULL,
  INDEX `book_fk_idx` (`book_id` ASC) VISIBLE,
  INDEX `member_fk_idx` (`member_id` ASC) VISIBLE,
  CONSTRAINT `book_fk`
    FOREIGN KEY (`book_id`)
    REFERENCES `booklibrary`.`books` (`book_id`)
    ON DELETE RESTRICT,
  CONSTRAINT `member_fk`
    FOREIGN KEY (`member_id`)
    REFERENCES `booklibrary`.`members` (`member_id`)
    ON DELETE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `booklibrary`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `booklibrary`.`users` (
  `username` VARCHAR(30) NOT NULL,
  `password` VARCHAR(256) NOT NULL,
  `full_name` VARCHAR(256) CHARACTER SET 'utf8mb3' NOT NULL,
  PRIMARY KEY (`username`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
