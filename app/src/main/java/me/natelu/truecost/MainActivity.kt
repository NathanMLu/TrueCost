package me.natelu.truecost

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.inflate
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions
import com.mindorks.paracamera.Camera
import kotlinx.android.synthetic.main.fragment_first.*
import me.natelu.truecost.databinding.ActivityMainBinding
import java.security.AccessController.getContext

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var camera: Camera
    private val PERMISSION_REQUEST_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        // Create Saved Instance State
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize Camera
        camera = Camera.Builder()
            .resetToCorrectOrientation(true)
            .setTakePhotoRequestCode(Camera.REQUEST_TAKE_PHOTO)
            .setDirectory("pics")
            .setName("cost_${System.currentTimeMillis()}")
            .setImageFormat(Camera.IMAGE_JPEG)
            .setCompression(60)
            .build(this)

        // Initialize Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Toolbar
        setSupportActionBar(binding.toolbar)

        // Initialize NavController
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Initialize Camera Bar
        binding.fab.setOnClickListener { view ->
            camera.takePicture()
        }


//        camera.takePicture()
    }

    private fun generateButton(content: String, id: Int) {
        // Defining the actual layout
//        val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)

        // Creating the button
        val button = Button(this)
        button.text = content
        button.id = id

        // LayoutParams for ActionBar.LayoutParams
        val dp = 330
        val px = dp * resources.displayMetrics.density
        button.layoutParams = LinearLayout.LayoutParams(px.toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)

        // Setting Gravity, Background, Text size, and Text color
        button.gravity = Gravity.CENTER_HORIZONTAL
        button.background = resources.getDrawable(R.drawable.round_button)
        button.setTextColor(Color.BLACK)
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)

        // Adding the button to the layout
        val c = findViewById<CoordinatorLayout>(R.id.coordinator)
        c.addView(button)
    }

    fun takePicture(view: View) {
        if (!hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
            !hasPermission(android.Manifest.permission.CAMERA)
        ) {
            requestPermissions()
        } else {
            try {
                camera.takePicture()
            } catch (e: Exception) {
                Toast.makeText(
                    this.applicationContext, getString(R.string.error_taking_picture),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this.applicationContext, getString(R.string.permission_storage_rationale),
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA
                ), PERMISSION_REQUEST_CODE
            )
            return
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    try {
                        camera.takePicture()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this.applicationContext, getString(R.string.error_taking_picture),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
                val bitmap = camera.cameraBitmap
                if (bitmap != null) {
                    val img: ImageView = findViewById(R.id.imageView)
                    img.setImageBitmap(bitmap)
                    detectKeyboardOnline(bitmap)
                } else {
                    Toast.makeText(
                        this.applicationContext, getString(R.string.picture_not_taken),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun detectKeyboard(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val options = FirebaseVisionLabelDetectorOptions.Builder()
            .setConfidenceThreshold(0.6f)
            .build()
        val detector = FirebaseVision.getInstance().getVisionLabelDetector(options)

        detector.detectInImage(image)

            .addOnSuccessListener {

                if (hasKeyboard(it.map { it.label.toString() })) {
                    displayResultMessage(true)
                } else {
                    displayResultMessage(false)
                }

            }
            .addOnFailureListener {
                Toast.makeText(
                    this.applicationContext, getString(R.string.error),
                    Toast.LENGTH_SHORT
                ).show()

            }
    }

    private fun detectKeyboardOnline(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val options = FirebaseVisionCloudDetectorOptions.Builder()
            .setMaxResults(10)
            .build()
        val detector = FirebaseVision.getInstance()
            .getVisionCloudLabelDetector(options)

        detector.detectInImage(image)
            .addOnSuccessListener {
                if (hasKeyboard(it.map { it.label.toString() })) {
                    displayResultMessage(true)
                } else {
                    displayResultMessage(false)
                }

            }
            .addOnFailureListener {
                Toast.makeText(
                    this.applicationContext, getString(R.string.error),
                    Toast.LENGTH_SHORT
                ).show()

            }
    }

    private fun hasKeyboard(labels: List<String>): Boolean {
        val responseTextView = findViewById<TextView>(R.id.responseTextView)
        responseTextView.text = labels.joinToString("\n")
//        for (label in labels) {
//            if (label.contains("keyboard")) {
//                return true
//            }
//        }
        return false
//        return false
    }

    private fun displayResultMessage(hasKeyboard: Boolean) {
        val responseTextView = findViewById<TextView>(R.id.responseTextView)

        if (hasKeyboard) {
//            responseTextView.text = getString(R.string.keyboard)
        } else {
//            responseTextView.text = getString(R.string.not_keyboard)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}