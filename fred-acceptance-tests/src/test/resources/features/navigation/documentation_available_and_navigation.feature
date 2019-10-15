@aboutAndQuickStart
Feature: Click on the 'About' and 'Quick Start' menu options in turn

  Background: 
    Given the user is on theFREDHomePage
  
  Scenario: Verify 'About'screen is displayed respectively and the menu remains 
  available.             
  
     When the user clicks on the about menu
     Then the about page is displayed

@justQuickStart
  Scenario: Verify the 'Quick Start' page is displayed and the menu remains
  available.
  
     When the user clicks on the quickStart menu
     Then the quickStart page is displayed  

# Uses new Step Definitions