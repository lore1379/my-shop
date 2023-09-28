Feature: Shop View High Level
  Specifications of the behavior of the Shop View

  Background: 
    Given The database contains a few products
    And The Shop View is shown

  Scenario: Add a product to cart
    Given The user selects a product from the shop list
    When The user clicks the "Add to Cart" button
    Then The cart list contains the product