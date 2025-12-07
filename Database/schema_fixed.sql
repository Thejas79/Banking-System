DROP DATABASE IF EXISTS bank_db;

CREATE DATABASE bank_db;

USE bank_db;


CREATE TABLE users
(
    username VARCHAR(50) NOT NULL PRIMARY KEY,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE usersInfo
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL,
    firstName   VARCHAR(50)  NOT NULL,
    lastName    VARCHAR(50)  NOT NULL,
    phoneNumber CHAR(11)     NOT NULL,
    address     VARCHAR(50)  NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    FOREIGN KEY (username) REFERENCES users (username)
);

CREATE TABLE bankAccountTypes
(
    id                       INT AUTO_INCREMENT PRIMARY KEY,
    name                     VARCHAR(50) NOT NULL,
    minimumBalanceInAccount  INT         NOT NULL,
    fees                     INT         NOT NULL,
    interest                 INT         NOT NULL,
    minimumBalanceToInterest INT         NOT NULL,
    withdrawalLimit          INT         NOT NULL
);

INSERT INTO bankAccountTypes (name, minimumBalanceInAccount, fees, interest, minimumBalanceToInterest,
                              withdrawalLimit)
VALUES ('Basic', 0, 2, 4, 1000, 50),
       ('Saving', 500, 3, 5, 750, 150);

CREATE TABLE status
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(10)
);

INSERT INTO status (name)
VALUES ('ACTIVE'),
       ('CLOSED'),
       ('PENDING');

CREATE TABLE currencies
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(3)  NOT NULL,
    name   VARCHAR(20) NOT NULL
);

INSERT INTO currencies(name, symbol)
VALUES ('Dollar', '$'),
       ('Euro', '€'),
       ('Japanese yen', '¥'),
       ('Great British Pound', '£');

CREATE TABLE userBankAccount
(
    id                       INT AUTO_INCREMENT PRIMARY KEY,
    username                 VARCHAR(50) NOT NULL,
    typeID                   INT         NOT NULL,
    currencyID               INT         NOT NULL,
    statusID                 INT         NOT NULL,
    balance                  INT         NOT NULL,
    createdAt                TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    withdrawalLimit          INT         NOT NULL,
    lastResetWithdrawalLimit TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt                TIMESTAMP            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (typeID) REFERENCES bankAccountTypes (id),
    FOREIGN KEY (username) REFERENCES users (username),
    FOREIGN KEY (statusID) REFERENCES status (id),
    FOREIGN KEY (currencyID) REFERENCES currencies (id),
    INDEX idx_balance (balance),
    INDEX idx_lastResetWithdrawalLimit (lastResetWithdrawalLimit)
);

CREATE TABLE transactionTypes
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

INSERT INTO transactionTypes(name)
VALUES ('DEPOSIT'),
       ('WITHDRAWAL'),
       ('TRANSFER');

CREATE TABLE transactions
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50) NOT NULL,
    amount    INT         NOT NULL,
    typeID    INT         NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES users (username),
    FOREIGN KEY (typeID) REFERENCES transactionTypes (id)
);

CREATE TABLE activityTypes
(
    id   INT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

INSERT INTO activityTypes (name)
VALUES ('LOGIN'),
       ('LOGOUT'),
       ('ViewBankAccounts'),
       ('ViewTransactionHistory'),
       ('ChangePassword'),
       ('UpdatePersonalInfo'),
       ('OpenBankAccount'),
       ('CloseBankAccount'),
       ('DEPOSIT'),
       ('WITHDRAWAL'),
       ('TRANSFER');

CREATE TABLE logs
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50) NOT NULL,
    activityID INT         NOT NULL,
    createdAt  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (username) REFERENCES users (username),
    FOREIGN KEY (activityID) REFERENCES activityTypes (id)
);

-- Triggers and Procedures
DELIMITER //

CREATE TRIGGER set_bankAccount_pending_status
    BEFORE INSERT
    ON userBankAccount
    FOR EACH ROW
BEGIN
    SET NEW.statusID = 3;
END//

CREATE EVENT reset_withdrawal_limit
    ON SCHEDULE
        EVERY 1 DAY
            STARTS CURRENT_TIMESTAMP
    DO
    UPDATE userBankAccount
    SET withdrawalLimit          = (SELECT withdrawalLimit FROM bankAccountTypes WHERE id = typeID),
        lastResetWithdrawalLimit = CURRENT_TIMESTAMP
    WHERE lastResetWithdrawalLimit <= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 30 DAY)//

CREATE TRIGGER set_default_withdrawalLimit
    BEFORE INSERT
    ON userBankAccount
    FOR EACH ROW
BEGIN
    SET NEW.withdrawalLimit = (SELECT withdrawalLimit FROM bankAccountTypes WHERE id = NEW.typeID);
END//

CREATE TRIGGER decrease_withdrawalLimit
    BEFORE UPDATE
    ON userBankAccount
    FOR EACH ROW
BEGIN
    IF (NEW.balance < OLD.balance) THEN
        SET NEW.withdrawalLimit = NEW.withdrawalLimit - 1;
    END IF;
END//

CREATE TRIGGER insert_changePassword_log
    AFTER UPDATE
    ON users
    FOR EACH ROW
BEGIN
    INSERT INTO logs(username, activityID) VALUES (NEW.username, 5);
END//

