package se.umu.arsu0013.thirty

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.umu.arsu0013.thirty.databinding.FragmentRollBinding
import se.umu.arsu0013.thirty.databinding.FragmentScoreBinding
import kotlin.properties.Delegates

private const val TAG = "ScoreFragment"
private const val ARG_FINAL_SCORE = "final_score"

class ScoreFragment : Fragment() {
    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!
    private var finalScore: Int = 0

    companion object {
        fun newInstance(finalScore: Int): ScoreFragment {
            val args = Bundle().apply {
                putInt(ARG_FINAL_SCORE, finalScore)
            }
            return ScoreFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finalScore = arguments?.getInt(ARG_FINAL_SCORE) as Int
        Log.d(TAG, "ScoreFragment onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScoreBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.scoreTextView.setText("$finalScore")
        return view
    }
}