IF OBJECT_ID('dbo.rpinet_CollectJob') IS NULL
    EXEC ('CREATE PROCEDURE dbo.rpinet_CollectJob AS RETURN 0;');
GO

ALTER PROCEDURE [dbo].[rpinet_CollectJob] @DatabaseName NVARCHAR(128) = NULL,
    @Top INT = 100,
    @Timestamp DATETIME = NULL,
    @MaxRunTime INT = 4,
    @BringThePain BIT = 0,
    @OutputServerName NVARCHAR(258) = NULL,
    @OutputDatabaseName NVARCHAR(258) = '$${OutputDatabaseName}',
    @OutputSchemaName NVARCHAR(258) = 'dbo',
    @Version VARCHAR(30) = NULL OUTPUT,
    @VersionDate DATETIME = NULL OUTPUT,
    @VersionCheckMode BIT = 0
AS
BEGIN
    SET NOCOUNT ON;
    SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;

    SELECT @Version = '4.0.7',
        @VersionDate = '20210108';

    IF (@VersionCheckMode = 1)
    BEGIN
        RETURN;
    END;

    IF @Timestamp IS NULL
    BEGIN
        SET @Timestamp = GETDATE();
    END;

    EXEC rpinet_CollectBC @DatabaseName = @DatabaseName,
        @Top = @Top,
        @Timestamp = @Timestamp,
        @MaxRunTime = @MaxRunTime,
        @OutputServerName = @OutputServerName,
        @OutputDatabaseName = @OutputDatabaseName,
        @OutputSchemaName = @OutputSchemaName,
        @VersionDate = @VersionDate OUTPUT

    EXEC rpinet_CollectBI @DatabaseName = @DatabaseName,
        @Timestamp = @Timestamp,
        @MaxRunTime = @MaxRunTime,
        @BringThePain = @BringThePain,
        @OutputServerName = @OutputServerName,
        @OutputDatabaseName = @OutputDatabaseName,
        @OutputSchemaName = @OutputSchemaName,
        @VersionDate = @VersionDate OUTPUT
END;
