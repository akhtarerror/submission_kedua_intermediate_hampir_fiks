package bangkit.mobiledev.storyappdicoding.view.add_story

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import bangkit.mobiledev.storyappdicoding.di.Injection
import bangkit.mobiledev.storyappdicoding.utils.reduceFileImage
import bangkit.mobiledev.storyappdicoding.utils.uriToFile
import bangkit.mobiledev.storyappdicoding.view.main.MainActivity
import bangkit.mobiledev.storyappdicoding.R
import bangkit.mobiledev.storyappdicoding.databinding.ActivityAddStoryBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var viewModel: AddStoryViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentImageUri: Uri? = null
    private var currentLocation: Location? = null

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                getLastLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                getLastLocation()
            }
            else -> {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                binding.cbShareLocation.isChecked = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        currentImageUri = savedInstanceState?.getParcelable(STATE_IMAGE_URI)
        if (currentImageUri != null) {
            showImage()
        }

        viewModel = ViewModelProvider(
            this,
            Injection.provideAddStoryViewModelFactory(this)
        )[AddStoryViewModel::class.java]

        binding.btnChooseFromGallery.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadStory() }

        binding.cbShareLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkLocationPermission()
            } else {
                currentLocation = null
            }
        }

        observeUploadStatus()
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                currentLocation = location
                Toast.makeText(
                    this,
                    "Location: ${location.latitude}, ${location.longitude}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                binding.cbShareLocation.isChecked = false
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
            binding.cbShareLocation.isChecked = false
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            getLastLocation()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.ivPhotoPreview.setImageURI(it)
        } ?: run {
            Toast.makeText(this, getString(R.string.img_cant_found), Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadStory() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()

            if (description.isEmpty()) {
                Toast.makeText(this, getString(R.string.desc_story_emp), Toast.LENGTH_SHORT).show()
                return
            }

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            val lat = currentLocation?.latitude?.toString()?.toRequestBody("text/plain".toMediaType())
            val lon = currentLocation?.longitude?.toString()?.toRequestBody("text/plain".toMediaType())

            viewModel.uploadStory(multipartBody, requestBody, lat, lon)
        } ?: run {
            Toast.makeText(this, getString(R.string.img_empty), Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeUploadStatus() {
        viewModel.uploadStatus.observe(this) { isSuccess ->
            binding.progressBar.visibility = View.GONE
            if (isSuccess) {
                Toast.makeText(this, getString(R.string.up_success), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                })
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, getString(R.string.up_failed, message), Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.buttonAdd.isEnabled = !isLoading
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(STATE_IMAGE_URI, currentImageUri)
    }

    companion object {
        private const val STATE_IMAGE_URI = "state_image_uri"
    }
}
