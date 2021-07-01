package se.umu.arsu0013.thirty

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import se.umu.arsu0013.thirty.databinding.FragmentPlayBinding
import kotlin.math.max

private const val TAG = "PlayFragment"
private const val ARG_TEST_VAL = "test_val"
private const val ARG_VIEW_MODEL = "view_model"
private const val ARG_DIE_VAL_1 = "die_1"
private const val ARG_DIE_VAL_2 = "die_2"
private const val ARG_DIE_VAL_3 = "die_3"
private const val ARG_DIE_VAL_4 = "die_4"
private const val ARG_DIE_VAL_5 = "die_5"
private const val ARG_DIE_VAL_6 = "die_6"
private const val ARG_ROLL_COUNT = "rolls_remaining"
private const val ARG_TOTAL_SCORE = "total_score"
private const val ARG_REMAINING_PLAY_OPTIONS = "play_options"

class PlayFragment : Fragment(), AdapterView.OnItemSelectedListener {

    interface PlayCallbacks {
        fun onGameOver(scores: HashMap<PlayOption, Int>, totalScore: Int)
    }

    private var callbacks: PlayCallbacks? = null
    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private var playOption: PlayOption =
        PlayOption.LOW // Should match the initial selection of the Spinner


    private val rollViewModel: RollViewModel by lazy {
        ViewModelProvider(this).get(RollViewModel::class.java)
    }


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


    //TODO: check if anything should be moved to onCreate instead
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.playSelectSpinner.onItemSelectedListener = this

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
        outState.putInt(ARG_TEST_VAL, 1337)
        outState.putInt(ARG_DIE_VAL_1, rollViewModel.dice[0].die.face)
        outState.putInt(ARG_DIE_VAL_2, rollViewModel.dice[1].die.face)
        outState.putInt(ARG_DIE_VAL_3, rollViewModel.dice[2].die.face)
        outState.putInt(ARG_DIE_VAL_4, rollViewModel.dice[3].die.face)
        outState.putInt(ARG_DIE_VAL_5, rollViewModel.dice[4].die.face)
        outState.putInt(ARG_DIE_VAL_6, rollViewModel.dice[5].die.face)
        outState.putInt(ARG_TOTAL_SCORE, rollViewModel.user.totalScore)
        outState.putInt(ARG_ROLL_COUNT, rollViewModel.user.rollCount)

        //TODO: Figure out why this causes a crash
        val playOptionValues = arrayListOf<Int>()
        for (i in rollViewModel.user.playOptions.indices) {
            Log.d(TAG, "in onSaveInstanceState loop")
            playOptionValues.add(rollViewModel.user.playOptions[i].goalSum)
        }
        outState.putIntegerArrayList(ARG_REMAINING_PLAY_OPTIONS, playOptionValues)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d(TAG, "onViewStateRestored called")
        val testInt = savedInstanceState?.getInt(ARG_TEST_VAL)

