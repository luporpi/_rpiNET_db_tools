-- Anpassen, sofern der Default Datenbankname bei der Installation angepasst wurde
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED

-- [pdv_collect_job]
--     @DatabaseName        Datenbankname des VIS Mandanten
--     @Top                 (optional) Anzahl der Zeilen für die Ausgabe von sp_BlitzCache (default = 100)
--     @Timestamp           (optional) Zeitstempel der Ausführung
--     @MaxRunTime          (optional) Stunden nach denen sich das Script automatisch beendet (default = null - bis fertig)
--     @BringThePain        (optional) Ausführung von bi_base bei Datenbanken mit mehr als 100 Partitionen (default = 0)
--     @OutputServerName    (optional) noch nicht unterstützt
--     @OutputDatabaseName  (optional) Name der 'collect' Datenbank (default = 'pdv_collect')
--     @OutputSchemaName	(optional) zu Verwendendes Schema (default = 'dbo')

-- Festlegen des Zeitstempels
DECLARE @timestamp DATETIME = GETDATE();

EXEC rpinet_CollectJob @DatabaseName = 'visrpi',
    @Top = 100,
    @Timestamp = @timestamp,
    @MaxRunTime = 4
