package se.umu.arsu0013.thirty

enum class PlayOption {
    LOW { // All dice with face 3 and below
        override fun calculateScore(dice: List<Tuple<Die, Boolean>>): Int {
            var sum = 0
            for (die in dice) {
                if (die.second && die.first.getFace() <= 3) {
                    sum += die.first.getFace()
                }
            }
            return sum
        }
    },
    FOUR { // All combinations of one or more dice that produce the sum 4
        override fun calculateScore(dice: List<Tuple<Die, Boolean>>): Int {
            calculateScoreForSum(4)
            TODO("Not yet implemented")
        }
    },
    FIVE { // -||- sum 5
        override fun calculateScore(dice: List<Tuple<Die, Boolean>>): Int {
            TODO("Not yet implemented")
        }
    },
    SIX { // -||- sum 6
        override fun calculateScore(dice: List<Tuple<Die, Boolean>>): Int {
            TODO("Not yet implemented")
        }
    },
    SEVEN { // -||- sum 7
        override fun calculateScore(dice: List<Tuple<Die, Boolean>>): Int {
            TODO("Not yet implemented")
        }
    },
    EIGHT { // -||- sum 8
        override fun calculateScore(dice: List<Tuple<Die, Boolean>>): Int {
            TODO("Not yet implemented")
        }
    },
    NINE{ // -||- sum 9
        override fun calculateScore(dice: List<Tuple<Die, Boolean>>): Int {
            TODO("Not yet implemented")
        }
    },
    TEN { // -||- sum 10
        override fun calculateScore(dice: List<Tuple<Die, Boolean>>): Int {
            TODO("Not yet implemented")
        }
    },
    ELEVEN { // -||- sum 11
        override fun calculateScore(dice: List<Tuple<Die, Boolean>>): Int {
            TODO("Not yet implemented")
        }
    },
    TWELVE { // -||- sum 12
        override fun calculateScore(dice: List<Tuple<Die, Boolean>>): Int {
            TODO("Not yet implemented")
        }
    };

    abstract fun calculateScore(dice: List<Tuple<Die, Boolean>>): Int

    // TODO: implement calculation for combinations of more than two dice
    protected fun calculateScoreForSum(sum: Int, dice: List<Tuple<Die, Boolean>>): Int {
        var score = 0

        for (die in dice) {
            // don't play non-selected die
            if (!die.second) {
                continue
            }

            val pairings = pairsToSum(die.first.getFace(), sum, dice)

            // only one possible pairing, easy peasy
            if (pairings.first == 1) {
                score += sum
                dice[pairings.second[0]].second = false // un-select to prevent several pairings

            } else { // check number of pairings for the other values, select one with lowest count

                for (pairingIndex in pairsToSum(die.first.getFace(), sum, dice).second) {

                }
            }

        }
        return score
    }

    // returns count of possible pairings and indexes where they can be found
    private fun pairsToSum(value: Int, wantedSum: Int, dice: List<Tuple<Die, Boolean>>): Tuple<Int, List<Int>> {
        var count = 0
        val indexList = mutableListOf<Int>()

        for (i in 0..dice.size) {
            if (dice[i].first.getFace() + value == wantedSum) {
                count += 1
                indexList.add(i)
            }
        }

        return Tuple(count, indexList)
    }


    // should return ALL combinations that produce the wanted sum
    // returned values represent indexes of dice, not their actual values
    private fun combinationsToSum(wantedSum: Int, dice: List<Tuple<Die, Boolean>>): MutableList<List<Int>> {
        val indexList = mutableListOf<List<Int>>()

        for (i in 0..6) {
            indexList.add(combineToSum(wantedSum, dice, i).flatten())
        }

        return indexList
    }

    //TODO: sketch and check a whole bunch of condition indexes
    private fun combineToSum(wantedSum: Int, dice: List<Tuple<Die, Boolean>>, numElem: Int): MutableList<List<Int>> {
        var currentIndexes = (0 until numElem).toMutableList()
        val combinationIndexes = mutableListOf<List<Int>>()

        while (currentIndexes[0] - (dice.size - 1) < numElem) {
            if (tryIndexes(currentIndexes, wantedSum, dice)) {
                combinationIndexes.add(currentIndexes)
            }

            currentIndexes = updateIndexes(currentIndexes, dice.size)
        }

        return combinationIndexes
    }

    private fun tryIndexes(indexes: MutableList<Int>, wantedSum: Int, dice: List<Tuple<Die, Boolean>>): Boolean {
        var sum = 0
        for (index in indexes) {
            sum += dice[index].first.getFace()
        }

        return sum == wantedSum
    }

    private fun updateIndexes(indexes: MutableList<Int>, numDice: Int): MutableList<Int> {
        for (i in 0 until indexes.size) {
            // TODO: Double check this with pen and paper
            if (indexes[indexes.size - (1+i)] - numDice < (1+i)) {
                indexes[indexes.size - (1+i)] += 1
                return indexes
            }
        }
        return indexes
    }
}