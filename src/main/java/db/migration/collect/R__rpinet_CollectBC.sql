IF OBJECT_ID('dbo.rpinet_CollectBC') IS NULL
    EXEC ('CREATE PROCEDURE dbo.rpinet_CollectBC AS RETURN 0;');
GO

ALTER PROCEDURE [dbo].[rpinet_CollectBC] @DatabaseName NVARCHAR(128) = NULL,
    @Top INT = 100,
    @Timestamp DATETIME = NULL,
    @MaxRunTime INT = 4,
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

    SELECT @Version = '3.0',
        @VersionDate = '20190420';

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

    DECLARE @erg INT,
        @sql NVARCHAR(MAX),
        @Params NVARCHAR(MAX) = NULL,
        @counter INT = 0,
        @OutputTableName NVARCHAR(258) = NULL,
        @SortOrder VARCHAR(50) = 'CPU',
        @StoredProcName NVARCHAR(128),
        @CurrentDate DATETIME,
        @TimestampString NVARCHAR(50),
        @Reanalyze BIT = 0,
        @Id INT = 0,
        @Killed BIT = 0;

    IF @Timestamp IS NULL
    BEGIN
        SET @Timestamp = GETDATE();
    END;

    EXEC rpinet_CollectLog @Id = @Id OUTPUT,
        @DatabaseName = DatabaseName,
        @StartDate = @Timestamp,
        @Killed = @Killed OUTPUT;

    IF @Killed = 1
        RETURN;

    SET @TimestampString = REPLACE(CONVERT(VARCHAR(8), @Timestamp, 112) + CONVERT(VARCHAR(8), @Timestamp, 114), ':', '')

    RAISERROR (
            N'Collect common data',
            0,
            1
            )
    WITH NOWAIT;

    WHILE @counter < (
            SELECT MAX(idx)
            FROM rpinet_collect_bc_common_tab
            )
    BEGIN
        SET @counter = @counter + 1;

        -- reuse data from first run of sp_BlitzCache
        IF @counter = 2
            SET @Reanalyze = 1
        SET @sql = N'sp_BlitzCache ' + N'@OutputDatabaseName=@OutputDatabaseName,' + N'@OutputSchemaName=@OutputSchemaName,' + 
            N'@OutputTableName=@OutputTableName,' + N'@Databasename=@Databasename,' + N'@Top=@Top,' + 
            N'@SortOrder=@SortOrder,' + N'@Reanalyze=@Reanalyze';
        SET @params = N'@OutputDatabaseName NVARCHAR(258),' + N'@OutputSchemaName NVARCHAR(258),' + 
            N'@OutputTableName NVARCHAR(258),' + N'@Databasename NVARCHAR(128),' + N'@Top INT,' + N'@SortOrder VARCHAR(50),' + 
            N'@Reanalyze BIT';

        SELECT @OutputTableName = 'bc_base__' + REPLACE(LOWER(sort), ' ', '_') + '__' + @TimestampString,
            @SortOrder = sort
        FROM rpinet_collect_bc_common_tab
        WHERE idx = @counter;

        EXEC @erg = sp_executesql @Sql,
            @Params,
            @OutputDatabaseName = @OutputDatabaseName,
            @OutputSchemaName = @OutputSchemaName,
            @OutputTableName = @OutputTableName,
            @Databasename = @Databasename,
            @Top = @Top,
            @SortOrder = @SortOrder,
            @Reanalyze = @Reanalyze

        IF @erg <> 0
            RAISERROR (
                    N'ERROR (%d): %s',
                    0,
                    1,
                    @erg,
                    @OutputTableName
                    )
            WITH NOWAIT;

        IF DATEDIFF(hour, @Timestamp, GETDATE()) >= @MaxRunTime
        BEGIN
            SET @Killed = 1;

            GOTO finish;
        END;
    END;

    RAISERROR (
            N'Collect special data',
            0,
            1
            )
    WITH NOWAIT;

    SET @counter = 0;

    CREATE TABLE #procs (
        idx INT IDENTITY PRIMARY KEY,
        name NVARCHAR(255)
        );

    SET @sql = N'INSERT INTO #procs(name) ' + N'SELECT ROUTINE_NAME FROM ' + QUOTENAME(@DatabaseName) + 
        N'.information_schema.routines WHERE routine_type in (''procedure'', ''function'') ' + N'UNION ' + 
        N'SELECT name FROM ' + QUOTENAME(@DatabaseName) + N'.sys.triggers';

    EXEC @erg = sp_executesql @Sql

    IF @erg <> 0
        RAISERROR (
                N'ERROR (%d): %s',
                0,
                1,
                @erg,
                @sql
                )
        WITH NOWAIT;

    -- prozedurdaten werden vollständig benötigt
    SET @Top = 100

    WHILE @counter < (
            SELECT MAX(idx)
            FROM #procs
            )
    BEGIN
        SET @counter = @counter + 1;
        SET @sql = N'sp_BlitzCache ' + N'@OutputDatabaseName=@OutputDatabaseName,' + N'@OutputSchemaName=@OutputSchemaName,' + 
            N'@OutputTableName=@OutputTableName,' + N'@Databasename=@Databasename,' + N'@Top=@Top,' + 
            N'@StoredProcName=@StoredProcName';
        SET @params = N'@OutputDatabaseName NVARCHAR(258),' + N'@OutputSchemaName NVARCHAR(258),' + 
            N'@OutputTableName NVARCHAR(258),' + N'@Databasename NVARCHAR(128),' + N'@Top INT,' + 
            N'@StoredProcName NVARCHAR(128)';

        SELECT @OutputTableName = N'bc_spec__' + REPLACE(LOWER(name), N' ', N'_') + N'__' + @TimestampString,
            @StoredProcName = name
        FROM #procs
        WHERE idx = @counter;

        RAISERROR (
                N'PARAMS: %s %s %s %s %d %s',
                0,
                1,
                @OutputDatabaseName,
                @OutputTableName,
                @OutputSchemaName,
                @Databasename,
                @Top,
                @StoredProcName
                )
        WITH NOWAIT;

        EXEC @erg = sp_executesql @Sql,
            @Params,
            @OutputDatabaseName = @OutputDatabaseName,
            @OutputSchemaName = @OutputSchemaName,
            @OutputTableName = @OutputTableName,
            @Databasename = @Databasename,
            @Top = @Top,
            @StoredProcName = @StoredProcName

        IF @erg <> 0
            RAISERROR (
                    N'ERROR (%d): %s',
                    0,
                    1,
                    @erg,
                    @OutputTableName
                    )
            WITH NOWAIT;

        IF DATEDIFF(hour, @Timestamp, GETDATE()) >= @MaxRunTime
        BEGIN
            SET @Killed = 1;

            GOTO cleanup;
        END;
    END;

    cleanup:

    DROP TABLE #procs;

    finish:

    EXEC rpinet_CollectLog @Id = @Id,
        @DatabaseName = DatabaseName,
        @EndDate = @Timestamp,
        @Killed = @Killed;
END;
