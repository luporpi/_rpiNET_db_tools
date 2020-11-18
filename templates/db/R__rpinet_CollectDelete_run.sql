IF OBJECT_ID('dbo.rpinet_CollectDelete_run_$${CollectDatabaseName}') IS NULL
    EXEC ('CREATE PROCEDURE dbo.rpinet_CollectDelete_run_$${CollectDatabaseName} AS RETURN 0;');
GO

ALTER PROCEDURE [dbo].[rpinet_CollectDelete_run_$${CollectDatabaseName}]
AS
BEGIN
    /*
    * [rpinet_CollectDelete]
    *     @TimeStamp           (optional) Collection to delete (default = NULL, alle)
    *     @OutputServerName    (optional) Not supported yet
    *     @OutputDatabaseName  (optional) Name of the collection database (default = $${OutputDatabaseName}')
    *     @OutputSchemaName	   (optional) Name of the schema (default = 'dbo')
    */
    DECLARE @timestamp DATETIME = NULL;

    /*
    * set timestamp (StartDate) from rpinet_collect_log_tab
    * (change datetime style if necessary)
    */
    SET @timestamp = NULL;--CONVERT(DATETIME, '<datetime>', 120)

    EXEC rpinet_CollectDelete @TimeStamp = @timestamp;
END;
