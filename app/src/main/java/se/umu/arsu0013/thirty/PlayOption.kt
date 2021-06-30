package se.umu.arsu0013.thirty

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