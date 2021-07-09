package se.umu.arsu0013.thirty


import androidx.lifecycle.ViewModel

const val MAX_ROLLS = 2

class PlayViewModel : ViewModel() {

    var dice = listOf(Die(), Die(), Die(), Die(), Die(), Die())
    val user = User()

    init {
        // change die faces from 0 initially, don't increment
        rollAll()
    }

    fun roll(): Boolean {
        var count = 0
        if (user.rollCount < MAX_ROLLS) {
            this.dice.map { die ->
                if (die.selected) {
                    die.roll()
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

    private fun rollAll() {
        this.dice.map { die ->
            die.roll()
        }
    }

    fun toggleSelect(die: Die) {
        if (!die.played) {
            die.selected = !die.selected
        }
    }


    fun resetPlayedDice() {
        user.resetPlayedDice(this.dice)
    }

    fun calculateScore(playOption: PlayOption): Boolean {
        if (user.calculateScore(playOption, getSelectedDice(dice))) {
            rollAll()
            resetPlayedDice()
            return true
        }
        return false
    }

    private fun getSelectedDice(dice: List<Die>): MutableList<Die> {
        val selected = mutableListOf<Die>()
        for (die in dice) {
            if (die.selected) {
                selected.add(die)
            }
        }
        return selected
    }

}