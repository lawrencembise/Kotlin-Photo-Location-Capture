package com.example.assignmentthree

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private lateinit var camera: Button
    private lateinit var clone: Button
    private lateinit var location: Button

    private lateinit var mainImage: ImageView
    private lateinit var cloneImage: ImageView

    private val cameraRequestId = 1666
    private var images: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        camera = findViewById(R.id.button4)
        clone = findViewById(R.id.button5)
        location = findViewById(R.id.button)

        mainImage = findViewById(R.id.imageView)
        cloneImage = findViewById(R.id.imageView3)


        if (

            ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                cameraRequestId
            )
        }


        camera.setOnClickListener {
            val cameraInt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraInt, cameraRequestId)


        }

        clone.setOnClickListener {
            if (images !== null) {
                cloneImage.setImageBitmap(images)
                //The location button wont be seen if we click the clone buton bfore capture!!!!!!!!!!!!
                    location.setVisibility(View.VISIBLE)
            }


        }
        location.setOnClickListener {
            //Added the temporary intent to next page
            intent = Intent(applicationContext, MapsActivity::class.java)
            intent.putExtra("picture", images)
            startActivity(intent)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraRequestId) {
            images = data?.extras?.get("data") as Bitmap
            mainImage.setImageBitmap(images)
        }
    }
    
}
