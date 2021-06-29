package se.umu.arsu0013.thirty



class User(val name: String = "Player") {
    private var score: Int = 0
    private var rollCount: Int = 0
    var playOptions = mutableListOf(
        PlayOption.LOW,
        /*PlayOption.FOUR,
        PlayOption.FIVE,
        PlayOption.SIX,
        PlayOption.SEVEN,
        PlayOption.EIGHT,
        PlayOption.NINE,
        PlayOption.TEN,
        PlayOption.ELEVEN,
        PlayOption.TWELVE*/
    )

    fun incrementThrowCount() {
        rollCount += 1
    }

    fun resetThrowCount() {
        rollCount = 0
    }

    fun calculateScore(playOption: PlayOption, dice: List<Triple<Die, Boolean, Boolean>>) {
        resetThrowCount()
        if (playOption == PlayOption.LOW) { // Edge case, easy to auto-compute
            score += calculateLowScore(dice)
        } else {
            val sumSelected = calculateSumSelected(dice)
            if (sumSelected == playOption.goalSum) {
                score += sumSelected
            }
        }

        setPlayedDice(dice)

        // TODO: This should be done later, on re-roll
        resetThrowCount() // Maybe this can stay
        this.playOptions.remove(playOption)
    }

    fun gameIsFinished(): Boolean {
        return this.playOptions.isEmpty()
    }

    // TODO: Add check for finished game
    // TODO: Add starting page with buttons for play, instructions, maybe settings/quit or smthn


    private fun calculateSumSelected(dice: List<Triple<Die, Boolean, Boolean>>): Int {
        var runningSum = 0
        for (triple in dice) {
            if (triple.selected) {
                runningSum += triple.die.getFace()
                triple.played = true
            }
        }
        return runningSum
    }

    private fun setPlayedDice(dice: List<Triple<Die, Boolean, Boolean>>) {
        for (triple in dice) {
            if (triple.selected) {
                triple.played = true
                triple.selected = false
            }
        }
    }

    fun resetPlayedDice(dice: List<Triple<Die, Boolean, Boolean>>) {
        for (triple in dice) {
            triple.played = false
        }
    }

    fun getScore(): Int {
        return this.score
    }

    fun getRollCount(): Int {
        return this.rollCount
    }

    private fun calculateLowScore(dice: List<Triple<Die, Boolean, Boolean>>): Int {
        var sum = 0
        for (triple in dice) {
            if (triple.selected && triple.die.getFace() <= 3) {
                sum += triple.die.getFace()
            }
        }
        return sum
    }
}