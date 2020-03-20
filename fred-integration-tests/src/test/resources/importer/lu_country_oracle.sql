
CREATE VIEW LU_COUNTRY AS
SELECT * FROM MIS.COUNTRY
ORDER BY decode (country_code, 'AQ', 'AA', 'NZ', 'AB', country_name);