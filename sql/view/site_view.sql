CREATE OR REPLACE VIEW Site_View AS
SELECT DISTINCT /*+ FIRST_ROWS */
F.Feature_ID, FR.FR_Number, F.Feature_Type, F.Locality, St.Shape, St.ObjectID
FROM FR.FEATURE F, FR.FR_NUMBER FR, SC.SITE St
WHERE F.Site_ID = St.Site_ID
AND F.FR_ID = FR.FR_ID(+);