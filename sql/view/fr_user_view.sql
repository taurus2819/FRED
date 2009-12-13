CREATE OR REPLACE VIEW FR_User_View AS
select
   person_id as pe_id
   , given_name
   , family_name
   , full_name
   , pe_client_code
   , cl_company_name
   , min(ir_id) as ir_id
   , deleted
from ip.user_right_view pv
where ir_id in (2, 4)
group by
   person_id
   , given_name
   , family_name
   , full_name
   , pe_client_code
   , cl_company_name
   , deleted
