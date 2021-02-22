USE master;

DROP DATABASE IF EXISTS LAB11

CREATE DATABASE LAB11 ON(
    NAME=LAB11DB,
    FILENAME='/var/opt/mssql/data/lab11bd.mdf',
    SIZE=10,
    MAXSIZE=UNLIMITED,
    FILEGROWTH=5%
)
LOG ON(
    NAME=LAB11LOG,
    FILENAME='/var/opt/mssql/data/lab11log.ldf',
    SIZE=5MB,
    MAXSIZE=25MB,
    FILEGROWTH=5MB
)
GO

USE LAB11

DROP TABLE IF EXISTS Shop
DROP TABLE IF EXISTS Position
DROP TABLE IF EXISTS Batch
DROP TABLE IF EXISTS Product
DROP TABLE IF EXISTS Employee

GO
CREATE TABLE Shop(
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(25) NOT NULL,
    address NVARCHAR(50) UNIQUE NOT NULL
)

CREATE TABLE Employee(
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(25) NOT NULL,
    surname NVARCHAR(25) NOT NULL,
    age INT CONSTRAINT adult CHECK (age>=18),
    phone NVARCHAR(11)
)

CREATE TABLE Position(
    id INT IDENTITY(1,1) PRIMARY KEY,
    shopId INT,
    employeeId INT,
    CONSTRAINT posShopFK FOREIGN KEY (shopId) REFERENCES Shop(id) on DELETE CASCADE,
    CONSTRAINT employeeFK FOREIGN KEY (employeeId) REFERENCES Employee(id) ON DELETE NO ACTION,
    name NVARCHAR(25) NOT NULL,
    enrollDate DATE NOT NULL,
    dismissalDate DATE
)

CREATE TABLE Product(
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(25) UNIQUE NOT NULL,
)

CREATE TABLE Batch(
    id INT IDENTITY(1,1) PRIMARY KEY,
    shopId INT,
    CONSTRAINT batchShopFK FOREIGN KEY (shopId) REFERENCES Shop(id) on DELETE CASCADE,
    productId INT,
    CONSTRAINT productFK FOREIGN KEY (productId) REFERENCES Product(id) ON DELETE CASCADE,
    price INT NOT NULL,
    number INT NOT NULL DEFAULT(100),
    dateOfManufacture DATE NOT NULL DEFAULT(CAST(GETDATE() AS DATE)),
    useByDate DATE,
    CONSTRAINT unsigned CHECK (number>0 AND price>0)
)

GO
DECLARE @shopId INT
DECLARE @employeeId INT
DECLARE @productId INT

INSERT INTO Shop(name,address) VALUES (N'Дикси',N'Калининец, ул. Пацанская')
SET @shopId=SCOPE_IDENTITY() 

INSERT INTO Employee(name,surname,age) VALUES (N'Галя',N'Иванова',50)
SET @employeeId=SCOPE_IDENTITY()

INSERT INTO Product(name) VALUES (N'Молоко Простоквашино')
SET @productId=SCOPE_IDENTITY()

INSERT INTO Position(shopId,employeeId,name,enrollDate) VALUES (@shopId,@employeeId,N'Кассир','2010-05-12')
INSERT INTO Batch(shopId,productId,price,number,dateOfManufacture,useByDate) VALUES (@shopId,@productId,70,500,'2020-12-01','2020-12-13')

------------------------------

INSERT INTO Shop(name,address) VALUES (N'Перекресток',N'Москва, Осенний б-р, 12к1')
SET @shopId=SCOPE_IDENTITY() 

INSERT INTO Employee(name,surname,age,phone) VALUES (N'Валя',N'Петрова',52,'79262675656')
SET @employeeId=SCOPE_IDENTITY()

INSERT INTO Product(name) VALUES (N'Водка царская')
SET @productId=SCOPE_IDENTITY()

