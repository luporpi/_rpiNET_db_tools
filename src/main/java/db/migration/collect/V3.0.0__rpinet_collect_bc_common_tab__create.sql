IF OBJECT_ID('dbo.rpinet_collect_bc_common_tab', 'U') IS NULL
BEGIN
	CREATE TABLE rpinet_collect_bc_common_tab (
		idx INT IDENTITY(1, 1),
		sort VARCHAR(50)
		);
END;