CREATE TRIGGER insert_changePersonalInfo_log
    AFTER UPDATE
    ON usersInfo
    FOR EACH ROW
BEGIN
    INSERT INTO logs(username, activityID) VALUES (NEW.username, 6);
END//

CREATE TRIGGER insert_openBankAccount_log
    AFTER INSERT
    ON userBankAccount
    FOR EACH ROW
BEGIN
    INSERT INTO logs(username, activityID) VALUES (NEW.username, 7);
END//

CREATE TRIGGER insert_closeBankAccount_log
    AFTER UPDATE
    ON userBankAccount
    FOR EACH ROW
BEGIN
    IF (NEW.statusID = 2) THEN
        INSERT INTO logs(username, activityID) VALUES (NEW.username, 8);
    END IF;
END//

-- Procedures
CREATE PROCEDURE insertLog(p_username VARCHAR(50), p_activityId INT)
BEGIN
    INSERT INTO logs (username, activityID) VALUES (p_username, p_activityId);
END//

CREATE PROCEDURE insertTransaction(p_username VARCHAR(50), p_amount INT, p_typeID INT)
BEGIN
    INSERT INTO transactions (username, amount, typeID) VALUES (p_username, p_amount, p_typeID);
END//

CREATE PROCEDURE updateBankAccountStatus(p_statusID INT, p_id INT)
BEGIN
    UPDATE userBankAccount SET statusID = p_statusID WHERE id = p_id;
END//

CREATE PROCEDURE updateBankAccountBalance(p_amount INT, p_id INT)
BEGIN
    UPDATE userBankAccount SET balance = balance + p_amount WHERE id = p_id;
END//

CREATE PROCEDURE getUserPassword(p_username VARCHAR(50))
BEGIN
    SELECT password FROM Users WHERE username = p_username;
END//

CREATE PROCEDURE getUserInfo(p_username VARCHAR(50))
BEGIN
    SELECT * FROM usersInfo WHERE username = p_username;
END//

CREATE PROCEDURE getNumberOfUserLogs(p_username VARCHAR(50))
BEGIN
    SELECT COUNT(*) AS numberOfLogs FROM logs WHERE username = p_username;
END//

CREATE PROCEDURE getUserLogs(p_username VARCHAR(50), p_limit INT, p_offset INT)
BEGIN
    SELECT activityID, createdAt FROM logs WHERE username = p_username ORDER BY id DESC LIMIT p_limit OFFSET p_offset;
END//

CREATE PROCEDURE updateUserPassword(p_password VARCHAR(50), p_username VARCHAR(50))
BEGIN
    UPDATE users SET password = p_password WHERE username = p_username;
END//

CREATE PROCEDURE updateUserInfo(
    IN p_columnName VARCHAR(30),
    IN p_columnValue VARCHAR(255),
    IN p_username VARCHAR(50)
)
BEGIN
    SET @query = CONCAT('UPDATE usersInfo SET ', p_columnName, ' = ?', ' WHERE username = ?');
    PREPARE statement FROM @query;
    SET @columnValue = p_columnValue;
    SET @username = p_username;
    EXECUTE statement USING @columnValue, @username;
    DEALLOCATE PREPARE statement;
END//

CREATE PROCEDURE insertBankAccount(p_username VARCHAR(50), p_typeID INT, p_currencyID INT, p_balance INT)
BEGIN
    INSERT INTO userBankAccount (username, typeID, currencyID, balance)
    VALUES (p_username, p_typeID, p_currencyID, p_balance);
END//

CREATE PROCEDURE checkUniqueEmail(p_email VARCHAR(255))
BEGIN
    SELECT NOT EXISTS(SELECT email FROM usersInfo WHERE email = p_email) AS isUniqueEmail;
END//

CREATE PROCEDURE insertUser(p_username VARCHAR(50), p_password VARCHAR(50))
BEGIN
    INSERT INTO users (username, password) VALUES (p_username, p_password);
END//

CREATE PROCEDURE insertUserInfo(p_username VARCHAR(50), p_firstName VARCHAR(50), p_lastName VARCHAR(50),
                                p_phoneNumber CHAR(11), p_address VARCHAR(50), p_email VARCHAR(255))
BEGIN
    INSERT INTO usersInfo (username, firstName, lastName, phoneNumber, address, email)
    VALUES (p_username, p_firstName, p_lastName, p_phoneNumber, p_address, p_email);
END//

CREATE PROCEDURE getUserTransactions(p_username VARCHAR(50), p_limit INT, p_offset INT)
BEGIN
    SELECT amount, typeID, createdAt
    FROM transactions
    WHERE username = p_username
    ORDER BY id DESC
    LIMIT p_limit OFFSET p_offset;
END//

CREATE PROCEDURE getNumberOfUserTransactions(p_username VARCHAR(50))
BEGIN
    SELECT COUNT(*) AS numberOfTransactions FROM transactions WHERE username = p_username;
END//

CREATE PROCEDURE checkValidBankAccount(p_id INT)
BEGIN
    SELECT EXISTS(SELECT id FROM userBankAccount WHERE id = p_id AND statusID = 1) AS isValidBankAccount;
END//

CREATE PROCEDURE getUserBankAccounts(p_username VARCHAR(50))
BEGIN
    SELECT id, typeID, currencyID, statusID, balance, withdrawalLimit, createdAt
    FROM userBankAccount
    WHERE username = p_username;
END//

DELIMITER ;