INSERT INTO Position(shopId,employeeId,name,enrollDate) VALUES (@shopId,@employeeId,N'Администратор','2014-08-13')
INSERT INTO Batch(shopId,productId,price,number,dateOfManufacture) VALUES (@shopId,@productId,650,750,'2020-07-01')

GO

SELECT * FROM Shop
SELECT * FROM Product
SELECT * FROM Batch
SELECT * FROM [Position]
SELECT * FROM Employee

GO

DROP VIEW IF EXISTS ShopProductView

GO
CREATE VIEW ShopProductView AS 
SELECT s.name AS [Shop Name],s.address,p.name AS [Product Name],s.dateOfManufacture,s.useByDate,s.price FROM
(SELECT Shop.name,Shop.address,Batch.productId,Batch.dateOfManufacture,Batch.useByDate,Batch.price FROM Shop INNER JOIN Batch ON Shop.id=Batch.shopId) s JOIN
(SELECT Product.name,Product.id FROM Product) p
ON p.id=s.productId

GO
SELECT * FROM ShopProductView

GO

DROP INDEX IF EXISTS Batch.Product_Index

CREATE INDEX Product_Index ON Batch(dateOfManufacture) INCLUDE(useByDate)

GO
SELECT dateOfManufacture,useByDate FROM Batch WITH(INDEX(Product_Index)) WHERE dateOfManufacture LIKE '2020%'

GO

CREATE VIEW EmployeePositionView WITH SCHEMABINDING AS
SELECT Employee.name,Employee.surname,Employee.age,Employee.phone,[Position].enrollDate,[Position].dismissalDate FROM dbo.Employee JOIN dbo.[Position] ON dbo.Employee.id=dbo.[Position].employeeId
GO
SELECT * FROM EmployeePositionView


CREATE UNIQUE CLUSTERED INDEX Ind ON EmployeePositionView(name,surname)
GO

SELECT EmployeePositionView.name,EmployeePositionView.surname,EmployeePositionView.age,EmployeePositionView.phone FROM EmployeePositionView 
WITH(INDEX(Ind)) WHERE name LIKE N'Г%'
GO

DROP PROCEDURE IF EXISTS dbo.employeeSelection

GO

CREATE PROCEDURE dbo.employeeSelection @cursor CURSOR VARYING OUTPUT AS
    SET @cursor = CURSOR
    FORWARD_ONLY STATIC FOR
    SELECT name,surname,age FROM Employee
    OPEN @cursor 
GO

DECLARE @employeeCursor CURSOR
EXECUTE dbo.employeeSelection @cursor = @employeeCursor OUTPUT

DECLARE @name NVARCHAR(25)
DECLARE @surname NVARCHAR(30)
DECLARE @age INT

FETCH NEXT FROM @employeeCursor INTO @name,@surname,@age

WHILE (@@FETCH_STATUS = 0)
BEGIN
    PRINT @name + ', ' + @surname + ', '+CAST(@age AS NVARCHAR(2))
	FETCH NEXT FROM @employeeCursor INTO @name,@surname,@age
END
GO

DROP FUNCTION IF EXISTS dbo.getShopStaff

GO

CREATE FUNCTION getShopStaff()
RETURNS @staff TABLE(
    name NVARCHAR(25),
    surname NVARCHAR(25),
    age INT,
    enrollDate DATE
) AS
BEGIN
    INSERT @staff 
    SELECT Employee.name,Employee.surname,Employee.age,[Position].enrollDate FROM Employee JOIN [Position] ON Employee.id=[Position].employeeId
    RETURN
END
GO

ALTER PROCEDURE dbo.employeeSelection @cursor CURSOR VARYING OUTPUT AS
    SET @cursor=CURSOR
    FORWARD_ONLY STATIC FOR
    SELECT name,surname,age,enrollDate FROM getShopStaff() ORDER BY name,surname 
    OPEN @cursor
GO

DROP PROCEDURE IF EXISTS dbo.listShopStaff

