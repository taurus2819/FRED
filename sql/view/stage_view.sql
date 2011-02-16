CREATE OR REPLACE VIEW Stage_View AS
SELECT Stage_ID, A1.name || Stage_Lower_Mod || DECODE(age_Lower_ID, NULL, NULL, DECODE(age_Upper_ID,
 NULL, NULL, ' - ')) || A2.name || Stage_Upper_Mod AS Stage, A1.code || Stage_Lower_Mod ||
 DECODE(age_Lower_ID, NULL, NULL, DECODE(age_Upper_ID, NULL, NULL, '-')) || A2.code || Stage_Upper_Mod AS
 code, age_Lower_ID, A1.name AS Stage_Lower, A1.code AS Stage_Lower_Abbrev, Stage_Lower_Mod,
 age_Upper_ID, A2.name AS Stage_Upper, A2.code AS Stage_Upper_Abbrev, Stage_Upper_Mod,
 A1.base_age AS base_age, DECODE(age_Upper_ID, NULL, A1.top_age, A2.top_age)
 AS top_age
FROM Stage S, Age A1, Age A2
WHERE S.age_Lower_ID = A1.Age_ID AND S.age_Upper_ID = A2.Age_ID(+);