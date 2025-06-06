package com.talos.mexicanplacesrf

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.talos.mexicanplacesrf.databinding.ActivityLoginBinding
import com.talos.mexicanplacesrf.utils.message

class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private var email = ""
    private var contrasena = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(3000)
        var screenSplash = installSplashScreen()
        screenSplash.setKeepOnScreenCondition { false }

        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null)
            actionLoginSuccessful()

        binding.btnLogin.setOnClickListener {
            if (!validateFields()) return@setOnClickListener
            binding.progressBar.visibility = View.VISIBLE
            authenticateUser(email, contrasena)
        }
        binding.btnRegistrarse.setOnClickListener {
            if (!validateFields()) return@setOnClickListener
            binding.progressBar.visibility = View.VISIBLE
            createUser(email, contrasena)
        }
        binding.tvRestablecerPassword.setOnClickListener {
            resetPassword()
        }
    }
    private fun validateFields(): Boolean{
        email = binding.tietEmail.text.toString().trim()  //Elimina los espacios en blanco
        contrasena = binding.tietContrasena.text.toString().trim()

        if(email.isEmpty()){
            binding.tietEmail.error = getString(R.string.email_required)
            binding.tietEmail.requestFocus()
            return false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.tietEmail.error = getString(R.string.invalid_email)
            binding.tietEmail.requestFocus()
            return false
        }

        if(contrasena.isEmpty()){
            binding.tietContrasena.error = getString(R.string.password_required)
            binding.tietContrasena.requestFocus()
            return false
        }else if(contrasena.length < 6){
            binding.tietContrasena.error = getString(R.string.pass_length_error)
            binding.tietContrasena.requestFocus()
            return false
        }
        return true
    }
    private fun handleErrors(task: Task<AuthResult>){
        var errorCode = ""

        try{
            errorCode = (task.exception as FirebaseAuthException).errorCode
        }catch(e: Exception){
            e.printStackTrace()
        }

        when(errorCode){
            "ERROR_INVALID_EMAIL" -> {
                message(getString(R.string.incorrect_email_format))
                binding.tietEmail.error = getString(R.string.incorrect_email_format)
                binding.tietEmail.requestFocus()
            }
            "ERROR_WRONG_PASSWORD" -> {
                message(getString(R.string.invalid_pass))
                binding.tietContrasena.error = getString(R.string.invalid_pass)
                binding.tietContrasena.requestFocus()
                binding.tietContrasena.setText("")

            }
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                //An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.
                message(getString(R.string.email_alredy_exist))
            }
            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                message(getString(R.string.used_email))
                binding.tietEmail.error = (getString(R.string.used_email))
                binding.tietEmail.requestFocus()
            }
            "ERROR_USER_TOKEN_EXPIRED" -> {
                message(getString(R.string.expired_session))
            }
            "ERROR_USER_NOT_FOUND" -> {
                message(getString(R.string.invalid_user))
            }
            "ERROR_WEAK_PASSWORD" -> {
                message(getString(R.string.invalid_password))
                binding.tietContrasena.error = getString(R.string.length_password)
                binding.tietContrasena.requestFocus()
            }
            "NO_NETWORK" -> {
                message(getString(R.string.net_not_avilable))
            }
            else -> {
                message(getString(R.string.not_succes_auth))
            }
        }

    }
    private fun actionLoginSuccessful(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
    private fun authenticateUser(user: String, psw: String){
        firebaseAuth.signInWithEmailAndPassword(user, psw).addOnCompleteListener {authResult ->

            if(authResult.isSuccessful){
                message(getString(R.string.succes_auth))
                actionLoginSuccessful()
            }else{

                binding.progressBar.visibility = View.GONE
                handleErrors(authResult)

            }

        }
    }

    private fun createUser(user: String, psw: String){
        firebaseAuth.createUserWithEmailAndPassword(user, psw).addOnCompleteListener {authResult ->
            if(authResult.isSuccessful){

                // mail verificacion
                firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                    message(getString(R.string.verification_mail_sent))
                }?.addOnFailureListener {
                    message(getString(R.string.verification_mail_notsent))
                }

                message(getString(R.string.success_registration))
                actionLoginSuccessful()
            }else{

                binding.progressBar.visibility = View.GONE
                handleErrors(authResult)

            }


        }
    }
    private fun resetPassword(){
        val resetMail = EditText(this)
        resetMail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.recover_password))
            .setMessage(getString(R.string.set_recover_email))
            .setView(resetMail)
            .setPositiveButton(getString(R.string.send)) { _, _ ->
                val mail = resetMail.text.toString()
                if (mail.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener {
                        message(getString(R.string.recover_link))
                    }.addOnFailureListener {
                        message(getString(R.string.request_not_sent))
                    }
                }else{
                    message(getString(R.string.request_valid_email))
                }

            }.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()

            }
            .create()
            .show()
    }
}