package se.umu.arsu0013.thirty


import androidx.lifecycle.ViewModel

const val MAX_ROLLS = 3

class RollViewModel : ViewModel() {

     var dice = listOf(
        Triple(Die(0), false, false),
        Triple(Die(0), false, false),
        Triple(Die(0), false, false),
        Triple(Die(0), false, false),
        Triple(Die(0), false, false),
        Triple(Die(0), false, false),
    )

    val user = User()

    init {
        // change die faces from 0 initially, don't increment
        rollAll()
    }

    fun roll(): Boolean {
        var count = 0
        if (user.rollCount < MAX_ROLLS) {
            this.dice.map { triple ->
                if (triple.selected) {
                    triple.die.roll()
                    count += 1
                }
            }

            // only count rolls that actually roll a die
            if (count > 0) {
                user.incrementThrowCount()
            }

            return true
        }
        return false
    }

    fun rollAll() {
        this.dice.map { triple ->
            triple.die.roll()
        }
    }

    fun toggleSelect(triple: Triple<Die, Boolean, Boolean>) {
        if (!triple.played) {
            triple.selected = !triple.selected
        }
    }


    fun resetPlayedDice() {
        user.resetPlayedDice(this.dice)
    }

    fun calculateScore(playOption: PlayOption): Boolean {
        if (user.calculateScore(playOption, dice)) {
            rollAll()
            resetPlayedDice()
            return true
        }
        return false
    }

}