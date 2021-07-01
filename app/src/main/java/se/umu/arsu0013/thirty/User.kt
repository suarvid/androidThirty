package se.umu.arsu0013.thirty

import android.util.Log

private const val TAG = "User"

class User(val name: String = "Player") {
    var totalScore: Int = 0
    private var scores: HashMap<PlayOption, Int> = HashMap(10)
    var rollCount: Int = 0
    private var playsLeft: Boolean =
        false // Flag variable, is set to true if any play is possible with selected option

    //this list is filled in PlayFragment's restorePlayOptions()
    // by using playOptionGoalSums() in this class
    var playOptions = mutableListOf<PlayOption>()

    fun incrementThrowCount() {
        rollCount += 1
    }

    private fun resetThrowCount() {
        rollCount = 0
    }

    fun calculateScore(playOption: PlayOption, dice: List<Die>): Boolean {

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
            return true
        }
        return false
    }


    private fun arePlaysRemaining(
        dice: List<Die>,
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
    // Based on top comment in https://stackoverflow.com/questions/4632322/finding-all-possible-combinations-of-numbers-to-reach-a-given-sum
    private fun subsetSum(numbers: List<Int>, target: Int, partial: List<Int>): List<Int> {
        Log.d(TAG, "subsetSum called")
        val partialSums = mutableListOf<Int>()
        val partialSum = sum(partial)

        if (partialSum == target) {
            Log.d(TAG, "sum(${partial}) = $target")
            partialSums.add(partialSum)
            playsLeft = true
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

    private fun getPlayableValues(dice: List<Die>): MutableList<Int> {
        val values = mutableListOf<Int>()
        for (die in dice) {
            if (!die.played) {
                values.add(die.face)
            }
        }
        return values
    }

    fun gameIsFinished(): Boolean {
        return this.playOptions.isEmpty()
    }

    private fun calculateSumSelected(dice: List<Die>): Int {
        var runningSum = 0
        for (die in dice) {
            if (die.selected) {
                runningSum += die.face
                die.played = true
            }
        }
        return runningSum
    }

    private fun setPlayedDice(dice: List<Die>) {
        for (die in dice) {
            if (die.selected) {
                die.played = true
                die.selected = false
            }
        }
    }

    fun resetPlayedDice(dice: List<Die>) {
        for (die in dice) {
            die.played = false
        }
    }


    fun getScores(): HashMap<PlayOption, Int> {
        return this.scores
    }


    private fun calculateLowScore(dice: List<Die>): Int {
        var sum = 0
        for (die in dice) {
            if (die.selected && die.face <= 3) {
                sum += die.face
            }
        }
        return sum
    }

    fun playOptionGoalSums(): List<Int> {
        return listOf(
            PlayOption.LOW.goalSum,
            PlayOption.FOUR.goalSum,
            PlayOption.FIVE.goalSum,
            PlayOption.SIX.goalSum,
            PlayOption.SEVEN.goalSum,
            PlayOption.EIGHT.goalSum,
            PlayOption.NINE.goalSum,
            PlayOption.TEN.goalSum,
            PlayOption.ELEVEN.goalSum,
            PlayOption.TWELVE.goalSum
        )

    }
}