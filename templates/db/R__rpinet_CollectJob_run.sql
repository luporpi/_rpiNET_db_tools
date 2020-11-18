IF OBJECT_ID('dbo.rpinet_CollectJob_run_$${CollectDatabaseName}') IS NULL
    EXEC ('CREATE PROCEDURE dbo.rpinet_CollectJob_run_$${CollectDatabaseName} AS RETURN 0;');
GO

ALTER PROCEDURE [dbo].[rpinet_CollectJob_run_$${CollectDatabaseName}]
AS
BEGIN
    /*
    * [rpinet_CollectJob]
    *     @DatabaseName        Name of the database for which the data is to be collected
    *     @Top                 (optional) Number of lines for output of sp_BlitzCache (default = 100)
    *     @Timestamp           (optional) Timestamp of execution
    *     @MaxRunTime          (optional) Hours after the script ends automatically (default = null - until finished)
    *     @BringThePain        (optional) Execution of bi_base on databases with more than 100 partitions (default = 0)
    *     @OutputServerName    (optional) Not supported yet
    *     @OutputDatabaseName  (optional) Name of the collection database (default = '$${OutputDatabaseName}')
    *     @OutputSchemaName	   (optional) Name of the schema (default = 'dbo')
    */
    DECLARE @timestamp DATETIME = NULL;

    /*
    * set timestamp (StartDate)
    */
    SET @timestamp = GETDATE();

    EXEC rpinet_CollectJob @DatabaseName = '$${CollectDatabaseName}',
        @Top = 100,
        @Timestamp = @timestamp,
        @MaxRunTime = 4;
END;
