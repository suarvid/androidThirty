package se.umu.arsu0013.thirty

import android.widget.Toast
import androidx.lifecycle.ViewModel
import kotlin.coroutines.coroutineContext

const val MAX_THROWS = 3

class RollViewModel: ViewModel() {

     var dice = listOf<Tuple<Die, Boolean>>(
        Tuple(Die(0), false),
        Tuple(Die(0), false),
        Tuple(Die(0), false),
        Tuple(Die(0), false),
        Tuple(Die(0), false),
        Tuple(Die(0), false),
    )

    val user = User()

    init {
        // change die faces from 0 initially, don't increment
        rollAll()
    }

    fun roll(): Boolean {
        var count = 0
        if (user.getRollCount() < MAX_THROWS) {
            this.dice.map { tuple ->
                if (tuple.second) {
                    tuple.first.roll()
                    count += 1
                }
            }

            // only count rolls that actually roll a die
            if (count > 0) {
                user.incrementThrowCount()
            }

            return true
        }
        return false
    }

    fun rollAll() {
        this.dice.map { tuple ->
            tuple.first.roll()
        }
    }

    fun toggleSelect(tuple: Tuple<Die, Boolean>) {
        tuple.second = !tuple.second
    }

}