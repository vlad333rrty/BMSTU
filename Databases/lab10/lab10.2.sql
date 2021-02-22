USE LAB6
GO
DROP TABLE IF EXISTS BankAccount
GO

CREATE TABLE BankAccount(
    id INT PRIMARY KEY,
    number NVARCHAR(12) NOT NULL UNIQUE,
    bankName NVARCHAR(20) not NULL,
    ownerName NVARCHAR(20) NOT NULL,
    ownerSurname NVARCHAR(20) NOT NULL,
    balance money NOT NULL
)

INSERT INTO BankAccount VALUES 
(1,'123456781011',N'Сбербанк','Vladislav','Muryzhnikov',$1000000),
(2,'123456781012','City bank','Gordon','Freeman',$123456789)

GO