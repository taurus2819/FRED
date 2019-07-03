Feature: Parse the User Manual file to verify it contains the text Version 1.0 
  
  Scenario: Verify the 'FRED User Manual' pdf document is opened 
     Given The user launches the FRED application
     When the user clicks on the 'FRED User Manual' menu 
     Then the user manual pdf is opened to verify it is 'Version 1.0'
  
