Feature: Shop View High Level
  Specifications of the behavior of the Shop View

  Background: 
    Given The database contains a few products
    And The Shop View is shown

  Scenario: Add a product to cart
    Given The user selects a product from the shop list
    When The user clicks the "Add to Cart" button
    Then The cart list contains the product
    
  Scenario: Add a not existing product to cart
    Given The user selects a product from the shop list
    But The product is in the meantime removed from the database
    When The user clicks the "Add to Cart" button
    Then An error is shown containing the name of the selected product
    And The product is removed from the list
    
  Scenario: Remove a product from cart
    Given The user selects a product from the cart list
    When The user clicks the "Remove from Cart" button
    Then The shop list contains the product