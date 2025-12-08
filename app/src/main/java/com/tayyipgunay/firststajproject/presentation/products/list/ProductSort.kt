package com.tayyipgunay.firststajproject.presentation.products.list


enum class ProductSort(val query: List<String>, val label: String) {

    PRICE_ASC(listOf("price,asc"), "Price (Low→High)"),
    PRICE_DESC(listOf("price,desc"), "Price (High→Low)"),



    ACTIVE_FIRST(listOf("isActive,desc"), "Active first"),

    PASSIVE_FIRST(listOf("isActive,asc"), "Passive first"),


    // Hibrit presetler (multi-sort):
    ACTIVE_AND_CHEAP(listOf("isActive,desc", "price,asc"), "Active → Cheap"),
    ACTIVE_AND_EXPENSIVE(listOf("isActive,desc", "price,desc"), "Active → Expensive"),
}