        restoreDiceValues(savedInstanceState)
        restorePlayOptions(savedInstanceState)
        rollViewModel.user.totalScore = savedInstanceState?.getInt(ARG_TOTAL_SCORE) ?: 0
        rollViewModel.user.rollCount = savedInstanceState?.getInt(ARG_ROLL_COUNT) ?: 0
        updatePlayOptions()
        updateDiceValues()
        updateCurrentScore()
        updateRemainingRolls()
        Log.d(TAG, "Retrieved Int with value $testInt")
    }

    private fun restorePlayOptions(savedInstanceState: Bundle?) {
        val playOptionValues =
            savedInstanceState?.getIntegerArrayList(ARG_REMAINING_PLAY_OPTIONS) ?: listOf(
                PlayOption.LOW.goalSum,
                PlayOption.FOUR.goalSum,
                PlayOption.FIVE.goalSum,
                PlayOption.SIX.goalSum,
                PlayOption.SEVEN.goalSum,
                PlayOption.EIGHT.goalSum,
                PlayOption.NINE.goalSum,
                PlayOption.TEN.goalSum,
                PlayOption.ELEVEN.goalSum,
                PlayOption.TWELVE.goalSum
            )
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
        rollViewModel.user.playOptions = restored
    }

    private fun restoreDiceValues(savedInstanceState: Bundle?) {
        this.rollViewModel.dice[0].die.face = savedInstanceState?.getInt(ARG_DIE_VAL_1) ?: (1..6).random()
        this.rollViewModel.dice[1].die.face = savedInstanceState?.getInt(ARG_DIE_VAL_2) ?: (1..6).random()
        this.rollViewModel.dice[2].die.face = savedInstanceState?.getInt(ARG_DIE_VAL_3) ?: (1..6).random()
        this.rollViewModel.dice[3].die.face = savedInstanceState?.getInt(ARG_DIE_VAL_4) ?: (1..6).random()
        this.rollViewModel.dice[4].die.face = savedInstanceState?.getInt(ARG_DIE_VAL_5) ?: (1..6).random()
        this.rollViewModel.dice[5].die.face = savedInstanceState?.getInt(ARG_DIE_VAL_6) ?: (1..6).random()
    }


    // TODO: check if there is a more graceful way of doing this
    private fun updateDiceValues() {
        val dice = rollViewModel.dice
        binding.diceVal0.setImageResource(getDieImageRes(dice[0].die.face))
        binding.diceVal1.setImageResource(getDieImageRes(dice[1].die.face))
        binding.diceVal2.setImageResource(getDieImageRes(dice[2].die.face))
        binding.diceVal3.setImageResource(getDieImageRes(dice[3].die.face))
        binding.diceVal4.setImageResource(getDieImageRes(dice[4].die.face))
        binding.diceVal5.setImageResource(getDieImageRes(dice[5].die.face))
    }

    private fun getDieImageRes(dieValue: Int): Int {
        when (dieValue) {
            1 -> return R.drawable.die_1
            2 -> return R.drawable.die_2
            3 -> return R.drawable.die_3
            4 -> return R.drawable.die_4
            5 -> return R.drawable.die_5
            6 -> return R.drawable.die_6
        }
        return R.drawable.die_1
    }


    //This is kind of ugly, but have not found a good way to loop and keep track of bindings
    private fun updateDiceColors() {
        val dice = rollViewModel.dice

        if (dice[0].selected) {
            binding.diceVal0.alpha = 1.0f
        } else {
            binding.diceVal0.alpha = 0.5f
        }

        if (dice[1].selected) {
            binding.diceVal1.alpha = 1.0f
        } else {
            binding.diceVal1.alpha = 0.5f
        }

        if (dice[2].selected) {
            binding.diceVal2.alpha = 1.0f
        } else {
            binding.diceVal2.alpha = 0.5f
        }

        if (dice[3].selected) {
            binding.diceVal3.alpha = 1.0f
        } else {
            binding.diceVal3.alpha = 0.5f
        }

        if (dice[4].selected) {
            binding.diceVal4.alpha = 1.0f
        } else {
            binding.diceVal4.alpha = 0.5f
        }

        if (dice[5].selected) {
            binding.diceVal5.alpha = 1.0f
        } else {
            binding.diceVal5.alpha = 0.5f
        }
    }


    private fun setOnClickListeners() {
        binding.diceVal0.setOnClickListener {
            rollViewModel.toggleSelect(rollViewModel.dice[0])
            updateDiceColors()
            Log.d(TAG, "Die 0 clicked!")
        }

        binding.diceVal1.setOnClickListener {
            rollViewModel.toggleSelect(rollViewModel.dice[1])
            updateDiceColors()
            Log.d(TAG, "Die 1 clicked!")
        }

        binding.diceVal2.setOnClickListener {
            rollViewModel.toggleSelect(rollViewModel.dice[2])
            updateDiceColors()
            Log.d(TAG, "Die 2 clicked!")
        }

        binding.diceVal3.setOnClickListener {
            rollViewModel.toggleSelect(rollViewModel.dice[3])
            updateDiceColors()
            Log.d(TAG, "Die 3 clicked!")
        }

        binding.diceVal4.setOnClickListener {
            rollViewModel.toggleSelect(rollViewModel.dice[4])
            updateDiceColors()
            Log.d(TAG, "Die 4 clicked!")
        }

        binding.diceVal5.setOnClickListener {
            rollViewModel.toggleSelect(rollViewModel.dice[5])
            updateDiceColors()
            Log.d(TAG, "Die 5 clicked!")
        }

        binding.rollButton.setOnClickListener {
            if (!rollViewModel.roll()) {
                Toast.makeText(
                    this.activity,
                    "Only 3 rolls per turn allowed!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                updateDiceValues()
                updateRemainingRolls()
                rollViewModel.resetPlayedDice()
                binding.playSelectSpinner.isEnabled = true
                updatePlayOptions()
            }
        }

        binding.playButton.setOnClickListener {
            Log.d(TAG, "Play button pressed")

            // To keep player from playing different options during the same round
            binding.playSelectSpinner.isEnabled = false

            if (rollViewModel.calculateScore(playOption)) {
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
            getString(R.string.remaining_rolls, max(0, MAX_ROLLS - rollViewModel.user.rollCount))
    }

    private fun updateCurrentScore() {
        binding.currentScore.text = getString(R.string.current_score, rollViewModel.user.totalScore)
    }


    private fun updatePlayOptions() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            rollViewModel.user.playOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.playSelectSpinner.adapter = adapter
    }

    private fun checkGameOver() {
        if (rollViewModel.user.gameIsFinished()) {
            callbacks?.onGameOver(rollViewModel.user.getScores(), rollViewModel.user.totalScore)
        }
    }
}
