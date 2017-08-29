FRED Adopted Age changes
========================

Also known as the "squirrel age" changes, because these changes add the ability to query by gathering information from lots of different places in FRED.

The original adopted age requirements are in a Word document called "FRED adopted ages 2017 project v4.docx" in "I:\Information Services\Applications\Systems\Paleo\FRED Db\Prior to FY2017-18\Adopted Age project 2017\Reqs and design". See that document for all the details.

Summarised, they modify the simple and advanced query to be able to query on an "adopted age". This was implemented directly using views rather than Chris Clowe's suggest approach of nightly database updates.

If performance becomes a problem, these views can be materialized.

The FRED simple query pulls its data from SQUIRREL_SAMPLE_VIEW. The advanced query pulls its data from SAMPLE_STAGE_VIEW; these are at the bottom of PDB-140.

These views are defined as, for each SAMPLE:

Narrow age 
-----------

Return the minimal overlap in ages in the PALEONTOLOGY table, or if none, the total overlap (AUTO_AGE_WIDE_DTRMND).

There are special cases:

* Only consider the most recent entries in the PALEONTOLOGY table; use the IDENFICATION_DATE or RECORD_ID to find the most recent (see PALEONTOLOGY_FIXED_VIEW). If the identification date is missing, fill it in from the list in the requirements document (see the DEFAULT_IDENTIFICATION_DATE table).

* Only consider approved groups (PAL_LIST.GROUP_ID), listed in the requirements document (see the "where pl.group_id in" clause in  AUTO_AGE_WIDE_DTRMND, AUTO_AGE_NARROW_VIEW, AUTO_AGE_WIDE_VIEW).

* Ignore PALEONTOLOGY.STAGE_ID entries where the age is CONST_MAX_BASE_AGE or CONST_MIN_TOP_AGE.

* If there are no entries for PALEONTOLOGY.STAGE_ID after these special cases, resort to known age, and then inferred age (see the nvl() clauses in SQUIRREL_AGE_VIEW). 
	

Wide age
---------

Return the maximal overlap of the union of all these ages:

* Determined ages (PALEONTOLOGY.STAGE_ID)

* Known ages (SAMPLE.KNOWN_STAGE_ID)

* Inferred ages (SAMPLE.INFERRED_STAGE_ID)


Again, respecting the special cases the same was as the narrow age logic does.
