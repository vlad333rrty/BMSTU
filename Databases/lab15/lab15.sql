USE LAB13_1

DROP TABLE IF EXISTS Book

CREATE TABLE Book(
    id INT PRIMARY KEY NOT NULL,
    name NVARCHAR(50) NOT NULL,
    author NVARCHAR(50) NOT NULL,
    price INT NOT NULL CONSTRAINT c1 CHECK(price>0),
    libraryId INT NOT NULL --FK
)

GO

USE LAB13_2

DROP TABLE IF EXISTS Library

CREATE TABLE Library(
    id INT PRIMARY KEY NOT NULL,
    name NVARCHAR(50) NOT NULL,
    address NVARCHAR(50) NOT NULL UNIQUE,
)

GO

DROP VIEW IF EXISTS LibraryBookView
GO

CREATE VIEW LibraryBookView AS
    SELECT l.id AS [library id],l.name AS [Library name],l.address,b.id AS [Book id],b.name AS [Book name],b.author,b.price
    FROM LAB13_2.dbo.Library l,LAB13_1.dbo.Book b WHERE l.id=b.libraryId
GO

USE LAB13_1

DROP TRIGGER IF EXISTS onBookInsert
GO

CREATE TRIGGER onBookInsert ON Book
INSTEAD OF INSERT
AS
BEGIN
    IF (EXISTS (SELECT libraryId from inserted WHERE libraryId NOT IN (SELECT id FROM LAB13_2.dbo.Library))) BEGIN
        RAISERROR('Wrong FK',-1,11)
    END
    INSERT INTO Book SELECT * FROM inserted
END
GO

DROP TRIGGER IF EXISTS onBookDelete
GO
CREATE TRIGGER onBookDelete ON Book
INSTEAD OF DELETE
AS
BEGIN
    DELETE b FROM Book AS b INNER JOIN deleted AS D ON d.id=b.id 
END
GO

DROP TRIGGER IF EXISTS onBookUpdate
GO

CREATE TRIGGER onBookUpdate ON Book
INSTEAD OF UPDATE
AS
BEGIN
    IF (UPDATE(id))BEGIN
        RAISERROR('Id is not updatable',-1,11)
    END
    IF (UPDATE(libraryId))BEGIN
        IF (EXISTS (SELECT libraryId FROM inserted WHERE libraryId NOT IN (SELECT id FROM LAB13_2.dbo.Library)))BEGIN
            RAISERROR('Wrong FK',-1,11)
        END
    END
    UPDATE Book SET name=i.name,author=i.author,price=i.price,libraryId=i.libraryId FROM Book b INNER JOIN inserted i ON i.id=b.id
END
GO

USE LAB13_2

GO

DROP TRIGGER IF EXISTS onLibraryInsert
GO

CREATE TRIGGER onLibraryInsert ON Library
INSTEAD OF INSERT
AS
BEGIN
    IF (EXISTS (SELECT id FROM inserted WHERE id in (SELECT id FROM Library))) BEGIN
        RAISERROR('Unique constraint: id',-1,11)
    END
    IF (EXISTS (SELECT address FROM inserted WHERE address in (SELECT address FROM Library))) BEGIN
        RAISERROR('Unique constraint: address',-1,11)
    END
    INSERT INTO Library SELECT * FROM inserted
END
GO

DROP TRIGGER IF EXISTS onLibraryDelete
GO

CREATE TRIGGER onLibraryDelete ON Library
INSTEAD OF DELETE
AS
BEGIN
    DELETE FROM LAB13_1.dbo.Book WHERE id IN (SELECT id FROM deleted) --CASCADE
    --IF (EXISTS (SELECT id FROM LAB13_1.dbo.Book WHERE id IN (SELECT id FROM deleted))) RAISERROR('FK constraint',-1,11) --NO ACTION
    --UPDATE LAB13_1.dbo.Book SET libraryId=d.COLUMN_DEFAULT FROM (SELECT COLUMN_DEFAULT FROM INFORMATION_SCHEMA.COLUMNS) d --SET DEFAULT
    --UPDATE LAB13_1.dbo.Book SET libraryId=NULL WHERE libraryId IN (SELECT id FROM deleted) -- SET NULL
    DELETE l FROM Library AS l INNER JOIN deleted d ON d.id=l.id 
END
GO

DROP TRIGGER IF EXISTS onLibraryUpdate
GO

CREATE TRIGGER onLibraryUpdate ON library
INSTEAD OF UPDATE
AS
BEGIN
    IF (UPDATE(id))BEGIN
        RAISERROR('Id is not updatable',-1,11)
    END
    UPDATE Library SET name=i.name,address=i.address FROM Library l INNER JOIN inserted i ON l.id=i.id
END
GO

INSERT INTO Library VALUES (1,'IM. LENINA','MOSCOW'),(2,'BIBLIOTEKA','MINSK')

INSERT INTO LAB13_1.dbo.Book VALUES (1,'BOOK 1','AUTHOR 1',200,1),(2,'BOOK 2','AUTHOR 2',450,2),(3,'BOOK 3','AUTHOR 3',315,1)

GO

DROP PROCEDURE IF EXISTS listStaff

GO
CREATE PROCEDURE listStaff AS
BEGIN
    SELECT * FROM LibraryBookView

    SELECT * FROM LAB13_1.dbo.Book
    SELECT * FROM Library
END
GO

EXECUTE listStaff
GO

DELETE FROM Library WHERE Library.id=2
EXECUTE listStaff 