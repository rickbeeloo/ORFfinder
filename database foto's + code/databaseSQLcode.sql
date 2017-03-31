-- MySQL Script generated by MySQL Workbench
-- Fri Mar 31 19:36:11 2017
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`sequence`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`sequence` (
  `header` VARCHAR(100) NOT NULL,
  `sequence` VARCHAR(1000) NULL,
  `seqID` INT(25) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`seqID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`orf`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`orf` (
  `start` INT(25) NULL,
  `stop` INT(25) NULL,
  `strand` CHAR(1) NULL,
  `ORFID` INT(25) NOT NULL AUTO_INCREMENT,
  `sequence_seqID` INT(25) NOT NULL,
  PRIMARY KEY (`ORFID`),
  INDEX `fk_orf_sequence_idx` (`sequence_seqID` ASC),
  CONSTRAINT `fk_orf_sequence`
    FOREIGN KEY (`sequence_seqID`)
    REFERENCES `mydb`.`sequence` (`seqID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`protein`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`protein` (
  `accession` VARCHAR(50) NOT NULL,
  `name` VARCHAR(100) NULL,
  PRIMARY KEY (`accession`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`blast`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`blast` (
  `bitScore` INT(25) NULL,
  `evalue` DOUBLE(100,5) NULL,
  `sequence` VARCHAR(1000) NULL,
  `identity` INT(5) NULL,
  `positives` INT(5) NULL,
  `gaps` INT(5) NULL,
  `blastID` INT(25) NOT NULL AUTO_INCREMENT,
  `orf_ORFID` INT(25) NOT NULL,
  `protein_accession` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`blastID`),
  INDEX `fk_blast_orf1_idx` (`orf_ORFID` ASC),
  INDEX `fk_blast_protein1_idx` (`protein_accession` ASC),
  CONSTRAINT `fk_blast_orf1`
    FOREIGN KEY (`orf_ORFID`)
    REFERENCES `mydb`.`orf` (`ORFID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_blast_protein1`
    FOREIGN KEY (`protein_accession`)
    REFERENCES `mydb`.`protein` (`accession`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
