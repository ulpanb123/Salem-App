package com.example.salemapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.backendless.files.BackendlessFile
import com.backendless.servercode.annotation.Async
import com.example.salemapplication.databinding.ActivityInitBinding
import java.io.IOException

class InitActivity : AppCompatActivity() {


    val TAG : String = "InitActivity"
    val PICK_IMAGE_CONST = 456
    private lateinit var profileImageView : ImageView
    private lateinit var editImageButton : Button
    private lateinit var nameEditText : EditText
    private lateinit var saveButton: Button
    private lateinit var profileImageFile : BackendlessFile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        val binding : ActivityInitBinding  = DataBindingUtil.setContentView(this, R.layout.activity_init)

        profileImageView = binding.profileImageView
        editImageButton = binding.editImageButton
        nameEditText = binding.editName
        saveButton = binding.saveButton

        editImageButton.setOnClickListener {
            onEditImageClick()
        }

        saveButton.setOnClickListener {
            onSaveClick()
        }
    }

    private fun onSaveClick() {
        val user : BackendlessUser = Backendless.UserService.CurrentUser()
        val name : String  = nameEditText.text.toString()

        user.setProperty("name", name)
        user.setProperty("profileImageFileURL", profileImageFile.fileURL)

        Backendless.UserService.update(user, object : AsyncCallback<BackendlessUser> {
            override fun handleFault(fault: BackendlessFault?) {
                if (fault != null) {
                    Log.e(TAG, "failed to save user details: " + fault.message)
                }
            }

            override fun handleResponse(response: BackendlessUser?) {
                Log.d(TAG, "successfully saved the user: $response")
                enterApp()
            }

        })
    }

    private fun enterApp() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onEditImageClick() {
        val getIntent : Intent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"

        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"

        val chooserIntent = Intent.createChooser(getIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(chooserIntent, PICK_IMAGE_CONST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_IMAGE_CONST) {
            if(resultCode == Activity.RESULT_OK) {
                var bm : Bitmap? = null
                if(data != null) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
                    }catch (e : IOException) {
                        e.printStackTrace()
                    }
                    profileImageView.setImageBitmap(bm)

                    val user : BackendlessUser = Backendless.UserService.CurrentUser()

                    Backendless.Files.Android.upload(bm, Bitmap.CompressFormat.JPEG, 100, user.email + "-avatar.jpeg", "avatars",
                            object : AsyncCallback<BackendlessFile> {
                                override fun handleFault(fault: BackendlessFault?) {
                                    if (fault != null) {
                                        Log.i(TAG, "couldn't upload file to server: " + fault.message)
                                    }
                                }

                                override fun handleResponse(response: BackendlessFile?) {
                                    Log.d(TAG, "successfully uploaded file")
                                    if (response != null) {
                                        profileImageFile = response
                                    }
                                }
                            })
                }
            }
        }
    }
}