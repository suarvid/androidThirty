package se.umu.arsu0013.thirty

/**
 * Enum Class representing the play options included in the game.
 *
 * @param goalSum the sum dice should be combined to in order to increase the player's score
 * @param achievedScore the score achieved upon play for each option
 */
enum class PlayOption(val goalSum: Int, var achievedScore: Int = 0) {
    LOW(-1 ), //bit ugly, special case to remove the option from spinner
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    ELEVEN(11),
    TWELVE(12);
}