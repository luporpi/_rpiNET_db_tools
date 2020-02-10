IF OBJECT_ID('dbo.rpinet_tools_searchdb') IS NULL
    EXEC ('CREATE PROCEDURE dbo.rpinet_tools_searchdb AS RETURN 0;');
GO

ALTER PROCEDURE [dbo].[rpinet_tools_searchdb] @DatabaseName NVARCHAR(128) = NULL,
    @SchemaName NVARCHAR(128) = 'dbo',
    @TableFilter NVARCHAR(1000) = '%',
    @ColumnFilter NVARCHAR(1000) = '%',
    @SearchString NVARCHAR(500) = NULL,
    @Version VARCHAR(30) = NULL OUTPUT,
    @VersionDate DATETIME = NULL OUTPUT,
    @VersionCheckMode BIT = 0
AS
SET NOCOUNT ON;
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;

SELECT @Version = '3.3.3',
    @VersionDate = '20200210';

IF (@VersionCheckMode = 1)
BEGIN
    RETURN;
END;

IF @DatabaseName IS NULL
    SET @DatabaseName = DB_NAME();

DECLARE @erg INT,
    @counter INT = 0,
    @TableName NVARCHAR(128),
    @ColumnName NVARCHAR(128),
    @ColumnType NVARCHAR(128),
    @sql NVARCHAR(MAX),
    @rows INT = 0;

CREATE TABLE #results (
    idx INT IDENTITY PRIMARY KEY,
    tablename NVARCHAR(256),
    columnname NVARCHAR(256),
    columntype NVARCHAR(256),
    found BIT DEFAULT 0
    );

-- TODO: collect
SET @sql = 
    N'INSERT INTO #results
    SELECT
        sta.name tablename,
        sco.name columnname,
        sty.name columntype,
        0
    FROM
        ' 
    + QUOTENAME(@DatabaseName) + '.sys.tables sta
    JOIN
        ' + QUOTENAME(@DatabaseName) + 
    '.sys.columns sco
    ON
        sta.object_id = sco.object_id
    JOIN
        ' + QUOTENAME(@DatabaseName) + 
    '.sys.types sty
    ON
        sco.user_type_id = sty.user_type_id
    WHERE
        sta.type = ''U'' AND
        sty.name in (''varchar'',''char'',''nvarchar'',''nchar'',''text'',''xml'') AND
        sta.name LIKE ''%' 
    + @TableFilter + '%'' AND
        sco.name LIKE ''%' + @ColumnFilter + '%'' AND
        sta.schema_id = SCHEMA_ID(''' + 
    @SchemaName + ''')
    ORDER BY tablename, columnname';

EXEC @erg = sp_executesql @sql;

IF @erg <> 0
    RAISERROR (
            N'ERROR (%d): %s',
            0,
            1,
            @erg,
            @Sql
            )
    WITH NOWAIT;

-- TODO: search
WHILE @counter < (
        SELECT MAX(idx)
        FROM #results
        )
BEGIN
    SET @counter = @counter + 1;

    SELECT @TableName = tablename,
        @ColumnName = columnname,
        @ColumnType = columntype
    FROM #results
    WHERE idx = @counter;

    SET @sql = N'SELECT @rows = COUNT(*) 
                FROM ' + QUOTENAME(@DatabaseName) + '.' + QUOTENAME(@SchemaName) + '.' + 
        QUOTENAME(@TableName) + ' (NOLOCK) ' + ' WHERE ' + CASE @ColumnType
            WHEN 'xml'
                THEN 'CAST(' + QUOTENAME(@ColumnName) + ' as NVARCHAR(max))'
            ELSE QUOTENAME(@ColumnName)
            END + ' LIKE ''%' + @SearchString + '%'' ESCAPE ''\'''

    EXEC @erg = sp_executesql @sql,
        N'@rows INT OUTPUT',
        @rows = @rows OUTPUT;

    IF @erg <> 0
        RAISERROR (
                N'ERROR (%d): %s',
                0,
                1,
                @erg,
                @Sql
                )
        WITH NOWAIT;

    IF @rows = 0
    BEGIN
        DELETE #results
        WHERE idx = @counter;
    END;
END;

SELECT 'SELECT ' + QUOTENAME(columnname) + ' FROM ' + QUOTENAME(tablename) AS query,
    tablename,
    columnname,
    columntype
FROM #results;

DROP TABLE #results;
