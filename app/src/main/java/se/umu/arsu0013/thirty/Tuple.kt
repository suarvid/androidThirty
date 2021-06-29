package se.umu.arsu0013.thirty

data class Triple<out A, B, C>(val die: A, var selected: B, var played: C)