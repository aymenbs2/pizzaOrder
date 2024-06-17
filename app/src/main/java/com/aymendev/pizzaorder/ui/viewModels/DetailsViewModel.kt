package com.aymendev.pizzaorder.ui.viewModels

import androidx.lifecycle.ViewModel
import com.aymendev.pizzaorder.R
import com.aymendev.pizzaorder.data.Order
import com.aymendev.pizzaorder.data.Pizza
import com.aymendev.pizzaorder.data.PizzaSupplement
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel(){

    var addedSupplementCount: Int=0
     lateinit var currentOrderPizza: Order
    lateinit var currentCart: MutableList<Order?>
    val currentSupplement= mutableListOf<PizzaSupplement>()
    val supplements= listOf(
        PizzaSupplement(id = 0,name="Potato", image = R.drawable.potato,1F),
        PizzaSupplement(id = 1,name="Champinion", image = R.drawable.champinion,1F)
    )
    val pizzas= listOf(
        Pizza(id = 0, name = "New Orleans Pizza", image = R.drawable.pizza, price= 16F),
        Pizza(id = 1, name = "Ham Pizza", image = R.drawable.ham_pizza, price = 15f),
        Pizza(id = 2, name = "Harissa Pizza", image = R.drawable.mk_pizza, price = 19f),
        Pizza(id = 3, name = "Bambo Pizza", image = R.drawable.pizza, price = 17f),
        Pizza(id = 3, name = "Mlokhia Pizza", image = R.drawable.pizza, price = 18f),

    )


}