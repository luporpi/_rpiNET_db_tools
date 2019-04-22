IF OBJECT_ID('dbo.rpinet_collect_log_tab', 'U') IS NULL
BEGIN
    IF OBJECT_ID('dbo.rpinet_collect_log_tab', 'U') IS NULL
        CREATE TABLE rpinet_collect_log_tab (
            idx INT IDENTITY(1, 1),
            DatabaseName NVARCHAR(128),
            StartDate DATETIME,
            EndDate DATETIME,
            Killed BIT DEFAULT 0
            );
END;
