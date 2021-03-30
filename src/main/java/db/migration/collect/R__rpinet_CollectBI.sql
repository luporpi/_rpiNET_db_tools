IF OBJECT_ID('dbo.rpinet_CollectBI') IS NULL
    EXEC ('CREATE PROCEDURE dbo.rpinet_CollectBI AS RETURN 0;');
GO

ALTER PROCEDURE [dbo].[rpinet_CollectBI] @DatabaseName NVARCHAR(128) = NULL,
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

    SELECT @Version = '4.0.8',
        @VersionDate = '20210330';

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
        @SchemaName NVARCHAR(128) = 'dbo',
        @TableName NVARCHAR(128),
        @TimestampString NVARCHAR(50),
        @Id INT = 0,
        @Killed BIT = 0,
        @dsql NVARCHAR(MAX),
        @Rowcount BIGINT = 0,
        @bitString NVARCHAR(1) = CAST(@BringThePain AS NVARCHAR(1));;

    IF @Timestamp IS NULL
    BEGIN
        SET @Timestamp = GETDATE();
    END;

    EXEC rpinet_CollectLog @Id = @Id OUTPUT,
        @DatabaseName = @DatabaseName,
        @StartDate = @Timestamp,
        @Killed = @Killed OUTPUT;

    IF @Killed = 1
        RETURN;

    SET @TimestampString = dbo.rpinet_timestamp(@Timestamp);

    RAISERROR (
            N'Collect common data',
            0,
            1
            )
    WITH NOWAIT;

    /* Count the total number of partitions */
    SET @dsql = N'SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
            SELECT @RowcountOUT = SUM(1) FROM ' + 
        QUOTENAME(@DatabaseName) + '.sys.partitions WHERE partition_number > 1 OPTION    ( RECOMPILE );';

    EXEC sp_executesql @dsql,
        N'@RowcountOUT BIGINT OUTPUT',
        @RowcountOUT = @Rowcount OUTPUT;

    IF @Rowcount > 100
        AND @BringThePain = 0
    BEGIN
        RAISERROR (
                N'Skip common data: Found 100+ partitions! Set @BringThePain = 1.',
                0,
                1
                )
        WITH NOWAIT;
    END;
    ELSE
    BEGIN
        WHILE @counter < 5
        BEGIN
            SET @sql = N'sp_BlitzIndex ' + '@OutputDatabaseName=@OutputDatabaseName,' + '@OutputSchemaName=@OutputSchemaName,' + 
                '@OutputTableName=@OutputTableName,' + '@Databasename=@Databasename,' + '@Mode=@mode,' + 
                '@BringThePain=@BringThePain';
            SET @params = N'@OutputDatabaseName NVARCHAR(258),' + '@OutputSchemaName NVARCHAR(258),' + 
                '@OutputTableName NVARCHAR(258),' + '@Databasename NVARCHAR(128),' + '@Mode TINYINT,' + '@BringThePain BIT';
            SET @OutputTableName = N'bi_base__' + CAST(@counter AS NVARCHAR(1)) + '__' + @TimestampString;

            RAISERROR (
                    N'PARAMS: %s %s %s %s %d %s',
                    0,
                    1,
                    @OutputDatabaseName,
                    @OutputTableName,
                    @OutputSchemaName,
                    @Databasename,
                    @Counter,
                    @bitString
                    )
            WITH NOWAIT;

            EXEC @erg = sp_executesql @Sql,
                @Params,
                @OutputDatabaseName = @OutputDatabaseName,
                @OutputSchemaName = @OutputSchemaName,
                @OutputTableName = @OutputTableName,
                @Databasename = @Databasename,
                @Mode = @counter,
                @BringThePain = @BringThePain

            IF @erg <> 0
                RAISERROR (
                        N'ERROR (%d): %s',
                        0,
                        1,
                        @erg,
                        @OutputTableName
                        )
                WITH NOWAIT;

            SET @counter = @counter + 1;

            IF DATEDIFF(hour, @Timestamp, GETDATE()) >= @MaxRunTime
            BEGIN
                SET @Killed = 1

                GOTO finish;
            END;
        END;
    END;

    RAISERROR (
            N'Collect special data',
            0,
            1
            )
    WITH NOWAIT;

    SET @counter = 0;

    CREATE TABLE #tables (
        idx INT IDENTITY PRIMARY KEY,
        name NVARCHAR(255)
        );

    SET @sql = N'INSERT INTO #tables(name) SELECT name from ' + QUOTENAME(@DatabaseName) + '.sys.objects where type = ''U'';'

    EXEC @erg = sp_executesql @Sql

    IF @erg <> 0
        RAISERROR (
                N'ERROR (%d): %s',
                0,
                1,
                @erg,
                @Sql
                )
        WITH NOWAIT;

    WHILE @counter < (
            SELECT MAX(idx)
            FROM #tables
            )
    BEGIN
        SET @counter = @counter + 1;
        SET @sql = N'sp_BlitzIndex ' + '@OutputDatabaseName=@OutputDatabaseName,' + '@OutputSchemaName=@OutputSchemaName,' + 
            '@OutputTableName=@OutputTableName,' + '@Databasename=@Databasename,' + '@SchemaName=@SchemaName,' + 
            '@TableName=@TableName';
        SET @params = N'@OutputDatabaseName NVARCHAR(258),' + '@OutputSchemaName NVARCHAR(258),' + 
            '@OutputTableName NVARCHAR(258),' + '@Databasename NVARCHAR(128),' + '@SchemaName NVARCHAR(128),' + 
            '@TableName NVARCHAR(128)';

        SELECT @OutputTableName = N'bi_spec__' + REPLACE(LOWER(name), ' ', '_') + '__' + @TimestampString,
            @TableName = name
        FROM #tables
        WHERE idx = @counter;

        RAISERROR (
                N'PARAMS: %s %s %s %s %s',
                0,
                1,
                @OutputDatabaseName,
                @OutputTableName,
                @OutputSchemaName,
                @Databasename,
                @OutputTableName
                )
        WITH NOWAIT;

        EXEC @erg = sp_executesql @Sql,
            @Params,
            @OutputDatabaseName = @OutputDatabaseName,
            @OutputSchemaName = @OutputSchemaName,
            @OutputTableName = @OutputTableName,
            @Databasename = @Databasename,
            @SchemaName = @SchemaName,
            @TableName = @TableName

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

    DROP TABLE #tables;

    finish:

    SET @Timestamp = GETDATE();

    EXEC rpinet_CollectLog @Id = @Id,
        @DatabaseName = @DatabaseName,
        @EndDate = @Timestamp,
        @Killed = @Killed;
END;
