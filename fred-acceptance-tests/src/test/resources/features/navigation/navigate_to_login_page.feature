
Feature: In order to Login to FRED the user
  should click on the login menu

  Scenario: Should display all the menus
    Given user wants to login to FRED
    When the user clicks on the login menu
    Then the FRED lgoin page with the title FRED Login should appear
