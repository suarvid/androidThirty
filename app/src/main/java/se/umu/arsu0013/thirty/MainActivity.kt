package se.umu.arsu0013.thirty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

//TODO: Make sure state (like selected die) is maintained through rotations
private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), PlayFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = PlayFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onGameOver(score: Int) {
        Log.d(TAG, "Game Over in MainActivity with score $score")
        val fragment = ScoreFragment.newInstance(score)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}