GO

CREATE PROCEDURE dbo.listShopStaff AS
BEGIN
    DECLARE @staffCursor CURSOR
    EXECUTE dbo.employeeSelection @cursor = @staffCursor OUTPUT
    DECLARE @name NVARCHAR(25)
    DECLARE @surname NVARCHAR(30)
    DECLARE @age INT
    DECLARE @enrollDate DATE

    FETCH NEXT FROM @staffCursor INTO @name,@surname,@age,@enrollDate

    WHILE (@@FETCH_STATUS = 0)
    BEGIN
        PRINT @name + ' ' + @surname + ', ' + CAST(@age AS NVARCHAR(2))+', '+CAST(@enrollDate AS NVARCHAR(10))
        FETCH NEXT FROM @staffCursor INTO @name,@surname,@age,@enrollDate
    END
END

GO

EXECUTE dbo.listShopStaff
GO

DROP TRIGGER IF EXISTS ProductInsertTrigger

GO
CREATE TRIGGER ProductInsertTrigger ON Product
AFTER INSERT
AS
BEGIN
    IF (SELECT COUNT(*) FROM inserted)>0 BEGIN
        PRINT 'New Products inserted:'
        SELECT name FROM inserted
    END
END

GO

DROP TRIGGER IF EXISTS onViewInsert

GO
CREATE TRIGGER onViewInsert ON ShopProductView
INSTEAD OF INSERT
AS
BEGIN
    INSERT INTO Shop(name,address) SELECT [Shop Name],address FROM inserted WHERE NOT (SELECT address FROM inserted) IN (SELECT address FROM Shop)
    INSERT INTO Product(name) SELECT [Product Name] FROM inserted WHERE NOT (SELECT [Product Name] FROM inserted) IN (SELECT name FROM Product)
    INSERT INTO Batch(shopId,productId,price,dateOfManufacture,useByDate) SELECT s.shopId,p.productId,i.price,i.dateOfManufacture,i.useByDate FROM 
    (SELECT Shop.id AS shopId FROM Shop JOIN inserted ON inserted.address=Shop.address) s,
    (SELECT Product.id AS productId,name FROM Product JOIN inserted ON Product.name=inserted.[Product Name]) p,
    inserted i
END
GO

INSERT INTO ShopProductView([Shop Name],address,[Product Name],dateOfManufacture,useByDate,price) VALUES
(N'Пятерочка',N'Москва, Бауманская ул., 58A','Twix','2020-12-05','2021-01-05',45)

GO

CREATE FUNCTION getShopId(@address NVARCHAR(50)) RETURNS INT
BEGIN
    RETURN (SELECT id FROM Shop WHERE address=@address)
END

GO
DROP TRIGGER IF EXISTS onViewUpdate

GO
CREATE TRIGGER onViewUpdate ON ShopProductView
INSTEAD OF UPDATE
AS
BEGIN
    IF (UPDATE([Shop Name]) OR UPDATE(address) OR UPDATE(dateOfManufacture) OR UPDATE(useByDate))BEGIN
        RAISERROR('Cannot change',-1,11)
    END

    DROP TABLE IF EXISTS tempStore

    SELECT i.[Shop Name],i.address,i.[Product Name] AS [new product name],i.price AS [new price],
    d.[Product Name] AS [old product name],d.price AS [old price],
    p.id 
    INTO tempStore
    FROM inserted i INNER JOIN deleted d ON i.[Shop Name]=d.[Shop Name]
    LEFT JOIN Product p ON p.name=d.[Product Name]

    SELECT * FROM tempStore

    UPDATE Batch SET price=t.[new price] FROM Batch b INNER JOIN tempStore t ON (t.id=b.productId AND b.shopId=dbo.getShopId(t.address))
    UPDATE Product SET name=t.[new product name] FROM Product p INNER JOIN tempStore t ON (t.id=p.id)

END
GO

