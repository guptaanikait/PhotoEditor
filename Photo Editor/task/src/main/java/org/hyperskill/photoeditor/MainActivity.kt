package org.hyperskill.photoeditor

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.get
import androidx.core.graphics.set
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.lang.Math.pow
import kotlin.math.pow

var bitmap1: Bitmap?=null
 var bitmap2: Bitmap?=null
lateinit var slider: Slider
lateinit var slider1: Slider
lateinit var slider2: Slider
lateinit var slider3: Slider
private const val MEDIA_REQUEST_CODE = 0
private var lastjob: Job?=null
class MainActivity : AppCompatActivity() {


    //var flag=0
    //currentImage.setImageBitmap(bitmap1)
    private val activityResultLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val photoUri = result.data?.data ?: return@registerForActivityResult
                // code to update ivPhoto with loaded image
                //bitmap1=getBitmap(contentResolver,photoUri)
                currentImage.setImageURI(photoUri)
                //val contentResolver = applicationContext.contentResolver
                //if(photoUri!=null) {
                    //bitmap1=null
                    bitmap1=currentImage.drawable.toBitmap()
                    //if(bitmap1!=null) {
                        //bitmap2=null
                        bitmap2 = bitmap1!!.copy(Bitmap.Config.ARGB_8888, true)
                        //currentImage.setImageBitmap(bitmap1)

