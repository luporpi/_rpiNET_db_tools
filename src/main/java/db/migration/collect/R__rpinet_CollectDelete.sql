IF OBJECT_ID('dbo.rpinet_CollectDelete') IS NULL
    EXEC ('CREATE PROCEDURE dbo.rpinet_CollectDelete AS RETURN 0;');
GO

ALTER PROCEDURE [dbo].[rpinet_CollectDelete] @Timestamp DATETIME = NULL,
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

    SELECT @Version = '3.1.1',
        @VersionDate = '20190706';

    IF (@VersionCheckMode = 1)
    BEGIN
        RETURN;
    END;

    DECLARE @sql NVARCHAR(MAX),
        @Params NVARCHAR(MAX) = NULL,
        @counter INT = 0,
        @like NVARCHAR(100) = 'b[ci][_]%',
        @timestampstring NVARCHAR(50) = NULL;

    SET @timestampstring = REPLACE(CONVERT(VARCHAR(8), @Timestamp, 112) + CONVERT(VARCHAR(8), @Timestamp, 114), ':', '');

    RAISERROR (
            @timestampstring,
            0,
            1
            );

    CREATE TABLE #tables (
        idx INT IDENTITY PRIMARY KEY,
        name NVARCHAR(255)
        );

    IF @timestamp IS NOT NULL
        SET @like = @like + '[_]' + @timestampstring + '%'
    SET @sql = 'INSERT INTO #tables(name) SELECT name from ' + QUOTENAME(@OutputDatabaseName) + 
        '.sys.objects where type = ''U'' AND name LIKE ''' + @like + ''';'

    EXEC sp_executesql @Sql

    WHILE @counter < (
            SELECT MAX(idx)
            FROM #tables
            )
    BEGIN
        SET @counter = @counter + 1;

        SELECT @sql = 'DROP TABLE ' + QUOTENAME(@OutputDatabaseName) + '.' + QUOTENAME(@OutputSchemaName) + '.' + QUOTENAME(name)
        FROM #tables
        WHERE idx = @counter;

        EXEC sp_executesql @Sql
    END;

    DROP TABLE #tables;
END;
