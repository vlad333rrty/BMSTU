USE LAB6
GO

DROP PROCEDURE IF EXISTS selection
GO

CREATE PROCEDURE dbo.selection @cursor CURSOR VARYING OUTPUT AS
    SET @cursor = CURSOR
    FORWARD_ONLY STATIC FOR
    SELECT Name,Price FROM Product
    OPEN @cursor

GO

DECLARE @product_cursor CURSOR
EXECUTE dbo.selection @cursor = @product_cursor OUTPUT

FETCH NEXT FROM @product_cursor

WHILE (@@FETCH_STATUS = 0)
BEGIN
	FETCH NEXT FROM @product_cursor
END


CLOSE @product_cursor
DEALLOCATE @product_cursor

GO
-----

DROP FUNCTION IF EXISTS getScore

DROP VIEW IF EXISTS rndView

GO
CREATE VIEW rndView AS
SELECT RAND() rndResult

GO

CREATE FUNCTION getScore(@a int,@b int)
	RETURNS INT
	AS
		BEGIN
			DECLARE @res FLOAT
            SELECT @res=rndResult FROM rndView
            DECLARE @number INT
            DECLARE @t INT
            SET @t=@b-@a
            SET @number=ROUND(@res*@t,0)
            SET @number=@number+@a
            RETURN @number
		END
GO

DROP PROCEDURE IF EXISTS dbo.selectAndAnalyse 

GO

CREATE PROCEDURE dbo.selectAndAnalyse @cursor CURSOR VARYING OUTPUT AS
    SET @cursor = CURSOR FORWARD_ONLY STATIC FOR
    SELECT Name,Price,dbo.getScore(1,10) as Score FROM Product
    OPEN @cursor
GO

DECLARE @s_cursor CURSOR
EXECUTE dbo.selectAndAnalyse @cursor=@s_cursor OUTPUT

WHILE (@@FETCH_STATUS = 0)
	BEGIN
		FETCH NEXT FROM @s_cursor;
	END

CLOSE @s_cursor
DEALLOCATE @s_cursor

GO

DROP FUNCTION IF EXISTS getVerdict

GO
CREATE FUNCTION getVerdict(@price int)
    RETURNS VARCHAR
    AS
        BEGIN
            DECLARE @verdict INT
            IF @price>25 SET @verdict=0 ELSE SET @verdict=1
            RETURN (@verdict)
        END

GO

DROP PROCEDURE IF EXISTS dbo.updatedProcedure
GO

CREATE PROCEDURE dbo.updatedProcedure AS
    DECLARE @j CURSOR
    DECLARE @name NVARCHAR(25)
    DECLARE @price INT

    EXECUTE dbo.selection @cursor = @j OUTPUT

    FETCH NEXT FROM @j INTO @name,@price

    WHILE (@@FETCH_STATUS=0)
    BEGIN
        IF (dbo.getVerdict(@price)>0)
            PRINT @name + ' may be doshik'
        ELSE
            print @name + ' may not be doshik'
        FETCH NEXT FROM @j INTO @name,@price
    END

    CLOSE @j
    DEALLOCATE @j
GO

EXECUTE dbo.updatedProcedure

GO

DROP FUNCTION IF EXISTS dbo.tableFunction

GO

CREATE FUNCTION tableFunction() 
RETURNS @resultTable TABLE (
    Name NVARCHAR(25) NOT NULL,
    Price INT NOT NULL,
    Score INT NOT NULL
)
AS
    BEGIN
        INSERT @resultTable 
        SELECT Name,Price,dbo.getScore(1,10) as Score 
        FROM Product WHERE dbo.getVerdict(Price)=0 
        RETURN 
    END
GO

ALTER PROCEDURE dbo.selectAndAnalyse @cursor CURSOR VARYING OUTPUT
AS
    SET @cursor = CURSOR 
	FORWARD_ONLY STATIC FOR 
	SELECT * FROM dbo.tableFunction()
	OPEN @cursor
GO

DECLARE @table_cursor CURSOR
EXECUTE dbo.selectAndAnalyse @cursor = @table_cursor OUTPUT

DECLARE @name NVARCHAR(20)
DECLARE @price INT
DECLARE @i INT

FETCH NEXT FROM @table_cursor INTO @name,@price,@i

WHILE (@@FETCH_STATUS = 0)
	BEGIN
		FETCH NEXT FROM @table_cursor INTO @name,@price,@i
        PRINT @name+ ' '+CAST(@price AS NVARCHAR(20))+' '+CAST(@i as NVARCHAR(20))
	END
CLOSE @table_cursor
DEALLOCATE @table_cursor
GO