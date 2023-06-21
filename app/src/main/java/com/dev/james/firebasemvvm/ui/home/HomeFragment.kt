package com.dev.james.firebasemvvm.ui.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dev.james.firebasemvvm.R
import com.dev.james.firebasemvvm.databinding.FragmentHomeBinding
import com.dev.james.firebasemvvm.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home){

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding
    private val viewModel : MainViewModel by activityViewModels()
    private var mp: MediaPlayer? = null
    var songs_array:ArrayList<String> = arrayListOf()
    val songsList : ArrayList<String> = arrayListOf()
    var song_no:Int = 0;
    val music:String = "https://sampleswap.org/mp3/artist/46669/joevirus_Out-post-aiff-160.mp3"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        initialize()

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(requireActivity().applicationContext, android.R.layout.simple_spinner_item, songsList)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        binding?.spinner!!.adapter = aa
        var selectedText =  binding?.spinner!!.selectedItem.toString()
        binding?.spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedText = songsList[position]
                Log.e("selectedText", "${selectedText + position}")
                if(position==0){
                    song_no = 0

                }
                else if(position == 1){
                    song_no = 1

                }
                else if(position == 2){
                    song_no = 2

                }  else if(position == 3){
                    song_no = 3

                }
                else if(position == 4){
                    song_no = 4

                }
                else{
                    song_no = 5

                }
                mp?.pause()
                binding!!.play.setImageResource(R.drawable.baseline_play_arrow_24)
                InitializeMediaplayer()
            }

        }
        binding?.play?.setOnClickListener {
            if(!mp?.isPlaying()!!){
                mp?.start()
                binding!!.play.setImageResource(R.drawable.baseline_pause_24)
            }else{
                mp?.pause()
                binding!!.play.setImageResource(R.drawable.baseline_play_arrow_24)

            }
        }
        //mp?.setOnPreparedListener(null)
            mp?.setOnPreparedListener { Toast.makeText(requireContext(), "Media Buffering Complete", Toast.LENGTH_SHORT).show() }
            getUser()
        registerObserver()
        listenToChannels()
        return binding?.root
    }

    private fun initialize() {
        songsList.add("Mixwell rap")
        songsList.add("Akon iam so paid")
        songsList.add("mixwell angels")
        songsList.add("Akon Right Now na na na")
        songsList.add("Seed AI 2D")
        songsList.add("Peppy TheFiring Squad")
        songs_array.add("https://sampleswap.org/mp3/artist/31042/mixwell_new-type-rap-160.mp3")
        songs_array.add("https://cdn.tunezjam.com/audio/Akon-I'm-So-Paid-Ft-Lil-Wayne-And-Young-Jeezy-(TunezJam.com).mp3")
        songs_array.add("https://sampleswap.org/mp3/artist/31042/Mixwell_Angels-with-Guns-160.mp3")
        songs_array.add("https://cdn.tunezjam.com/audio/Akon-Right-Now-Na-Na-Na-(TunezJam.com).mp3")
        songs_array.add("https://sampleswap.org/mp3/artist/6536/Seed-AI_2D-160.mp3")
        songs_array.add("https://sampleswap.org/mp3/artist/5101/Peppy--The-Firing-Squad_YMXB-160.mp3")
        InitializeMediaplayer()

    }

   private fun InitializeMediaplayer() {
       mp = MediaPlayer()

       mp!!.setAudioAttributes(
           AudioAttributes.Builder()
               .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
               .build()
       )
       mp?.setDataSource(songs_array[song_no])
       mp?.prepareAsync()
   }


    private fun getUser() {
        viewModel.getCurrentUser()
    }

    private fun listenToChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
           viewModel.allEventsFlow.collect { event ->
               when(event){
                   is MainViewModel.AllEvents.Message ->{
                       Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                   }
               }
           }
        }
    }

    private fun registerObserver() {
        viewModel.currentUser.observe(viewLifecycleOwner,{ user ->
            user?.let {
                binding?.apply{
//                    welcomeTxt.text = "welcome ${it.email}"
                    (activity as AppCompatActivity?)!!.supportActionBar!!.setBackgroundDrawable(
                        ColorDrawable(Color.parseColor("#FF4f05e3")))
                    play.isVisible = true
                    music.isVisible = true
                    spinner.isVisible = true
                   signinButton.text = "sign out"
                    (activity as AppCompatActivity?)!!.supportActionBar?.title = "welcome ${it.email}"
                    signinButton.setOnClickListener {
                        viewModel.signOut()
                    }
                }
            }?: binding?.apply {
              //  welcomeTxt.isVisible = false
                play.isVisible = false
                music.isVisible = false
                spinner.isVisible = false
                signinButton.text = "sign in"
                (activity as AppCompatActivity?)!!.supportActionBar?.title = "Signin"
                (activity as AppCompatActivity?)!!.supportActionBar!!.setBackgroundDrawable(
                    ColorDrawable(Color.parseColor("#FF4f05e3")))
                signinButton.setOnClickListener {
                    findNavController().navigate(R.id.action_homeFragment_to_signInFragment)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}