IF OBJECT_ID('dbo.rpinet_CollectLog') IS NULL
    EXEC ('CREATE PROCEDURE dbo.rpinet_CollectLog AS RETURN 0;');
GO

ALTER PROCEDURE [dbo].[rpinet_CollectLog] @Id INT OUTPUT,
    @DatabaseName NVARCHAR(128) = NULL,
    @StartDate DATETIME = NULL,
    @EndDate DATETIME = NULL,
    @Killed BIT = 0 OUTPUT,
    @Version VARCHAR(30) = NULL OUTPUT,
    @VersionDate DATETIME = NULL OUTPUT,
    @VersionCheckMode BIT = 0
AS
BEGIN
    SET NOCOUNT ON;
    SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;

    SELECT @Version = '3.3.6',
        @VersionDate = '20200606';

    IF (@VersionCheckMode = 1)
    BEGIN
        RETURN;
    END;

    IF OBJECT_ID('dbo.rpinet_collect_log_tab', 'U') IS NOT NULL
    BEGIN
        SELECT @Id = idx,
            @Killed = Killed
        FROM rpinet_collect_log_tab
        WHERE Databasename = DatabaseName
            AND StartDate = @StartDate

        IF @Id <= 0
        BEGIN
            INSERT INTO rpinet_collect_log_tab (
                Databasename,
                Startdate
                )
            VALUES (
                @DatabaseName,
                @StartDate
                );

            SET @Id = SCOPE_IDENTITY();
        END;
        ELSE
        BEGIN
            UPDATE rpinet_collect_log_tab
            SET EndDate = @EndDate,
                Killed = @Killed
            WHERE idx = @Id
        END;
    END;
END;
