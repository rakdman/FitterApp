package com.example.fittr.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.fittr.MainActivity
import com.example.fittr.R
import com.example.fittr.databinding.FragmentActivityFinishBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

class ActivityFinishFragment : DialogFragment() {

    private var _binding: FragmentActivityFinishBinding? = null
    private val binding get() = _binding!!
//    private val progressBarHandler = Handler()
//
//    var currentLevel = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentActivityFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.currentLevel.setText(currentLevel.toString())
//        binding.nextLevel.setText((currentLevel+1).toString())

        val Instance = BikeFragment.getInstance()
        var kms = Instance?.getCurrentKms()
        var coins = Instance?.getCurrentTripCoins()

        binding.kmsCount.setText("+ ${String.format("%.2f",kms)} KMs")
        binding.coinsCount.setText("+ ${coins}")
        binding.level.setText(MainActivity.getInstance()?.level.toString())

        when(Instance?.activitySelected)
        {
            BikeFragment.activityEnum.CYCLE -> {binding.activityIcon.setImageResource(R.drawable.ic_baseline_directions_bike_24)}
            BikeFragment.activityEnum.RUN -> {binding.activityIcon.setImageResource(R.drawable.ic_baseline_directions_run_24)}
            BikeFragment.activityEnum.WALK -> {binding.activityIcon.setImageResource(R.drawable.ic_baseline_directions_walk_24)}
            else -> {}
        }

//       //Progress in Metre
//
//        kms=15.0
//
//        Thread(Runnable {
//
//            for(i in 0..(kms*1000).toInt() step 10) {
//                progressBarHandler.post(Runnable {
//                    binding.progressBar.progress += 10
//                    if(binding.progressBar.progress >= binding.progressBar.max)
//                    {
//                        binding.progressBar.progress = 0
//                        currentLevel+=1
//                        binding.currentLevel.setText(currentLevel.toString())
//                        binding.nextLevel.setText((currentLevel+1).toString())
//                    }
//                })
//                try {
//                    Thread.sleep(1)
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//            }
//
//            activity?.runOnUiThread {
//                binding.end.visibility = View.VISIBLE
//            }
//        }).start()



        binding.end.setOnClickListener {
            Instance?.showControlButton()
            Instance?.resetAfterActivity()
            MainActivity.getInstance()?.enableNavigation()
            Instance?.activitySelected = BikeFragment.activityEnum.UNKNOWN
            dismiss()
        }
    }
}