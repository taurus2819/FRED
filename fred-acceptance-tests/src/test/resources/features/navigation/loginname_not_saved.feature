@preventXSS
Feature: Guarantee the loginname is not returned as a value to prevent xss 
  
  Scenario: Prevent XSS by not printing out the loginname value 
     Given Frank is on theFREDLoginPage
     When he puts in a username and no password 
     Then the username value is not returned in HTML

# Uses new Step Definitions
  
