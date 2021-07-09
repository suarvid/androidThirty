package se.umu.arsu0013.thirty

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.umu.arsu0013.thirty.databinding.FragmentStartScreenBinding

private const val TAG = "StartFragment"

class StartFragment : Fragment() {

    // callBack interface to communicate with hosting Activity
    interface StartCallbacks {
        fun onPlay()
    }

    private var callbacks: StartCallbacks? = null
    private var _binding: FragmentStartScreenBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): StartFragment {
            return StartFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as StartCallbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "StartFragment onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartScreenBinding.inflate(inflater, container, false)
        val view = binding.root
        setOnClickListeners()
        return view
    }

    private fun setOnClickListeners() {

        binding.playStartButton.setOnClickListener {
            callbacks?.onPlay()
        }
    }
}