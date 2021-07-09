package se.umu.arsu0013.thirty


import androidx.lifecycle.ViewModel

const val MAX_ROLLS = 2

/**
 * View Model class for the Play Fragment.
 * Contains dice-handling logic as well as a User for score calculation.
 */

class PlayViewModel : ViewModel() {
    var dice = listOf(Die(), Die(), Die(), Die(), Die(), Die())
    val user = User()

    init {
        // change die faces from 0 initially, don't increment
        rollAll()
    }

    /**
     * Rolls the dice selected by the user and increases the user's roll count if the user
     * has any remaining rolls.
     */
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

    // Roll all dice without increasing the roll-count
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

    // Delegate the score calculation to the User object, re-roll all dice for next round
    // and mark all dice as playable again
    fun calculateScore(playOption: PlayOption): Boolean {
        if (user.calculateScore(playOption, getSelectedDice(dice))) {
            rollAll()
            resetPlayedDice()
            return true
        }
        return false
    }

    // Retrieve all dice selected by the user
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