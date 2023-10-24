Feature: Shop View High Level
  Specifications of the behavior of the Shop View

  Background: 
    Given The database contains a few products
    And The database contains a cart with a few products in it
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
    
  Scenario: Checkout a product from cart
    Given The user selects a product from the cart list
    When The user clicks the "Checkout Product" button
    Then The product is removed from the cart list
    And A successful purchase message is shown containing the name of the selected product
    
  Scenario: Checkout a not existing product from cart
    Given The user selects a product from the cart list
    But The product is in the meantime removed from the cart in the database
    When The user clicks the "Checkout Product" button
    Then An error is shown containing the name of the selected product in the cart
    And The product is removed from the cart list
