# Passed - BDS 9/10/19
@loginPageCheck
Feature: In order to Login to FRED the user
  should click on the login menu

  Scenario: Should display the Login page
    Given user is on theFREDHomePage
    When the user clicks on the login menu
    Then the FRED login page with the title FRED Login should appear

# Uses new Step Definitions
