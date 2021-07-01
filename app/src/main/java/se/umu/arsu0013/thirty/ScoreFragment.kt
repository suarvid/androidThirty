package se.umu.arsu0013.thirty

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.umu.arsu0013.thirty.databinding.FragmentScoreBinding

private const val TAG = "ScoreFragment"
private const val ARG_PLAY_OPTION_SCORES = "play_option_scores"
private const val ARG_FINAL_SCORE = "final_score"

class ScoreFragment : Fragment() {
    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!
    private var finalScore: Int? = 0

    // Should a Fragment really hold this data?
    // Might be ok since it's just scores, but idk
    private var scores: HashMap<PlayOption, Int>? = null

    companion object {
        fun newInstance(scores: HashMap<PlayOption, Int>, totalScore: Int): ScoreFragment {
            val args = Bundle().apply {
                putSerializable(ARG_PLAY_OPTION_SCORES, scores)
                putInt(ARG_FINAL_SCORE, totalScore)
            }
            return ScoreFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ScoreFragment onCreate() called")
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScoreBinding.inflate(inflater, container, false)
        val view = binding.root
        scores = arguments?.getSerializable(ARG_PLAY_OPTION_SCORES) as HashMap<PlayOption, Int>
        finalScore = arguments?.getInt(ARG_FINAL_SCORE)
        updateScoreTexts()

        binding.playAgainButton.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            activity?.startActivity(intent)
        }
        return view
    }


    // yikes
    private fun updateScoreTexts() {
        binding.lowScoreTextview.text = getString(R.string.low_score, scores?.get(PlayOption.LOW))
        binding.fourScoreTextview.text = getString(
            R.string.four_score,
            scores?.get(PlayOption.FOUR)
        )
        binding.fiveScoreTextview.text = getString(
            R.string.five_score,
            scores?.get(PlayOption.FIVE)
        )
        binding.sixScoreTextview.text = getString(R.string.six_score, scores?.get(PlayOption.SIX))
        binding.sevenScoreTextview.text = getString(
            R.string.seven_score,
            scores?.get(PlayOption.SEVEN)
        )
        binding.eightScoreTextview.text = getString(
            R.string.eight_score,
            scores?.get(PlayOption.EIGHT)
        )
        binding.nineScoreTextview.text = getString(
            R.string.nine_score,
            scores?.get(PlayOption.NINE)
        )
        binding.tenScoreTextview.text = getString(R.string.ten_score, scores?.get(PlayOption.TEN))
        binding.elevenScoreTextview.text = getString(
            R.string.eleven_score,
            scores?.get(PlayOption.ELEVEN)
        )
        binding.twelveScoreTextview.text = getString(
            R.string.twelve_score,
            scores?.get(PlayOption.TWELVE)
        )
        binding.totalScoreTextView.text = getString(R.string.final_score, finalScore)
    }

}