SELECT * FROM ShopProductView
GO

DROP TRIGGER IF EXISTS onViewDelete 

GO
CREATE TRIGGER onViewDelete ON ShopProductView
INSTEAD OF DELETE
AS
BEGIN
    DELETE FROM Batch WHERE id IN (SELECT id FROM Batch WHERE
        Batch.shopId IN (SELECT id FROM Shop WHERE Shop.name IN (SELECT [Shop name] FROM deleted)) 
        AND 
        Batch.productId IN (SELECT id FROM Product WHERE Product.name IN (SELECT [Product name] FROM deleted))
        AND
        Batch.dateOfManufacture IN (SELECT dateOfManufacture FROM deleted))
END

GO

UPDATE Product SET name=N'Аленка' WHERE name='Twix'

SELECT * FROM ShopProductView

GO

INSERT INTO Product(name) VALUES ('Twix')
DELETE FROM Product WHERE name=N'Аленка'

SELECT * FROM Product
GO 

DROP PROCEDURE IF EXISTS dbo.makeOrder

GO

DROP FUNCTION IF EXISTS dbo.getProductId

GO

CREATE FUNCTION getProductId(@productName NVARCHAR(25)) RETURNS INT
BEGIN
    RETURN (SELECT id FROM Product WHERE Product.name=@productName)
END

GO

CREATE PROCEDURE makeOrder(@shopId INT,@productName NVARCHAR(25),@price INT,@number INT) AS
BEGIN
    DECLARE @productId INT
    SET @productId=dbo.getProductId(@productName)
    PRINT @productId
    INSERT INTO BATCH(shopId,productId,price,number)
    VALUES (@shopId,@productId,@price,@number)
END
GO

SELECT * FROM ShopProductView

EXECUTE makeOrder 1,'Twix',50,100

SELECT * FROM ShopProductView

GO

DROP PROCEDURE IF EXISTS dbo.cancelOrder

GO
CREATE PROCEDURE cancelOrder(@shopId INT,@productName NVARCHAR(25)) AS
BEGIN
    DECLARE @productId INT
    SET @productId=dbo.getProductId(@productName)
    DELETE FROM Batch WHERE Batch.id=(SELECT DISTINCT Batch.id FROM Batch WHERE Batch.productId=@productId AND Batch.shopId=@shopId)
END
GO

SELECT * FROM ShopProductView
EXECUTE cancelOrder 1,'Twix'
SELECT * FROM ShopProductView

GO

DROP FUNCTION IF EXISTS dbo.getProductName

GO
CREATE FUNCTION getProductName(@productId INT) RETURNS NVARCHAR(25)
BEGIN
    RETURN (SELECT name FROM Product WHERE id=@productId)
END

GO

DROP PROCEDURE IF EXISTS dbo.getProductStatistics

GO
CREATE PROCEDURE getProductStatistics(@lowerBound INT,@upperBound INT) AS
BEGIN
    SELECT dbo.getProductName(productId) AS [Product name],
    COUNT(shopId) AS [Vendors number],
    MIN(price) AS [Minimal price],
    MAX(price) AS [Maximal price],
    AVG(price) AS [Average price],
    SUM(number) AS [Total number],
    MAX(dateOfManufacture) AS [Newest batch]
    FROM Batch GROUP BY [productId] HAVING(AVG(price) BETWEEN @lowerBound AND @upperBound)
END
GO

SELECT * FROM ShopProductView

EXECUTE dbo.getProductStatistics 15,700

GO

INSERT INTO ShopProductView([Shop Name],address,[Product Name],dateOfManufacture,price)
VALUES (N'Дикси',N'Селятино, Московская обл.',N'Водка царская','2020-10-11',670)

GO

SELECT * FROM ShopProductView

EXECUTE dbo.getProductStatistics 15,700
GO

