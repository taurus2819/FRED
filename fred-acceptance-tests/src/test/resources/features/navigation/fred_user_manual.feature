Feature: Parse 'Fred User Manual'
  
  Scenario: Verify the 'FRED User Manual' pdf document is opened 
     Given The user launches the FRED application
     When the user clicks on the 'FRED User Manual' menu 
     Then the user manual pdf is opened
  
