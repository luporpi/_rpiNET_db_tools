IF OBJECT_ID('dbo.rpinet_CollectCheck_run_$${DatabaseName}') IS NULL
    EXEC ('CREATE PROCEDURE dbo.rpinet_CollectCheck_run_$${DatabaseName} AS RETURN 0;');
GO

ALTER PROCEDURE [dbo].[rpinet_CollectCheck_run_$${DatabaseName}]
AS
BEGIN
    SET NOCOUNT ON;
    SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;

    -- [rpinet_CollectCheck]
    --     @DatabaseName        Name of the database for which the data is to be collected      
    EXEC rpinet_CollectCheck @DatabaseName = '$${DatabaseName}'
END;
