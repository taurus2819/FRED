-- Remove IR_ID column from fr_user_view
create or replace force view fr.fr_user_view ("PE_ID", "GIVEN_NAME", "FAMILY_NAME", "FULL_NAME", "PE_CLIENT_CODE", "CL_COMPANY_NAME", "DELETED", "FR_WEBSITE_ACCESS", "FR_DATA_ENTRY", "FR_ADMIN", "LAST_LOGIN", "USERNAME") as
  select
/**
 * $HeadURL$
 * $LastChangedBy$
 * $Rev$
 * $Date$
 */
   person_id as pe_id
   , given_name
   , family_name
   , full_name
   , pe_client_code
   , cl_company_name
   , deleted
   , sum(decode (ir_name, 'FR_WEBSITE_ACCESS', 1, 0))
   , sum(decode (ir_name, 'FR_DATA_ENTRY', 1, 0))
   , sum(decode (ir_name, 'FR_ADMIN', 1, 0))
   , last_login
   , os_login
from ip.user_right_view2 pv
left join fr.fr_user fu on pv.person_id = fu.pe_id
where ir_name in ('FR_DATA_ENTRY', 'FR_WEBSITE_ACCESS', 'FR_ADMIN')
group by
   person_id
   , given_name
   , family_name
   , full_name
   , pe_client_code
   , cl_company_name
   , deleted
   , last_login
   , os_login;

-- These were invalid after updating the above...possibly were already invalid.
alter view fr.folder_view compile;
alter view fr.audit_view compile;
alter package fr.fred_maintain compile;
