package com.yosha10.storyapp.ui.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.yosha10.storyapp.R
import com.yosha10.storyapp.auth.login.dataStore
import com.yosha10.storyapp.databinding.ActivityAddBinding
import com.yosha10.storyapp.helper.ViewModelFactory
import com.yosha10.storyapp.helper.createCustomTempFile
import com.yosha10.storyapp.helper.reduceFileImage
import com.yosha10.storyapp.helper.uriToFile
import com.yosha10.storyapp.pref.StoryPreference
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddActivity : AppCompatActivity() {
    private var _activityAddBinding: ActivityAddBinding? = null
    private val binding get() = _activityAddBinding
    private lateinit var currentPhotoPath: String

    private var getFile: File? = null
    private var viewModel: AddViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        init()
        setupObserve()
        setupActionBar(getString(R.string.add_page))

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this@AddActivity,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding?.btnCamera?.setOnClickListener {
            startTakePhoto()
        }

        binding?.btnGallery?.setOnClickListener {
            startGallery()
        }

        binding?.buttonAdd?.setOnClickListener {
            uploadImage()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityAddBinding = null
    }

    private fun init(){
        val pref = StoryPreference.getInstance(dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(pref, applicationContext)
        val viewModel: AddViewModel by viewModels {
            factory
        }
        this.viewModel = viewModel
    }

    private fun setupActionBar(title: String?){
        supportActionBar?.apply {
            this.title = title
            setDisplayShowHomeEnabled(true)
            setDisplayUseLogoEnabled(true)
        }
    }

    private fun setupBinding(){
        _activityAddBinding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    private fun setupObserve(){
        viewModel?.isLoading?.observe(this){
            it.getContentIfNotHandled()?.let { isLoading ->
                binding?.loading?.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewModel?.errorMessage?.observe(this){
            it.getContentIfNotHandled()?.let { msg ->
                Snackbar.make(window.decorView.rootView,msg, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel?.statusAddStory?.observe(this){ success ->
            if (success) {
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 1000)
                finish()
            }
        }

        viewModel?.imagePreview?.observe(this){ uri ->
            binding?.previewImage?.let {
                Glide.with(this@AddActivity)
                    .load(uri)
                    .into(it)
            }
        }

        viewModel?.fileImage?.observe(this){ file ->
            getFile = file
        }
    }

    private fun uploadImage(){
        if (getFile != null){
            val file = reduceFileImage(getFile as File)

            val txtDescription = binding?.edAddDescription?.text?.toString()
            val description = txtDescription?.toRequestBody("text/plain".toMediaType()) as RequestBody
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            viewModel?.postStory(imageMultiPart, description)

        } else {
            Toast.makeText(this, "Silahkan ambil atau masukkan gambar terlebih dahulu!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTakePhoto(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also { file ->
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddActivity,
                "com.yosha10.storyapp",
                file
            )
            currentPhotoPath = file.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery(){
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                getFile = file
                setFileImage(file)
                val result = BitmapFactory.decodeFile(file.path)
                binding?.previewImage?.setImageBitmap(result)
                setImagePreview(file.path)
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddActivity)
                getFile = myFile
                setFileImage(myFile)
                binding?.previewImage?.setImageURI(uri)
                setImagePreview(myFile.path)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this@AddActivity,
                    getString(R.string.access_camera),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun setFileImage(file: File){
        viewModel?.setFileImage(file)
    }

    private fun setImagePreview(image: String){
        viewModel?.setImagePreview(image)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}