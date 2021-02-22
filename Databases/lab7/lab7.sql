USE LAB6;

DROP VIEW IF EXISTS HotelRoomView

GO
-----

CREATE VIEW HotelRoomView AS
    SELECT * FROM HotelRoom WHERE NOT HotelRoom.Is_Empty='YES'
GO
-----
DROP VIEW IF EXISTS ShopProductView

GO
-----
CREATE VIEW ShopProductView AS
    SELECT Shop.Shop_Name,Product.Name AS [Product Name],Product.Price 
    FROM Shop INNER JOIN Product ON Shop.Id=Product.Shop_Id
GO

PRINT 'HERE THE VIEW STARTS'

SELECT * FROM ShopProductView

GO
-----
DROP INDEX IF EXISTS Product.Product_Index

CREATE INDEX Product_Index ON Product(Name) INCLUDE(Price)
GO

SELECT * FROM Product 
SELECT * FROM Product WITH(INDEX(Product_Index))
SELECT Name,Price FROM Product WITH(INDEX(Product_Index))
SELECT Name,Price FROM Product WITH(INDEX(Product_Index)) WHERE (Name='Twix')

GO 
-----
DROP VIEW IF EXISTS HotelRoomIndexView

GO
-----
CREATE VIEW HotelRoomIndexView WITH SCHEMABINDING AS 
SELECT Room_Number,Capacity,Is_Empty
FROM dbo.HotelRoom WHERE HotelRoom.Is_Empty='YES'
GO
-----

CREATE UNIQUE CLUSTERED INDEX Ind ON HotelRoomIndexView(Room_Number,Capacity)
GO

SELECT *  FROM HotelRoomIndexView