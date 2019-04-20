DECLARE @sql NVARCHAR(MAX) = NULL,
    @databasename NVARCHAR(100) = NULL;

SET @databasename = N'$${OutputDatabaseName}';

IF NOT EXISTS (
        SELECT *
        FROM sys.databases
        WHERE name = @databasename
        )
BEGIN
    SET @sql = 'CREATE DATABASE ' + QUOTENAME(@databasename);

    EXEC sp_executesql @sql

    SET @sql = 'ALTER DATABASE ' + QUOTENAME(@databasename) + ' SET RECOVERY SIMPLE;';

    EXEC sp_executesql @sql
END;
