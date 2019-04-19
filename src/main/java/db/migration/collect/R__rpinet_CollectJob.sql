IF OBJECT_ID('dbo.rpinet_CollectJob') IS NULL
    EXEC ('CREATE PROCEDURE dbo.rpinet_CollectJob AS RETURN 0;');
GO

ALTER PROCEDURE [dbo].[rpinet_CollectJob]
    -- zu analysierende datenbank
    @DatabaseName NVARCHAR(128) = NULL,
    -- anzahl der zeilen für sp_BlitzCache base
    @Top INT = 100,
    -- startzeit
    @Timestamp DATETIME = NULL,
    -- stunden nach denen sich das script automatisch beendet
    @MaxRunTime INT = 4,
    -- bi_base erlauben bei 100+ partitions
    @BringThePain BIT = 0,
    -- noch nicht unterstützt
    @OutputServerName NVARCHAR(258) = NULL,
    -- name der datenbank in die gesammelt wird
    @OutputDatabaseName NVARCHAR(258) = '$${databaseName}',
    -- datenbank schema
    @OutputSchemaName NVARCHAR(258) = 'dbo',
    -- version
    @Version VARCHAR(30) = NULL OUTPUT,
    -- datum der version
    @VersionDate DATETIME = NULL OUTPUT,
    -- nur rückgabe der versionsinformationen
    @VersionCheckMode BIT = 0
AS
BEGIN
    SET NOCOUNT ON;
    SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;

    SELECT @Version = '3.0',
        @VersionDate = '20190420';

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
