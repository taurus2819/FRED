@loginPageCheck
Feature: click the login menu to Login to FRED

  Scenario: Should display the Login page
    Given user is on theFREDHomePage
    When the user clicks on the login menu
    Then the FRED login page with the title FRED Login should appear
