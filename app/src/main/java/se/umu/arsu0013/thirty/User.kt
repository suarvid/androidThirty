package se.umu.arsu0013.thirty


class User(val name: String = "Player") {
    private var score: Int = 0
    private var rollCount: Int = 0
    var playOptions = mutableListOf<PlayOption>(
        PlayOption.LOW,
        PlayOption.FOUR,
        PlayOption.FIVE,
        PlayOption.SIX,
        PlayOption.SEVEN,
        PlayOption.EIGHT,
        PlayOption.NINE,
        PlayOption.TEN,
        PlayOption.ELEVEN,
        PlayOption.TWELVE
    )

    fun incrementThrowCount() {
        rollCount += 1
    }

    fun resetThrowCount() {
        rollCount = 0
    }

    fun calculateScore(playOption: PlayOption, dice: List<Tuple<Die, Boolean>>) {
        resetThrowCount()
        score += playOption.calculateScore(dice)
        resetThrowCount()
        this.playOptions.remove(playOption)
    }

    fun getScore(): Int {
        return this.score
    }

    fun getRollCount(): Int {
        return this.rollCount
    }
}