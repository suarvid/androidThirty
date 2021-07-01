package se.umu.arsu0013.thirty


data class Die(var face: Int = 0, var selected: Boolean = false, var played: Boolean = false) {

    init {
        roll()
    }

    fun roll() {
        this.face = (1..6).random()
    }
}