                    //}

            }
        }

    fun getBitmap(contentResolver: ContentResolver, fileUri: Uri?): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, fileUri!!))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
            }
        } catch (e: Exception){
            null
        }
    }





    private lateinit var currentImage: ImageView
    private lateinit var btnGallery: Button
    private lateinit var btnsave:Button

    //private

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()

        //do not change this line
        currentImage.setImageBitmap(createBitmap())
        bitmap1=createBitmap()
        bitmap2= bitmap1!!.copy(Bitmap.Config.ARGB_8888, true)
        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intent)
        }
        btnsave.setOnClickListener {
            //if(Build.VERSION.SDK_INT>28){
                //nowdothis()
            //}
            if(hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                nowdothis()
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    dothis()
                } else {
                    dothis1()
                }
            }
        }
        slider.addOnChangeListener{_, value, _ ->
            brightnessslider()
        }
        slider1.addOnChangeListener { _, value, _ ->
            brightnessslider()
        }
        slider2.addOnChangeListener { _, value, _ ->
            brightnessslider()
        }
        slider3.addOnChangeListener { _, value, _ ->
            brightnessslider()
        }


    }
    private fun brightnessslider(){
        lastjob?.cancel()

            val bmhelp: Bitmap = bitmap1!!

        lastjob= lifecycleScope.launch(Dispatchers.Default) {
            //var bmhelp2 = bmhelp.copy(Bitmap.Config.ARGB_8888, true)
            //var bmhelp3 = bmhelp.copy(Bitmap.Config.ARGB_8888, true)
            var longhelp:Long=0
            //var longhelpret: Deferred<Long> = this.async {

            for (i in 0..bmhelp!!.width - 1) {
                for (j in 0..bmhelp!!.height - 1) {
                    val bitmapval = bmhelp!![i, j]
                    //val red=bitmapval.red()+value
                    //val green=bitmapval.green()+value
                    //val blue=bitmapval.blue()+value
                    // bitmap2!![i,j]=Color.rgb(red.toInt(),green.toInt(),blue.toInt())
                    var red = Color.red(bitmapval)
                    var green = Color.green(bitmapval)
                    var blue = Color.blue(bitmapval)
                    red += slider.value.toInt()
                    green += slider.value.toInt()
                    blue += slider.value.toInt()


                    if (red > 255)
                        red = 255
                    if (green > 255)
                        green = 255
                    if (blue > 255) {
                        blue = 255
                    }
                    if (red < 0)
                        red = 0
                    if (green < 0)
                        green = 0
                    if (blue < 0) {
                        blue = 0
                    }

                    var avg = (red + green + blue)
                    longhelp += avg


                }


                    //bmhelp2!![i, j] = Color.rgb(red, green, blue)
                    //bmhelp3[i,j]=Color.rgb(red,green,blue)


                }
                //return@async longhelp1
            //}

            //var longhelp:Long=longhelp1
            //else {
            //if(isActive){j
            //if(isActive){
            var avgval: Int = (longhelp / (bmhelp.width * bmhelp.height * 3)).toInt()
            var alphahelp = ((255 + slider1.value) / (255 - slider1.value)).toFloat()
            var bmhelp2 = bmhelp.copy(Bitmap.Config.RGB_565, true)
            for (i in 0..bmhelp.width - 1) {
                for (j in 0..bmhelp.height - 1) {
                    //if(isActive) {
                            ensureActive()
                             launch {
                              //ensureActive()
                            //launch(newSingleThreadContext("MyOwnThread")){

                            val bitmapval = bmhelp[i, j]
                            var red = Color.red(bitmapval)
                            var green = Color.green(bitmapval)
                            var blue = Color.blue(bitmapval)
                            red += slider.value.toInt()
                            green += slider.value.toInt()
                            blue += slider.value.toInt()
                            if (red > 255)
                                red = 255
                            if (green > 255)
                                green = 255
                            if (blue > 255) {
                                blue = 255
                            }
                            if (red < 0)
                                red = 0
                            if (green < 0)
                                green = 0
                            if (blue < 0) {
                                blue = 0
                            }
                            var newred = ((alphahelp * (red - avgval)) + avgval).toInt()
                            var newgreen = ((alphahelp * (green - avgval)) + avgval).toInt()
                            var newblue = ((alphahelp * (blue - avgval)) + avgval).toInt()
                            if (newblue > 255) {
                                newblue = 255
                            }
                            if (newred > 255) {
                                newred = 255
                            }
                            if (newgreen > 255) {
                                newgreen = 255
                            }
                            if (newred < 0)
                                newred = 0
                            if (newgreen < 0) {
                                newgreen = 0
                            }
                            if (newblue < 0) {
                                newblue = 0
                            }
                            var rgbavg = ((newred + newgreen + newblue) / 3).toInt()
                            var alpha =
                                ((255.0 + slider2.value).toDouble() / (255.0 - slider2.value).toDouble())
                            newred = (alpha * (newred - rgbavg).toDouble()).toInt() + rgbavg
                            newgreen = (alpha * (newgreen - rgbavg).toDouble()).toInt() + rgbavg
                            newblue = (alpha * (newblue - rgbavg).toDouble()).toInt() + rgbavg
                            if (newblue > 255) {
                                newblue = 255
                            }
                            if (newred > 255) {
                                newred = 255
                            }
                            if (newgreen > 255) {
                                newgreen = 255
                            }
                            if (newred < 0)
                                newred = 0
                            if (newgreen < 0) {
                                newgreen = 0
                            }
                            if (newblue < 0) {
                                newblue = 0
                            }
                            newred =
                                ((255.0 * (newred.toFloat() / 255.0).toFloat()
                                    .pow(slider3.value)).toInt())
                            //newgreen=((255.0*(newgreen.toFloat()/255.0).toFloat().pow(slider3.value)).toInt()
                            //newblue=((255.0*(newblue.toFloat()/255.0)).toFloat().pow(slider3.value)).toInt()
                            //newgreen=((255.0*(newgreen.toFloat()/255.0)).toFloat().pow(slider3.value)).toInt()
                            newgreen = ((255.0 * (newgreen.toFloat() / 255.0).toFloat()
                                .pow(slider3.value)).toInt())
                            newblue =
                                ((255.0 * (newblue.toFloat() / 255.0).toFloat()
                                    .pow(slider3.value)).toInt())


                            if (newblue > 255) {
                                newblue = 255
                            }
                            if (newred > 255) {
                                newred = 255
                            }
                            if (newgreen > 255) {
                                newgreen = 255
                            }
                            if (newred < 0)
                                newred = 0
                            if (newgreen < 0) {
                                newgreen = 0
                            }
                            if (newblue < 0) {
                                newblue = 0
                            }
                            // }

                              //ensureActive()
                             //lifecycleScope.ensureActive()
                            //if (isActive) {
                                //withContext(Dispatchers.Main) {
                                 //if(lastjob!!.isActive)
                                 //lifecycleScope.ensureActive()
                                bmhelp2[i, j] = Color.rgb(newred, newgreen, newblue)
                                //}
                            }
                        //}
                    //}



                }
            }

            if(isActive) {
                withContext(Dispatchers.Main) {
                    //currentImage.setImageBitmap(bitmap2)
                    currentImage.setImageBitmap(bmhelp2)
                }
            }
        }
        //}

    }
    
    private fun hasPermission(manifestPermission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.checkSelfPermission(manifestPermission) == PackageManager.PERMISSION_GRANTED
        } else {
            PermissionChecker.checkSelfPermission(this, manifestPermission) == PermissionChecker.PERMISSION_GRANTED
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun dothis(){
        when {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED -> {
                //Toast.makeText(this, "Storage permission is granted", Toast.LENGTH_SHORT).show()
            }
            ActivityCompat
                .shouldShowRequestPermissionRationale(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("This app needs permission to access this feature.")
                    .setPositiveButton("Grant") { _, _ ->
                        requestPermissions(
                            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            MEDIA_REQUEST_CODE
                        )
                    }
                    .setNegativeButton("Cancel", null)
                    .show()

            }
            else -> {
                requestPermissions(
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MEDIA_REQUEST_CODE
                )
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MEDIA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted, use the restricted features
                    nowdothis()
                } else {
                    // no permission, block access to this feature
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
    private  fun dothis1() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) ==
                    PackageManager.PERMISSION_GRANTED -> {
                //Toast.makeText(this, "Storage permission is granted", Toast.LENGTH_SHORT).show()
            }

            ActivityCompat
                .shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) -> {
                AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("This app needs permission to access this feature.")
                    .setPositiveButton("Grant") { _, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            MEDIA_REQUEST_CODE
                        )
                    }
                    .setNegativeButton("Cancel", null)
                    .show()

            }

            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MEDIA_REQUEST_CODE
                )
            }
        }
    }



    private  fun nowdothis(){
        val uri:Uri? =
            if (Build.VERSION.SDK_INT >= 29) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        if(uri==null){
            return
        }
        val resolver = this.contentResolver
        //val values= ContentValues()
        val bitmaptoput =
            (currentImage.drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.RGB_565, false)
        val hyperimage = ContentValues().apply {
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH,bitmaptoput.width)
            put(MediaStore.Images.Media.HEIGHT,bitmaptoput.height)

        }
        val hyperimageUri = resolver
            .insert(uri, hyperimage)
        if(hyperimageUri==null){
            return
        }
        val outStream = resolver.openOutputStream(hyperimageUri)
        bitmaptoput.compress(Bitmap.CompressFormat.JPEG, 100, outStream!!)
        outStream.close()



    }

    private fun bindViews() {
        currentImage = findViewById(R.id.ivPhoto)
        btnGallery = findViewById<Button>(R.id.btnGallery)
        slider = findViewById(R.id.slBrightness)
        btnsave=findViewById(R.id.btnSave)
        slider1=findViewById(R.id.slContrast)
        slider2=findViewById(R.id.slSaturation)
        slider3=findViewById(R.id.slGamma)

    }

    // do not change this function
    fun createBitmap(): Bitmap {
        val width = 200
        val height = 100
        val pixels = IntArray(width * height)
        // get pixel array from source

        var R: Int
        var G: Int
        var B: Int
        var index: Int

        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                // get color
                R = x % 100 + 40
                G = y % 100 + 80
                B = (x+y) % 100 + 120

                pixels[index] = Color.rgb(R,G,B)

            }
        }
        // output bitmap
        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmapOut
    }
}