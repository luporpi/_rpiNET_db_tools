-- Anpassen, sofern der Default Datenbankname bei der Installation angepasst wurde
SET TRANSACTION ISOLATION LEVEL READ UNCOMMITTED

-- [pdv_collect_check]
--     @DatabaseName        Datenbankname des VIS Mandanten
EXEC rpinet_CollectCheck @DatabaseName = 'visrpi'

SELECT *
FROM rpinet_collect_log_tab
select * from rpinet_collect_bc_common_tab