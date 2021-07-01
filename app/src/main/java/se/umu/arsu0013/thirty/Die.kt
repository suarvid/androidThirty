package se.umu.arsu0013.thirty

// TODO: Maybe change from the Triples to Die by implementing their behaviour here instead
data class Die(var face: Int) {

    init {
        roll()
    }

    fun roll() {
        this.face = (1..6).random()
    }
}