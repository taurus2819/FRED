@loginTest
Feature: Uses test user to log into FRED

  Background: 
    Given the user is on theFREDHomePage
  
  Scenario: Log into FRED             
  
     When the user clicks on the login menu
     And the user logs in
     Then the user name is shown as logged in
     And the user is logged out
  # The idea of creating a new folder and doing the work and then deleting the folder doesn't work
#  Scenario: Verify the user can create a new working folder
#  When the user clicks on the login menu
#And the user logs in
#     When the user clicks on the dataEntry menu
#     And the user clicks on the newFolder menu
#     And a new name is entered for the folder
#     Then the new Folder Name is registered
#  
#  Scenario: Verify the user can create a new Outcrop with incorrect values and have them 'fixed'
#
#    When the user clicks the new Folder Name
#    And the user clicks the 'New Outcrop' menu
#    And the user enters illegal characters
#    Then the resulting outcrop has no illegal characters