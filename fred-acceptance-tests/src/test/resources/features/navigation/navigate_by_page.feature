@fredHomePage
Feature: Navigate to FRED home Page
  type in the FRED URL and
  the user must be directed to the FRED page

Scenario: Visiting FRED
    Given Chris is on theFREDHomePage
    When he focuses on the FRED home page
    Then the FRED page title should appear
#    And check FRED is getting the data