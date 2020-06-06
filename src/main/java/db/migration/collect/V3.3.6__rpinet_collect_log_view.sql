IF OBJECT_ID('dbo.rpinet_timestamp') IS NULL
    EXEC ('CREATE FUNCTION dbo.rpinet_timestamp (@date DATETIME) RETURNS NVARCHAR(16) AS BEGIN RETURN ''''; END;'
            );
GO
CREATE VIEW [dbo].[rpinet_collect_log_view] (
    idx,
    DatabaseName,
    StartDate,
    EndDate,
    Killed
    )
AS
SELECT idx,
    DatabaseName,
    dbo.rpinet_timestamp(StartDate) StartDate,
    dbo.rpinet_timestamp(EndDate) EndDate,
    Killed
FROM dbo.rpinet_collect_log_tab;
