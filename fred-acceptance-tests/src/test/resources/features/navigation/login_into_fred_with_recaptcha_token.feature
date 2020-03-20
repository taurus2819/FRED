@loginWithRecaptcha
Feature: Guarantee the login with recaptcha check  
  
  Scenario: With the recaptcha running in the background print out the loginname value 
     Given Frank is on theFREDLoginPage
     When he puts in a username and password 
     Then the username value shown as logged in
     And the user is logged out
     
  
