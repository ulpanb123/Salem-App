   package com.example.salemapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.example.salemapplication.databinding.ActivityRegisterBinding
import kotlin.math.sign

   class RegisterActivity : AppCompatActivity() {

    private val TAG : String = "Register"
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText
    private lateinit var registerButton : Button
    private lateinit var signInButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //synchronize Backendless
        Backendless.initApp(this, Consts.APP_ID, Consts.ANDROID_KEY)

        val binding : ActivityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        val context : Context = applicationContext

        emailEditText = binding.emailEditText
        passwordEditText = binding.passwordEditText
        registerButton = binding.registerButton
        signInButton = binding.signInButton

        registerButton.setOnClickListener {
            onRegisterButtonClicked()
        }
        signInButton.setOnClickListener {
            onSignInButtonClicked()
        }

        Backendless.UserService.isValidLogin(object : AsyncCallback<Boolean> {
            override fun handleFault(fault: BackendlessFault?) {
                if (fault != null) {
                    Log.i(TAG, "failed to check user "+ fault.message)
                }
            }

            override fun handleResponse(response: Boolean) {
                Log.i(TAG, "is user already logged in? $response")
                if(response) {
                    loginUser()
                }
            }

        })

    }

       private fun onSignInButtonClicked() {
           val email : String = emailEditText.text.toString()
           val password : String = passwordEditText.text.toString()

           Backendless.UserService.login(email, password, object : AsyncCallback<BackendlessUser> {
               override fun handleFault(fault: BackendlessFault?) {
                   if (fault != null) {
                       Log.i(TAG, "failed to sign in: "+fault.message)
                   }
               }

               override fun handleResponse(response: BackendlessUser?) {
                   Log.i(TAG, "successfully logged in")
                   loginUser()
               }
           }, true)
       }

       private fun onRegisterButtonClicked() {
           val email : String = emailEditText.text.toString()
           val password : String = passwordEditText.text.toString()

           var user : BackendlessUser = BackendlessUser()
           user.setProperty("email", email)
           user.password = password

           Backendless.UserService.register(user, object : AsyncCallback<BackendlessUser> {
               override fun handleFault(fault: BackendlessFault?) {
                   if (fault != null) {
                       Log.i(TAG, "failed to register:" + fault.message)
                   }
               }

               override fun handleResponse(response: BackendlessUser?) {
                   Log.i(TAG, "Successfully registered")
                   onSignInButtonClicked()
               }
           })
       }

       private fun loginUser() {
           // TODO: log in user
            val currentUserId : String = Backendless.UserService.loggedInUser()

           Backendless.UserService.findById(currentUserId, object : AsyncCallback<BackendlessUser> {
               override fun handleFault(fault: BackendlessFault?) {
                   if (fault != null) {
                       Log.i(TAG, "failed to find such user : " + fault.message)
                   }
               }

               override fun handleResponse(response: BackendlessUser?) {
                   Log.i(TAG, "Successfully found the user")
                   Backendless.UserService.setCurrentUser(response)

                   //if the user has a name. If yes, go to Main, if NO, go to Init
                   if (response != null) {
                       if(response.getProperty("name") == null) {
                           val intent : Intent = Intent(applicationContext, InitActivity::class.java)
                           startActivity(intent)
                           finish()
                       } else {
                           val intent : Intent = Intent(applicationContext, MainActivity::class.java)
                           startActivity(intent)
                           finish()
                       }
                   }
               }
           })
       }
   }