CREATE OR REPLACE VIEW FR_User_View AS
select
   person_id as pe_id
   , given_name
   , family_name
   , full_name
   , pe_client_code
   , cl_company_name
   , min( decode ir_name, 'FR_DATA_ENTRY', 2, 'FR_WEBSITE_ACCESS', 4) as ir_id
   , deleted
from ip.user_right_view pv
where ir_name in ('FR_DATA_ENTRY', 'FR_WEBSITE_ACCESS')
group by
   person_id
   , given_name
   , family_name
   , full_name
   , pe_client_code
   , cl_company_name
   , deleted