INSERT INTO Employee(name,surname,age) VALUES ('Steve','Jobs',56)
INSERT INTO [Position](employeeId,name,enrollDate) VALUES (SCOPE_IDENTITY(),N'Консультант','2011-10-06')

SELECT * FROM Shop RIGHT JOIN [Position] ON Shop.id=[Position].shopId

SELECT * FROM Shop RIGHT JOIN Employee FULL OUTER JOIN [Position] ON [Position].employeeId=Employee.id ON Shop.id=[Position].shopId

GO

DROP FUNCTION IF EXISTS getShopName

GO
CREATE FUNCTION getShopName(@shopId INT) RETURNS NVARCHAR(25)
BEGIN
    RETURN (SELECT name FROM Shop WHERE id=@shopId)
END

GO

DROP PROCEDURE IF EXISTS makeCall

GO
CREATE PROCEDURE makeCall(@name NVARCHAR(25),@surname NVARCHAR(25)) AS
BEGIN
    PRINT @name + ' ' + @surname + ' is busy now '
END

GO
DROP PROCEDURE IF EXISTS callEmployee

GO
CREATE PROCEDURE callEmployee(@name NVARCHAR(25),@surname NVARCHAR(25)) AS
BEGIN
    IF (EXISTS (SELECT * FROM Employee WHERE Employee.name=@name AND Employee.surname=@surname)) BEGIN
        EXECUTE dbo.makeCall @name,@surname
    END
END

EXECUTE callEmployee 's','d'

GO

DROP PROCEDURE IF EXISTS listPerishableProducts

GO
CREATE PROCEDURE listPerishableProducts AS
BEGIN
    SELECT dbo.getProductName(Batch.productId) AS Name FROM Batch WHERE Batch.useByDate IS NOT NULL 
END
GO

DROP PROCEDURE IF EXISTS dateDictionary

GO
CREATE PROCEDURE dateDictionary AS 
BEGIN
    SELECT [Position].enrollDate AS date,'Employee enrolled' AS event FROM [Position] UNION ALL SELECT Batch.dateOfManufacture,'Product delivered' FROM Batch  
END

GO

EXECUTE dateDictionary
GO

DROP PROCEDURE IF EXISTS listCommonPositions

GO
CREATE PROCEDURE listCommonPositions(@firstShopName NVARCHAR(25),@secondShopName NVARCHAR(25)) AS
BEGIN
    SELECT name FROM Position WHERE dbo.getShopName(id)=@firstShopName 
    INTERSECT
    SELECT name FROM Position WHERE dbo.getShopName(id)=@secondShopName
END
GO

EXECUTE listCommonPositions N'Дикси',N'Перекресток'

GO
DROP PROCEDURE IF EXISTS listUniquePositions

GO

CREATE PROCEDURE listUniquePositions(@shopName NVARCHAR(25)) AS
BEGIN
    SELECT name FROM Position EXCEPT SELECT name FROM Position WHERE NOT dbo.getShopName(id)=@shopName
END

GO

EXECUTE listUniquePositions N'Дикси'

GO
DROP PROCEDURE IF EXISTS checkUniqueShops

GO
CREATE PROCEDURE checkUniqueShops(@firstAddressPattern NVARCHAR(25),@secondAddressPattern NVARCHAR(25)) AS
BEGIN
    SELECT name FROM Shop WHERE address LIKE @firstAddressPattern UNION SELECT name FROM Shop WHERE address LIKE @secondAddressPattern
END
GO

EXECUTE  checkUniqueShops N'%Москва%',N'%Бауманская%'
GO

DROP TABLE IF EXISTS Customer
CREATE TABLE Customer(
    name NVARCHAR(25),
    surname NVARCHAR(25),
    age INT
)
GO

INSERT INTO Customer(name,surname,age) VALUES
(N'Галя',N'Иванова',32),('Arthas','Menethil',30)

GO
SELECT name,surname FROM Customer INTERSECT SELECT name,surname FROM Employee
GO