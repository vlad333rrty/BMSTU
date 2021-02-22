USE LAB6
GO

DROP TRIGGER IF EXISTS onInsertTrigger 
DROP TRIGGER IF EXISTS onDeleteTrigger
DROP TRIGGER IF EXISTS onUpdateTrigger

GO

CREATE TRIGGER onInsertTrigger ON Product
AFTER INSERT
AS
BEGIN
    IF EXISTS (SELECT Price FROM inserted WHERE Price<0) BEGIN
        RAISERROR('Inappropriate price',-1,1);
        ROLLBACK TRANSACTION;
    END
    RETURN
END
GO

--INSERT INTO Product(Name,Price,Shop_Id) VALUES
--('Twix',-4,2)

GO
CREATE TRIGGER onDeleteTrigger ON Product
INSTEAD OF DELETE
AS
BEGIN
    IF (SELECT COUNT(*) FROM Product)=1 BEGIN
        PRINT 'Last product deleted'
    END
    DELETE FROM Product WHERE Product_Id IN (SELECT Product_Id FROM deleted)
END
GO

--DELETE FROM Product WHERE Product.Name='Chester'
GO

CREATE TRIGGER onUpdateTrigger ON Product
AFTER UPDATE
AS
BEGIN

    DROP TABLE IF EXISTS newTable
    DROP TABLE IF EXISTS oldTable

    SELECT Product_Id,Name as New_Name,Price AS New_Price,Shop_Id as New_Shop_Id INTO newTable FROM inserted
    SELECT * INTO oldTable FROM deleted
    
    SELECT oldTable.*,newTable.New_Name,newTable.New_Price,newTable.New_Shop_Id 
    FROM oldTable INNER JOIN newTable ON oldTable.Product_Id=newTable.Product_Id

END
GO

--UPDATE Product SET name='Twix' WHERE Name='Twix2'

-----------------
USE LAB11;

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

UPDATE ShopProductView SET [Product Name]='kek' WHERE [Shop Name]=N'Дикси'

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
