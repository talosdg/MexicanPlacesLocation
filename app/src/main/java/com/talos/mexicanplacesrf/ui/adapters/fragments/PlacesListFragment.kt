package com.talos.mexicanplacesrf.ui.adapters.fragments

import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.talos.mexicanplacesrf.LoginActivity
import com.talos.mexicanplacesrf.R
import com.talos.mexicanplacesrf.application.PlacesRFApp
import com.talos.mexicanplacesrf.data.PlaceRepository
import com.talos.mexicanplacesrf.databinding.FragmentPlacesListBinding
import com.talos.mexicanplacesrf.ui.adapters.PlacesAdapter
import kotlinx.coroutines.launch
import androidx.activity.OnBackPressedCallback

class PlacesListFragment : Fragment() {
    private var _binding: FragmentPlacesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var repository: PlaceRepository

    private var wasPlayingBeforeNavigate = false

    private lateinit var firebaseAuth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialogue()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlacesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btReload.setOnClickListener {
            binding.tvConectionError.visibility = View.INVISIBLE
            binding.ivLele.visibility = View.INVISIBLE
            binding.btReload.visibility = View.INVISIBLE
            loadData()
        }
        loadData()

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.jarabe)
        //mediaPlayer.start()
        binding.tbPause.isChecked = false

        binding.tbPause.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mediaPlayer.start()
            } else {
                mediaPlayer.pause()
            }
        }

        mediaPlayer.setOnCompletionListener {
            binding.tbPause.isChecked = false
        }

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser

        binding.tvUsuario.text = buildString {
            if (user?.isEmailVerified != true) {
                append(user?.email)
                append(" ")
                append(getString(R.string.email_not_verif))

            }else{
                append(getString(R.string.user_legend))
                append(user?.email)
                binding.tvWelcome.visibility = View.VISIBLE

            }
        }

        if (user?.isEmailVerified != true){
            binding.btnReenviarVerificacion.visibility = View.VISIBLE
            binding.btnReenviarVerificacion.setOnClickListener {
                user?.sendEmailVerification()?.addOnSuccessListener{
                    Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
                }?.addOnFailureListener {
                    Toast.makeText(requireContext(),
                        getString(R.string.email_not_sent), Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            binding.btnReenviarVerificacion.visibility = View.INVISIBLE
        }


        binding.btnCerrarSesion.setOnClickListener {
            firebaseAuth.signOut()
            Toast.makeText(requireContext(), getString(R.string.closed_session), Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        _binding = null
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        if (::mediaPlayer.isInitialized && wasPlayingBeforeNavigate) {
            mediaPlayer.start()
            binding.tbPause.isChecked = true
            wasPlayingBeforeNavigate = false
        }
    }
    override fun onPause() {
        super.onPause()
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            binding.tbPause.isChecked = false
        }
    }

    private fun loadData(){
        // se instancia el repositorio desde PlacesRFApp
        repository = (requireActivity().application as PlacesRFApp).repository

        if (user?.isEmailVerified != true){
            binding.btnReenviarVerificacion.visibility = View.VISIBLE
        }
        lifecycleScope.launch {
            try {
                val places = repository.getPlacesApiary()

                binding.rvGames.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = PlacesAdapter(places) { selectedPlace ->

                        wasPlayingBeforeNavigate = mediaPlayer.isPlaying
                        binding.tvConectionError.visibility = View.INVISIBLE
                        binding.ivLele.visibility = View.INVISIBLE
                        binding.btReload.visibility = View.INVISIBLE

                        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                            mediaPlayer.pause()
                            binding.tbPause.isChecked = false
                        }
                        selectedPlace.id?.let { id ->

                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(
                                    R.id.fragment_container,
                                    PlaceDetailFragment.newInstance(id)
                                )
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                }
            }catch (e: Exception){
                binding.tvConectionError.visibility = View.VISIBLE
                binding.ivLele.visibility = View.VISIBLE
                binding.btReload.visibility = View.VISIBLE
                binding.btnReenviarVerificacion.visibility = View.INVISIBLE
            } finally {
                binding.pbLoading.visibility = View.GONE
            }
        }
    }
    private fun showExitDialogue() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.exit_title))
        builder.setMessage(getString(R.string.ask_exit))
        builder.setPositiveButton(getString(R.string.yes_answer)) { _, _ ->
            requireActivity().finishAffinity()
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }

}