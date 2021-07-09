package se.umu.arsu0013.thirty

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import se.umu.arsu0013.thirty.databinding.FragmentPlayBinding
import kotlin.math.max

private const val TAG = "PlayFragment"
// Values used for storing and retrieving state
private const val ARG_DIE_VAL_1 = "die_1"
private const val ARG_DIE_VAL_2 = "die_2"
private const val ARG_DIE_VAL_3 = "die_3"
private const val ARG_DIE_VAL_4 = "die_4"
private const val ARG_DIE_VAL_5 = "die_5"
private const val ARG_DIE_VAL_6 = "die_6"
private val ARG_DIE_VALS = listOf(
    ARG_DIE_VAL_1, ARG_DIE_VAL_2, ARG_DIE_VAL_3, ARG_DIE_VAL_4,
    ARG_DIE_VAL_5, ARG_DIE_VAL_6
)
private const val ARG_ROLL_COUNT = "rolls_remaining"
private const val ARG_TOTAL_SCORE = "total_score"
private const val ARG_REMAINING_PLAY_OPTIONS = "play_options"

class PlayFragment : Fragment(), AdapterView.OnItemSelectedListener {

    //callBack interface to communicate with the hosting activity
    interface PlayCallbacks {
        fun onGameOver(scores: HashMap<PlayOption, Int>, totalScore: Int)
    }

    private var callbacks: PlayCallbacks? = null
    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!

    private var playOption: PlayOption =
        PlayOption.LOW // Should match the initial selection of the Spinner


    private val playViewModel: PlayViewModel by lazy {
        ViewModelProvider(this).get(PlayViewModel::class.java)
    }

    // list of Die objects stored in the viewModel
    private var modelDice: List<Die>? = null
    // list of dice bindings in the layout
    private var bindingDice: List<ImageButton>? = null
    // combination of the above lists, simplifies setting onClickListeners, updating values etc.
    private var dicePairs: List<Pair<Die, ImageButton>>? = null


    companion object {
        fun newInstance(): PlayFragment {
            return PlayFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as PlayCallbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "PlayFragment onCreate() called")
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.playSelectSpinner.onItemSelectedListener = this
        bindingDice = listOf(
            binding.diceVal0, binding.diceVal1, binding.diceVal2,
            binding.diceVal3, binding.diceVal4, binding.diceVal5
        )
        modelDice = playViewModel.dice
        dicePairs = modelDice!!.zip(bindingDice!!)

        setOnClickListeners()
        updateDiceValues()
        updateDiceColors()
        updateRemainingRolls()
        updatePlayOptions()
        updateCurrentScore()
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState called")

        saveDiceState(outState)
        outState.putInt(ARG_TOTAL_SCORE, playViewModel.user.totalScore)
        outState.putInt(ARG_ROLL_COUNT, playViewModel.user.rollCount)

        val playOptionValues = arrayListOf<Int>()
        for (i in playViewModel.user.playOptions.indices) {
            Log.d(TAG, "in onSaveInstanceState loop")
            playOptionValues.add(playViewModel.user.playOptions[i].goalSum)
        }
        outState.putIntegerArrayList(ARG_REMAINING_PLAY_OPTIONS, playOptionValues)
    }

