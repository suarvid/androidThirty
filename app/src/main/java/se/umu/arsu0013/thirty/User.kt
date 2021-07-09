package se.umu.arsu0013.thirty

import android.util.Log

private const val TAG = "User"


/**
 * Class representing a User.
 * The logic is responsible for score calculation as well as determining
 * when the game is completed.
 *
 * @param name the name of the Player, currently not used.
 */
class User(val name: String = "Player") {
    var totalScore: Int = 0
    private var scores: HashMap<PlayOption, Int> = HashMap(10)
    var rollCount: Int = 0

    // Ugly class-wide variable to deal with recursion in score-counting
    var scoreCounter = 0

    //this list is filled in PlayFragment's restorePlayOptions()
    // by using playOptionGoalSums() in this class
    var playOptions = mutableListOf<PlayOption>()

    fun incrementThrowCount() {
        rollCount += 1
    }

    private fun resetThrowCount() {
        rollCount = 0
    }

    /**
     * Function for calculating the score achieved with the supplied dice for the
     * given play option.
     * The calculation for play option LOW is much simpler than for the others
     * and is therefore handled in a separate case.
     *
     * @param playOption the selected play option for the current play
     * @param dice the dice used to calculate the score
     *
     */

    fun calculateScore(playOption: PlayOption, dice: List<Die>): Boolean {

        if (playOption == PlayOption.LOW) { // Edge case, easy to auto-compute, bit ugly though
            Log.d(TAG, "play option is Low")
            val lowScore = calculateLowScore(dice)
            totalScore += lowScore
            playOption.achievedScore += lowScore
            setPlayedDice(dice)
        } else {
            // Check if there are any valid combinations
            var combinationsRemaining = false
            if (areAnyPlays(playOption, dice)) {
                combinationsRemaining = scoreCalculation(playOption, dice)
            }

            // Call scoreCalculation to handle one combination at a time
            while (combinationsRemaining) {
                Log.d(TAG, "Multiplier Value: $scoreCounter")
                combinationsRemaining = scoreCalculation(playOption, dice)
            }

            // Add the sum of the play option for each valid combination
            Log.d(TAG, "Final multiplier for option $playOption is $scoreCounter")
            playOption.achievedScore = playOption.goalSum * scoreCounter
            totalScore += playOption.achievedScore
            // Reset the counter for the next option
            scoreCounter = 0
        }

        // Save the score for the final score screen
        scores[playOption] = playOption.achievedScore
        resetThrowCount()
        this.playOptions.remove(playOption)
        Log.d(TAG, "No plays left for play option $playOption")

        return true
    }

    /**
     * Function which computes the score for the individual combinations.
     * As a heuristic, selects the combination in increasing order of dice used.
     * This is done in order to maximize the number of valid combinations to be used in the calculation.
     * @param playOption option to calculate the score for
     * @param dice the dice used in the calculation
     */
    // always select the shortest list, will consume the fewest dice
    private fun scoreCalculation(playOption: PlayOption, dice: List<Die>): Boolean {
        // Calculate all possible combinations that sum up to the play option value
        val possibleCombinations = subsetSums(getPlayableValues(dice), playOption.goalSum, listOf())
        Log.d(TAG, "Possible combinations found: $possibleCombinations")

        // Some lists returned by subsetSums will be empty lists, make sure non-empty lists exist
        if (!containsNonEmptyList(possibleCombinations)) {
            Log.d(TAG, "Only Empty combinations remaining, return")
            return false
        }
        // Select combinations in increasing order of number of dice used
        val smallestComb = possibleCombinations[findShortestNonEmptyList(possibleCombinations)]
        Log.d(TAG, "Smallest found combination is $smallestComb")
        scoreCounter++


        // find dice with matching values, set them to played and de-select them
        smallestComb.forEach { value ->
            val die = findDiceWithValue(value, dice)
            Log.d(TAG, "Found die with value $value: $die")
            if (die != null && !die.played) {
                die.played = true
                die.selected = false
            } else {
                return false
            }
        }

        return true
    }

    // Function used to
    private fun areAnyPlays(playOption: PlayOption, dice: List<Die>): Boolean {
        return subsetSums(getPlayableValues(dice), playOption.goalSum, listOf()).isNotEmpty()
    }

    // This should probably also check if each die has been played or not
    private fun containsNonEmptyList(lists: MutableList<List<Int>>): Boolean {
        for (list in lists) {
            if (list.isNotEmpty()) {
                return true
            }
        }

        return false
    }

    private fun findDiceWithValue(value: Int, dice: List<Die>): Die? {
        for (die in dice) {
            if (die.face == value && !die.played && die.selected) {
                return die
            }
        }

        return null
    }

    private fun findShortestNonEmptyList(lists: List<List<Any>>): Int {
        var minLength = Int.MAX_VALUE
        var minIndex = 0
        for (i in lists.indices) {
            if (lists[i].size < minLength && lists[i].isNotEmpty()) {
                minLength = lists[i].size
                minIndex = i
            }
        }

        return minIndex
    }

    private fun subsetSums(
        numbers: List<Int>,
        target: Int,
        partial: List<Int>
    ): MutableList<List<Int>> {
        Log.d(TAG, "subsetSums called")
        val partialSums = mutableListOf<List<Int>>()
        val partialSum = sum(partial)

        if (partialSum == target) {
            Log.d(TAG, "sum(${partial}) = $target")
            partialSums.add(partial)
        }

        for (i in numbers.indices) {
            val n = numbers[i]
            val remaining = numbers.slice(i + 1 until numbers.size)
            partialSums.add(subsetSums(remaining, target, partial + n).flatten())
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