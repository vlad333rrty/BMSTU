USE LAB6
GO


-- BEGIN TRANSACTION
-- 	SELECT * FROM BankAccount
-- 	UPDATE BankAccount SET balance=balance*1.1 WHERE id=1
-- 	WAITFOR DELAY '00:00:05'
	
-- 	SELECT * FROM BankAccount
-- 	SELECT * FROM sys.dm_tran_locks
-- COMMIT TRAN
-- GO 

-- BEGIN TRANSACTION
-- 	SELECT * FROM BankAccount
-- 	UPDATE BankAccount SET balance*=1.1 WHERE id=1
-- 	SELECT * FROM BankAccount
-- 	SELECT * FROM sys.dm_tran_locks
-- COMMIT TRANSACTION
-- GO

-- BEGIN TRANSACTION
--     INSERT INTO BankAccount (id,number,bankName,ownerName,ownerSurname,balance) VALUES(3,'123456789876','City Bank','Igor','Vscie',$228)
--     SELECT * FROM sys.dm_tran_locks
-- COMMIT TRANSACTION
-- go

BEGIN TRANSACTION
    INSERT INTO BankAccount (id,number,bankName,ownerName,ownerSurname,balance) VALUES(4,'123456789872','City Bank','Igor','Vscie',$228)
    SELECT resource_type, resource_subtype, request_mode FROM sys.dm_tran_locks
COMMIT TRANSACTION
GO