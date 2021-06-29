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
import se.umu.arsu0013.thirty.databinding.FragmentRollBinding
import kotlin.math.max

private const val TAG = "PlayFragment"

class PlayFragment : Fragment(), AdapterView.OnItemSelectedListener {

    interface Callbacks {
        fun onGameOver(score: Int)
    }

    private var callbacks: Callbacks? = null
    private var _binding: FragmentRollBinding? = null
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
        callbacks = context as Callbacks?
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
        _binding = FragmentRollBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.playSelectSpinner.onItemSelectedListener = this
        /*
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.play_options_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.playSelectSpinner.adapter = adapter
        }
        */

        setOnClickListeners()
        updateDiceValues()
        updateDiceColors()
        updateRemainingRolls()
        updatePlayOptions()
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // TODO: check if there is a more graceful way of doing this
    private fun updateDiceValues() {
        val dice = rollViewModel.dice
        binding.diceVal0.setImageResource(getDieImageRes(dice[0].die.getFace()))
        binding.diceVal1.setImageResource(getDieImageRes(dice[1].die.getFace()))
        binding.diceVal2.setImageResource(getDieImageRes(dice[2].die.getFace()))
        binding.diceVal3.setImageResource(getDieImageRes(dice[3].die.getFace()))
        binding.diceVal4.setImageResource(getDieImageRes(dice[4].die.getFace()))
        binding.diceVal5.setImageResource(getDieImageRes(dice[5].die.getFace()))
    }

    private fun getDieImageRes(dieValue: Int): Int {
        when(dieValue) {
            1 -> return R.drawable.red1
            2 -> return R.drawable.red2
            3 -> return R.drawable.red3
            4 -> return R.drawable.red4
            5 -> return R.drawable.red5
            6 -> return R.drawable.red6
        }
        return R.drawable.red1
    }


    // TODO: This is kind of ugly, but have not found a good way to loop and keep track of bindings
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
                if (rollViewModel.user.getRollCount() == 0) { // Have to roll all at start of round
                    rollViewModel.rollAll()
                }
                updateDiceValues()
                updateRemainingRolls()
                rollViewModel.resetPlayedDice()
                binding.playSelectSpinner.isEnabled = true
                updatePlayOptions()
            }
        }

        // TODO: Rename button to playButton and change text
        binding.playButton.setOnClickListener {
            Log.d(TAG, "Select button pressed")

            // To keep player from playing different options during the same round
            binding.playSelectSpinner.isEnabled = false

            rollViewModel.user.calculateScore(playOption, rollViewModel.dice)

            updateCurrentScore()
            resetRollCount()
            updateRemainingRolls()
            updateDiceColors()
            checkGameOver()
            //updatePlayOptions()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        playOption = parent?.getItemAtPosition(position) as PlayOption
        Log.d(TAG, "playOption $playOption selected")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented") // check when this is called
    }

    private fun updateRemainingRolls() {
        binding.remainingRolls.setText("Remaining rolls: ${max(0,MAX_ROLLS - rollViewModel.user.getRollCount())}")
    }

    private fun updateCurrentScore() {
        binding.currentScore.setText("Current score: ${rollViewModel.user.getScore()}")
    }

    private fun resetRollCount() {
        rollViewModel.resetRollCount()
    }

    private fun updatePlayOptions() {
        val adapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, rollViewModel.user.playOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.playSelectSpinner.adapter = adapter
    }

    private fun checkGameOver() {
        if (rollViewModel.user.gameIsFinished()) {
            callbacks?.onGameOver(rollViewModel.user.getScore())
        }
    }
}
