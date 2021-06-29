package se.umu.arsu0013.thirty

data class Die(private var face: Int) {

    init {
        roll()
    }

    fun roll() {
        this.face = (1..6).random()
    }

    fun getFace(): Int {
        return this.face
    }
}