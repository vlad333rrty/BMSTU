USE master;

DROP DATABASE IF EXISTS LAB12;


CREATE DATABASE LAB12 ON(
    NAME=LAB12DB,
    FILENAME='/var/opt/mssql/data/lab12bd.mdf',
    SIZE=10,
    MAXSIZE=UNLIMITED,
    FILEGROWTH=5%
)
LOG ON(
    NAME=LAB12LOG,
    FILENAME='/var/opt/mssql/data/lab12log.ldf',
    SIZE=5MB,
    MAXSIZE=25MB,
    FILEGROWTH=5MB
)
GO

USE LAB12;

DROP TABLE IF EXISTS Shop
DROP TABLE IF EXISTS Customer

CREATE TABLE Shop(
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(25),
    address NVARCHAR(50),
    score INT
)

CREATE TABLE Customer(
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(25),
    surname NVARCHAR(25),
    age INT
)

GO

INSERT INTO Shop VALUES ('Shop1','Address1',5),('Shop2','Address2',4)
INSERT INTO Customer VALUES ('Name1','Surname1',25),('Name2','Surname2',23)

SELECT * FROM Shop
SELECT * FROM Customer
