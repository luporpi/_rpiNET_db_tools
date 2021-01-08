IF OBJECT_ID('dbo.rpinet_CollectCheck') IS NULL
    EXEC ('CREATE PROCEDURE dbo.rpinet_CollectCheck AS RETURN 0;');
GO

ALTER PROCEDURE [dbo].[rpinet_CollectCheck] @DatabaseName NVARCHAR(258) = NULL,
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

    RAISERROR (
            N'Checking database validity',
            0,
            1
            )
    WITH NOWAIT;

    IF @DatabaseName IS NULL
        OR @DatabaseName = N''
    BEGIN
        RAISERROR (
                'You did not specify a database. Please enter a name and try again.',
                16,
                1
                );

        RETURN;
    END;

    SET @DatabaseName = LTRIM(RTRIM(@DatabaseName));

    IF (DB_ID(@DatabaseName)) IS NULL
        AND @DatabaseName <> N''
    BEGIN
        RAISERROR (
                'The database you specified does not exist. Please check the name and try again.',
                16,
                1
                );

        RETURN;
    END;

    IF (
            SELECT DATABASEPROPERTYEX(@DatabaseName, 'Status')
            ) <> 'ONLINE'
    BEGIN
        RAISERROR (
                'The database you specified is not readable. Please check the name and try again. Better yet, check your server.'
                ,
                16,
                1
                );

        RETURN;
    END;

    DECLARE @sql NVARCHAR(MAX),
        @erg INT = 0,
        @Timestamp DATETIME = NULL,
        @params NVARCHAR(MAX) = '@ergout INT OUTPUT',
        @params2 NVARCHAR(MAX) = NULL,
        @output NVARCHAR(100) = '',
        @TimestampString NVARCHAR(50) = NULL;

    SET @params2 = @params + ', @tsout DATETIME OUTPUT'
    SET @sql = 
        'SELECT @ergout = idx, @tsout = startdate from rpinet_collect_log_tab where idx = (select max(idx) from rpinet_collect_log_tab)'
        ;

    EXEC sp_executesql @Sql,
        @params2,
        @ergout = @erg OUTPUT,
        @tsout = @Timestamp OUTPUT

    SET @TimestampString = dbo.rpinet_timestamp(@Timestamp);
    SET @sql = 'SELECT @ergout = count(name) from sys.objects where type = ''U'' AND name LIKE ''bc_%' + @TimestampString + 
        '''';
    SET @erg = 0;

    EXEC sp_executesql @Sql,
        @params,
        @ergout = @erg OUTPUT

    SET @output = @output + 'BC: ' + CAST(@erg AS NVARCHAR(5))
    SET @sql = N'SELECT @ergout = (count(name)+7) from (' + N'SELECT ROUTINE_NAME AS name FROM ' + QUOTENAME(@DatabaseName) + 
        N'.information_schema.routines WHERE routine_type in (''procedure'', ''function'') ' + N'UNION ' + 
        N'SELECT name FROM ' + QUOTENAME(@DatabaseName) + N'.sys.triggers) a';
    SET @erg = 0;

    EXEC sp_executesql @Sql,
        @params,
        @ergout = @erg OUTPUT

    SET @output = @output + ' of ' + CAST(@erg AS NVARCHAR(5))
    SET @sql = 'SELECT @ergout = count(name) from sys.objects where type = ''U'' AND name LIKE ''bi_%' + @TimestampString + 
        '%''';
    SET @erg = 0;

    EXEC sp_executesql @Sql,
        @params,
        @ergout = @erg OUTPUT

    SET @output = @output + '    BI: ' + CAST(@erg AS NVARCHAR(5))

    IF EXISTS (
            SELECT *
            FROM sys.all_objects
            WHERE name = 'dm_db_stats_histogram'
            )
    BEGIN
        SET @sql = 'SELECT @ergout = (count(name)*5+5) from ' + QUOTENAME(@DatabaseName) + '.sys.objects where type = ''U''';
    END
    ELSE
    BEGIN
        SET @sql = 'SELECT @ergout = (count(name)*4+5) from ' + QUOTENAME(@DatabaseName) + '.sys.objects where type = ''U''';
    END;

    SET @erg = 0;

    EXEC sp_executesql @Sql,
        @params,
        @ergout = @erg OUTPUT

    SET @output = @output + ' of ' + CAST(@erg AS NVARCHAR(5))

    SELECT @output;

    SET @sql = 'SELECT TOP 1 * FROM rpinet_collect_log_view ORDER by 1 DESC'

    EXEC sp_executesql @Sql
END;
