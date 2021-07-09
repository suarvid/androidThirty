package se.umu.arsu0013.thirty

/**
 * Simple data class representing a Dice.
 *
 * @param face the current value of the dice
 * @param selected represents whether or not the die is currently selected by the player
 * @param played represents if the dice has been played this round
 */
data class Die(var face: Int = 0, var selected: Boolean = false, var played: Boolean = false) {

    init {
        roll()
    }

    fun roll() {
        this.face = (1..6).random()
    }
}