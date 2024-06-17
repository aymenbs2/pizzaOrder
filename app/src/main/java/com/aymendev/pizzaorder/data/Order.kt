package com.aymendev.pizzaorder.data

data class Order(var pizza: Pizza,val pizzaIngredient: MutableList<PizzaSupplement>) {
}