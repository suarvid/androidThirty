package se.umu.arsu0013.thirty

import android.util.Log

private const val TAG = "User"

class User(val name: String = "Player") {
    private var totalScore: Int = 0
    private var scores: HashMap<PlayOption, Int> = HashMap(10)
    private var rollCount: Int = 0
    private var playsLeft: Boolean =
        false // Flag variable, is set to true if any play is possible with selected option
    var playOptions = mutableListOf(
        PlayOption.LOW,
        PlayOption.FOUR,
        PlayOption.FIVE,
        /*
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

        playsLeft = false
        if (playOption == PlayOption.LOW) { // Edge case, easy to auto-compute, bit ugly though
            Log.d(TAG, "play option is Low")
            val lowScore = calculateLowScore(dice)
            totalScore += lowScore
            playOption.achievedScore += lowScore
        } else {
            val sumSelected = calculateSumSelected(dice)
            if (sumSelected == playOption.goalSum) {
                totalScore += sumSelected
                playOption.achievedScore += sumSelected
            }
        }

        setPlayedDice(dice)
        scores[playOption] = playOption.achievedScore

        arePlaysRemaining(dice, playOption)

        if (!playsLeft) {
            // TODO: This should be done later, on re-roll
            resetThrowCount() // Maybe this can stay
            this.playOptions.remove(playOption)
            Log.d(TAG, "No plays left for play option $playOption")
        }
    }


    private fun arePlaysRemaining(
        dice: List<Triple<Die, Boolean, Boolean>>,
        playOption: PlayOption
    ) {
        val playableValues = getPlayableValues(dice)
        if (playOption == PlayOption.LOW) {
            checkLowValues(playableValues)
        } else {
            // if empty, no plays left, i.e. move on
            subsetSum(playableValues, playOption.goalSum, listOf()).isEmpty() //should do the trick
        }
    }

    private fun checkLowValues(values: List<Int>) {
        values.forEach {
            if (it <= 3) {
                playsLeft = true
            }
        }
    }

    // For calculating possible combinations that yield the sought sum
    private fun subsetSum(numbers: List<Int>, target: Int, partial: List<Int>): List<Int> {
        Log.d(TAG, "subsetSum called")
        val partialSums = mutableListOf<Int>()
        val partialSum = sum(partial)

        if (partialSum == target) {
            Log.d(TAG, "sum(${partial}) = $target")
            partialSums.add(partialSum)
            playsLeft = true
        }

        // maybe unnecessary
        if (partialSum > target) {

        }

        for (i in numbers.indices) {
            val n = numbers[i]
            val remaining = numbers.slice(i + 1 until numbers.size)
            partialSums.addAll(subsetSum(remaining, target, partial + n)) // to get flat list
        }

        return partialSums
    }

    private fun sum(values: List<Int>): Int {
        var sum = 0
        values.forEach { value ->
            sum += value
        }

        return sum
    }

    private fun getPlayableValues(dice: List<Triple<Die, Boolean, Boolean>>): MutableList<Int> {
        val values = mutableListOf<Int>()
        for (triple in dice) {
            if (!triple.played) {
                values.add(triple.die.getFace())
            }
        }
        return values
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

    fun getTotalScore(): Int {
        return this.totalScore
    }

    fun getScores(): HashMap<PlayOption, Int> {
        return this.scores
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