    private fun saveDiceState(outState: Bundle) {
        for (pair in modelDice?.zip(ARG_DIE_VALS)!!) {
            outState.putSerializable(
                pair.second,
                Triple(pair.first.face, pair.first.selected, pair.first.played)
            )
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d(TAG, "onViewStateRestored called")

        restoreDiceValues(savedInstanceState)
        restorePlayOptions(savedInstanceState)
        playViewModel.user.totalScore = savedInstanceState?.getInt(ARG_TOTAL_SCORE) ?: 0
        playViewModel.user.rollCount = savedInstanceState?.getInt(ARG_ROLL_COUNT) ?: 0
        updatePlayOptions()
        updateDiceValues()
        updateDiceColors()
        updateCurrentScore()
        updateRemainingRolls()
    }

    // Restore the remaining play options based on the saved goal sums
    private fun restorePlayOptions(savedInstanceState: Bundle?) {
        val playOptionValues =
            savedInstanceState?.getIntegerArrayList(ARG_REMAINING_PLAY_OPTIONS)
                ?: playViewModel.user.playOptionGoalSums()
        val restored = mutableListOf<PlayOption>()
        playOptionValues.forEach { value ->
            when (value) {
                PlayOption.LOW.goalSum -> restored.add(PlayOption.LOW)
                PlayOption.FOUR.goalSum -> restored.add(PlayOption.FOUR)
                PlayOption.FIVE.goalSum -> restored.add(PlayOption.FIVE)
                PlayOption.SIX.goalSum -> restored.add(PlayOption.SIX)
                PlayOption.SEVEN.goalSum -> restored.add(PlayOption.SEVEN)
                PlayOption.EIGHT.goalSum -> restored.add(PlayOption.EIGHT)
                PlayOption.NINE.goalSum -> restored.add(PlayOption.NINE)
                PlayOption.TEN.goalSum -> restored.add(PlayOption.TEN)
                PlayOption.ELEVEN.goalSum -> restored.add(PlayOption.ELEVEN)
                PlayOption.TWELVE.goalSum -> restored.add(PlayOption.TWELVE)
            }
        }
        playViewModel.user.playOptions = restored
    }

    private fun restoreDiceValues(savedInstanceState: Bundle?) {
        for (pair in modelDice?.zip(ARG_DIE_VALS)!!) {
            val triple =
                savedInstanceState?.getSerializable(pair.second) as Triple<Int, Boolean, Boolean>?
            pair.first.face = triple?.first ?: (1..6).random()
            pair.first.selected = triple?.second ?: false
            pair.first.played = triple?.third ?: false
        }
    }

    private fun updateDiceValues() {
        for (pair in dicePairs!!) {
            pair.second.setImageResource(getDieImageRes(pair.first.face))
        }
    }

    private fun getDieImageRes(dieValue: Int): Int {
        when (dieValue) {
            1 -> return R.drawable.white1
            2 -> return R.drawable.white2
            3 -> return R.drawable.white3
            4 -> return R.drawable.white4
            5 -> return R.drawable.white5
            6 -> return R.drawable.white6
        }
        return R.drawable.white1
    }


    private fun updateDiceColors() {
        for (pair in dicePairs!!) {
            if (pair.first.selected) {
                pair.second.alpha = 1.0f
            } else {
                pair.second.alpha = 0.5f
            }
        }
    }



    private fun setOnClickListeners() {
        // Iterate through pairs of die objects and corresponding binding in the layout
        for (pair in dicePairs!!) {
            pair.second.setOnClickListener {
                playViewModel.toggleSelect(pair.first)
                updateDiceColors()
                Log.d(TAG, "Die ${pair.first} clicked!")
            }
        }

        binding.rollButton.setOnClickListener {
            if (!playViewModel.roll()) {
                Toast.makeText(
                    this.activity,
                    "Only $MAX_ROLLS rolls per turn allowed!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                updateDiceValues()
                updateRemainingRolls()
                playViewModel.resetPlayedDice()
                binding.playSelectSpinner.isEnabled = true
                updatePlayOptions()
            }
        }

        binding.playButton.setOnClickListener {
            Log.d(TAG, "Play button pressed")

            // To keep player from playing different options during the same round
            binding.playSelectSpinner.isEnabled = false

            if (playViewModel.calculateScore(playOption)) {
                updatePlayOptions()
                binding.playSelectSpinner.isEnabled = true
            }

            updateDiceValues()
            updateCurrentScore()
            updateRemainingRolls()
            updateDiceColors()
            checkGameOver()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        playOption = parent?.getItemAtPosition(position) as PlayOption
        Log.d(TAG, "playOption $playOption selected")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented") // is never really called
    }


    private fun updateRemainingRolls() {
        binding.remainingRolls.text =
            getString(R.string.remaining_rolls, max(0, MAX_ROLLS - playViewModel.user.rollCount))
    }

    private fun updateCurrentScore() {
        binding.currentScore.text = getString(R.string.current_score, playViewModel.user.totalScore)
    }


    private fun updatePlayOptions() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            playViewModel.user.playOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.playSelectSpinner.adapter = adapter
    }

    private fun checkGameOver() {
        if (playViewModel.user.gameIsFinished()) {
            callbacks?.onGameOver(playViewModel.user.getScores(), playViewModel.user.totalScore)
        }
    }
}
