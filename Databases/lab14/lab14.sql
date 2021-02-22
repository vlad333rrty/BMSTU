USE LAB13_1

DROP TABLE IF EXISTS Book

CREATE TABLE Book(
    id INT PRIMARY KEY NOT NULL,
    name NVARCHAR(50) NOT NULL,
    author NVARCHAR(50) NOT NULL,
    --[year of publication] DATE NOT NULL,
    --price INT NOT NULL CONSTRAINT p CHECK (price>0)
)

GO
USE LAB13_2

DROP TABLE IF EXISTS Book

CREATE TABLE Book(
    id INT PRIMARY KEY NOT NULL,
    --name NVARCHAR(50) NOT NULL,
    --author NVARCHAR(50) NOT NULL,
    [year of publication] DATE NOT NULL,
    price INT NOT NULL CONSTRAINT p CHECK (price>0)
)
GO

DROP VIEW IF EXISTS BookView

GO

CREATE VIEW BookView AS
    SELECT f.id,f.name,f.author,s.[year of publication],s.price FROM
    LAB13_1.dbo.Book AS f, LAB13_2.dbo.Book as s
    WHERE f.id=s.id
GO

DROP TRIGGER IF EXISTS onInsertTrigger

GO

CREATE TRIGGER onInsertTrigger ON BookView
INSTEAD OF INSERT
AS
BEGIN
    IF (EXISTS (SELECT id FROM LAB13_1.dbo.Book INTERSECT SELECT id FROM inserted)) BEGIN
        RAISERROR('Book with such id already exists',-1,11)
    END

    INSERT INTO LAB13_1.dbo.Book SELECT id,name,author FROM inserted
    INSERT INTO LAB13_2.dbo.Book SELECT id,[year of publication],price FROM inserted
END
GO

DROP TRIGGER IF EXISTS onDeleteTrigger

GO

CREATE TRIGGER onDeleteTrigger ON BookView
INSTEAD OF DELETE
AS
BEGIN
    DELETE t FROM LAB13_1.dbo.Book AS t INNER JOIN deleted AS d ON t.id=d.id
    DELETE t FROM LAB13_2.dbo.Book AS t INNER JOIN deleted AS d ON t.id=d.id
END
GO

DROP TRIGGER IF EXISTS onUpdateTrigger

GO

CREATE TRIGGER onUpdateTrigger ON BookView
INSTEAD OF UPDATE
AS
BEGIN
    IF (UPDATE(id) OR UPDATE([year of publication])) BEGIN
        RAISERROR('Wrong update',-1,11)
    END

    UPDATE LAB13_1.dbo.Book SET name=i.name,author=i.author FROM LAB13_1.dbo.Book b INNER JOIN inserted i ON b.id=i.id
    UPDATE LAB13_2.dbo.Book SET price=i.price FROM LAB13_1.dbo.Book b INNER JOIN inserted i ON b.id=i.id
END
GO

INSERT INTO BookView VALUES (1,'A','AUTHOR','2010-01-01',200),(2,'B','BUTHOR','1996-11-17',350)

SELECT * FROM BookView

DELETE FROM BookView WHERE id=1

SELECT * FROM BookView

UPDATE BookView SET price=price*2 WHERE name='B'

SELECT * FROM BookView