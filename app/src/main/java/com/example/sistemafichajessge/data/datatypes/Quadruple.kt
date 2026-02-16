package com.example.sistemafichajessge.data.datatypes

data class Quadruple<out A, out B, out C, out D>(
    private val first: A,
    private val second: B,
    private val third: C,
    private val fourth: D
)
