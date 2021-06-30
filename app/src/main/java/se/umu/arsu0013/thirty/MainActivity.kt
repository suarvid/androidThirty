package se.umu.arsu0013.thirty

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

//TODO: Make sure state (like selected die) is maintained through rotations
private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), PlayFragment.PlayCallbacks, StartFragment.StartCallbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = StartFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onPlay() {
        val fragment = PlayFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack("play")
            .commit()
    }

    override fun onAbout() {
        TODO("Not yet implemented")
    }

    override fun onQuit() {
        finishAffinity()
    }

    override fun onGameOver(scores: HashMap<PlayOption, Int>, totalScore: Int) {
        Log.d(TAG, "Game Over in MainActivity")
        val fragment = ScoreFragment.newInstance(scores, totalScore)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}