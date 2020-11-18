IF OBJECT_ID('dbo.rpinet_CollectCheck_run_$${CollectDatabaseName}') IS NULL
    EXEC ('CREATE PROCEDURE dbo.rpinet_CollectCheck_run_$${CollectDatabaseName} AS RETURN 0;');
GO

ALTER PROCEDURE [dbo].[rpinet_CollectCheck_run_$${CollectDatabaseName}]
AS
BEGIN
    SET NOCOUNT ON;
    SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;

    -- [rpinet_CollectCheck]
    --     @DatabaseName        Name of the database for which the data is to be collected      
    EXEC rpinet_CollectCheck @DatabaseName = '$${CollectDatabaseName}'
END;
