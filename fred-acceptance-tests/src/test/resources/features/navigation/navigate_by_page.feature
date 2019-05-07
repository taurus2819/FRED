
Feature: Navigate to FRED open Page
  typin in the URL https://data-uat.gns.cri.nz/fred
  the user must be directed to the FRED page

Scenario: Visiting FRED
    Given Chris is on the FRED home page
    When he focuses on the FRED home page
    Then the FRED page title should appear
    And check FRED is getting the data
