-- Anpassen, sofern der Default Datenbankname bei der Installation angepasst wurde
USE [pdv_collect]

SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED

-- [pdv_collect_delete]
--     @TimeStamp           (optional) Löschen eines Erfassungssatzes (default = NULL, alle)
--     @OutputServerName    (optional) noch nicht unterstützt
--     @OutputDatabaseName  (optional) Name der 'collect' Datenbank (default = 'pdv_collect')
--     @OutputSchemaName	(optional) zu Verwendendes Schema (default = 'dbo')

DECLARE @timestamp DATETIME = NULL;

-- Zeitstempel (StartDate) aus pdv_collect_log
-- (style bei Bedarf anpassen)
SET @timestamp = NULL; --CONVERT(DATETIME, '<datetime>', 120)

EXEC pdv_collect_delete @TimeStamp = @timestamp;
