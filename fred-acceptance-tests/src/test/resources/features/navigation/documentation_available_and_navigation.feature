Feature: Click on the 'About', 'Quick Start' and 'Fred User Manual'menu options in turn

  Background: 
    Given the user is on the FRED homepage
  
  Scenario: Verify 'About'screen is displayed respectively and the menu remains 
  available.             
  
     When the user clicks on the 'About' menu
     Then the 'About' page is displayed
  
  Scenario: Verify the 'Quick Start' page is displayed and the menu remains
  available.
  
     When the user clicks on the 'Quick Start' menu
     Then the 'Quick Start' page is displayed  