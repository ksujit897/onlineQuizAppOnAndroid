package com.example.onlinequizapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Display
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.onlinequizapp.Model.DownloadingObject
import com.example.onlinequizapp.Model.ParsePlantUtility
import com.example.onlinequizapp.Model.Plant
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var cameraButton: Button? = null

    private var photoButton: Button? = null
    private var imageTaken: ImageView? = null
    private var imgTaken: ImageView? = null
    private var button1: Button? = null
    private var button2: Button? = null
    private var button3: Button? = null
    private var button4: Button? = null


    var OPEN_CAMERA_BUTTON_REQUEST_ID = 1000
    var OPEN_GALLERY_BUTTON_REQUEST_ID = 2000

    // Instance Variables

    var correctAnswerIndex: Int = 0
    var correctPlant: Plant? = null

    var numberOfTimesUserAnsweredCorrectly : Int = 0
    var numberOfTimesUserAnsweredInCorrectly : Int = 0

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)




        setProgressBar(false)

        displayUIWidgets(false)


        /*       Toast.makeText(this,"ONCREATE IS CLICKED",Toast.LENGTH_LONG).show()

               val myPlant: Plant =
                   Plant("", "", "", "", "", "", 0, 0)

               Plant(
                   "Koelreuteria",
                   "paniculata",
                   "",
                   "Koelreuteria_paniculata_branch.JPG",
                   "Golden Rain Tree",
                   "Branch of Koelreuteria paniculata",
                   3,
                   24
               )

         */

        cameraButton = findViewById<Button>(R.id.btnOpenCamera)
        photoButton = findViewById<Button>(R.id.btnOpenPhotoGallery)
        imageTaken = findViewById<ImageView>(R.id.imageTaken)

        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)

        cameraButton?.setOnClickListener(View.OnClickListener {

            Toast.makeText(this, "OPen Camera  IS CLICKED", Toast.LENGTH_LONG).show()

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, OPEN_CAMERA_BUTTON_REQUEST_ID)

        })

        photoButton?.setOnClickListener(View.OnClickListener {

            Toast.makeText(this, "OPen gallery  IS CLICKED", Toast.LENGTH_LONG).show()

            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(galleryIntent, OPEN_GALLERY_BUTTON_REQUEST_ID)
        })

        // see the next plant

        btnNextPlant.setOnClickListener(View.OnClickListener {


            if (checkForInternetConnection()) {

                setProgressBar(true)

                try {


                    val innerClassObject = DownloadingPlantTask()
                    innerClassObject.execute()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                //button1?.setBackgroundColor(Color.LTGRAY)
              //  button1?.setBackgroundColor(Color.LTGRAY)
              //  button1?.setBackgroundColor(Color.LTGRAY)
              //  button1?.setBackgroundColor(Color.LTGRAY)

            var gradientColors : IntArray = IntArray(2)
                gradientColors.set(0, Color.parseColor("#FFFFFF"))
                gradientColors.set(1, Color.parseColor("#000000"))


            var gradientDrawable: GradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM , gradientColors)

            var convertDipValue = dipToFloat(this@MainActivity , 50f)
                gradientDrawable.setCornerRadius(convertDipValue)


                gradientDrawable.setStroke(5,Color.parseColor("#ffffff"))


                button1?.setBackground(gradientDrawable)
                button2?.setBackground(gradientDrawable)
                button3?.setBackground(gradientDrawable)
                button4?.setBackground(gradientDrawable)

            }
        })


    }

    fun dipToFloat(context:Context,dipValue:Float):Float{
    var metrics:DisplayMetrics = context.getResources().getDisplayMetrics()

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue , metrics )
}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OPEN_CAMERA_BUTTON_REQUEST_ID) {
            if (resultCode == Activity.RESULT_OK) {
                val imageData = data?.getExtras()?.get("data") as Bitmap
                Log.d("image is ", imageData.toString())
                imageTaken?.setImageBitmap(imageData)
            }
        }

        if (requestCode == OPEN_GALLERY_BUTTON_REQUEST_ID) {
            if (resultCode == Activity.RESULT_OK) {
                val contentURI = data?.getData()
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                imgTaken?.setImageBitmap(bitmap)
            }
        }
    }

    fun button1IsClicked(buttonView: View) {
        // Toast.makeText(this,"BUTTON ONE IS CLICKED",Toast.LENGTH_LONG).show()

        specifyTheRightAndWrongAnswer(0)
    }

    fun button2IsClicked(buttonView: View) {
        //Toast.makeText(this,"BUTTON TWO IS CLICKED",Toast.LENGTH_LONG).show()

        specifyTheRightAndWrongAnswer(1)
    }

    fun button3IsClicked(buttonView: View) {
        // Toast.makeText(this,"BUTTON THREE IS CLICKED",Toast.LENGTH_LONG).show()

        specifyTheRightAndWrongAnswer(2)
    }

    fun button4IsClicked(buttonView: View) {
        //Toast.makeText(this,"BUTTON FOUR IS CLICKED",Toast.LENGTH_LONG).show()

        specifyTheRightAndWrongAnswer(3)
    }

    inner class DownloadingPlantTask : AsyncTask<String, Int, List<Plant>>() {
        override fun doInBackground(vararg params: String?): List<Plant>? {

// can access backgroung thread but not the user interence thread

//            val downloadingObject: DownloadingObject =
            //              DownloadingObject()
            //        var jsonData = downloadingObject.downloadJSONDataFrom("http://plantplaces.com/perl/mobile/flashcard.pl")
            //      Log.i("JSON",jsonData)


            val parsePlant = ParsePlantUtility()

            return parsePlant.parsePlantObjectFromJSONData()
        }

        override fun onPostExecute(result: List<Plant>?) {
            super.onPostExecute(result)
            // can access user interference thread but not the background



            var numberOfPlant = result?.size ?: 0

            if (numberOfPlant > 0) {
                var randomPlantIndexForButton1: Int = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton2: Int = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton3: Int = (Math.random() * result!!.size).toInt()
                var randomPlantIndexForButton4: Int = (Math.random() * result!!.size).toInt()

                var allRandomPlants = ArrayList<Plant>()
                allRandomPlants.add(result.get(randomPlantIndexForButton1))
                allRandomPlants.add(result.get(randomPlantIndexForButton2))
                allRandomPlants.add(result.get(randomPlantIndexForButton3))
                allRandomPlants.add(result.get(randomPlantIndexForButton4))

                button1?.text = result.get(randomPlantIndexForButton1).toString()
                button2?.text = result.get(randomPlantIndexForButton2).toString()
                button3?.text = result.get(randomPlantIndexForButton3).toString()
                button4?.text = result.get(randomPlantIndexForButton4).toString()

                correctAnswerIndex = (Math.random() * allRandomPlants.size).toInt()
                correctPlant = allRandomPlants.get(correctAnswerIndex)

                val downloadingImageTask = DownloadingImageTask()

                val bitmap =
                    downloadingImageTask.execute(allRandomPlants.get(correctAnswerIndex).pictureName)
                        .get()
                imageTaken?.setImageBitmap(bitmap)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        Toast.makeText(this, "ONSTART IS CLICKED", Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        // Toast.makeText(this,"ONRESUME IS CLICKED",Toast.LENGTH_LONG).show()
        checkForInternetConnection()
    }

    override fun onPause() {
        super.onPause()

        Toast.makeText(this, "ONPAUSE IS CLICKED", Toast.LENGTH_LONG).show()
    }

    override fun onStop() {
        super.onStop()

        Toast.makeText(this, "ONSTOP IS CLICKED", Toast.LENGTH_LONG).show()
    }

    override fun onRestart() {
        super.onRestart()
        Toast.makeText(this, "ONRESTART IS CLICKED", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()

        Toast.makeText(this, "ONDESTROY IS CLICKED", Toast.LENGTH_LONG).show()
    }

    //    private fun checkPermission()
    fun imageViewIsClicked(view: View) {
        val randomNumber: Int = (Math.random() * 6).toInt() + 1

        Log.i("TAG", "The Random no. is: $randomNumber")

/*    if (randomNumber == 1)
    {
        btnOpenCamera.setBackgroundColor(Color.YELLOW)
    }

    else if (randomNumber == 2)
    {
        btnOpenPhotoGallery.setBackgroundColor(Color.MAGENTA)
    }
    else if (randomNumber == 3)
    {
       button1?.setBackgroundColor(Color.BLACK)
    }
    else if (randomNumber == 4)
    {
        button2?.setBackgroundColor(Color.BLUE)
    }
    else if (randomNumber == 5)
    {
        button3?.setBackgroundColor(Color.CYAN)
    }
    else if (randomNumber == 6)
    {
        button4?.setBackgroundColor(Color.DKGRAY)
    }

 */

        when (randomNumber) {
            1 -> btnOpenCamera.setBackgroundColor(Color.YELLOW)
            2 -> btnOpenPhotoGallery.setBackgroundColor(Color.MAGENTA)
            3 -> button1?.setBackgroundColor(Color.BLACK)
            4 -> button2?.setBackgroundColor(Color.BLUE)
            5 -> button3?.setBackgroundColor(Color.CYAN)
            6 -> button4?.setBackgroundColor(Color.DKGRAY)
        }
    }

    // Check for internet connection

    private fun checkForInternetConnection(): Boolean {
        val connectivityManager: ConnectivityManager =
            this.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connectivityManager.activeNetworkInfo
        val isDeviceConnectedToInternet = networkInfo != null && networkInfo.isConnectedOrConnecting

        if (isDeviceConnectedToInternet) {
            return true
        } else {
            createAlert()

            return false
        }
    }

    private fun createAlert() {
        val alertDialog: AlertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("Network Error")
        alertDialog.setMessage("Please check for the internet connection")

        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            "OK",
            { dialog: DialogInterface?, which: Int ->

                Toast.makeText(
                    this@MainActivity,
                    "You Must be connected to the internet",
                    Toast.LENGTH_SHORT
                ).show()

//                finish()
            })

        alertDialog.show()
    }

    // specify the right and the wrong answer

    private fun specifyTheRightAndWrongAnswer(userGuess: Int) {
        when (correctAnswerIndex) {
            0 -> button1?.setBackgroundColor(Color.GREEN)
            1 -> button2?.setBackgroundColor(Color.GREEN)
            2 -> button3?.setBackgroundColor(Color.GREEN)
            3 -> button4?.setBackgroundColor(Color.GREEN)
        }

        if (userGuess == correctAnswerIndex) {
            textView.setText("Right")

            numberOfTimesUserAnsweredCorrectly++
            textView.setText("$numberOfTimesUserAnsweredCorrectly")
        } else {
            var correctPlantName = correctPlant.toString()
            textView.setText("Wrong. Choose : $correctPlantName")

            numberOfTimesUserAnsweredInCorrectly++
            textView.setText("$numberOfTimesUserAnsweredInCorrectly")
        }

    }

    // Downloading Image process

    inner class DownloadingImageTask : AsyncTask<String, Int, Bitmap?>() {
        override fun doInBackground(vararg pictureName: String?): Bitmap? {
            try {


                val downloadingObject = DownloadingObject()
                val plantBitmap = downloadingObject.downloadPlantPicture(pictureName[0])
                Log.d("MainActivity","Downloaded object is "+plantBitmap)
                return plantBitmap
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)

            setProgressBar(false)

            displayUIWidgets( true)

         //   playAnimationOnView(imgTaken, Techniques.Bounce)
      //      playAnimationOnView(button1, Techniques.BounceInDown)
        //    playAnimationOnView(button2, Techniques.FadeIn)
          //  playAnimationOnView(button3, Techniques.Hinge)
            //playAnimationOnView(button4, Techniques.Pulse)
            //playAnimationOnView(textView, Techniques.RollOut)
        //    playAnimationOnView(txtRightAnswers, Techniques.Tada)
        //    playAnimationOnView(txtWrongAnswers, Techniques.ZoomInLeft)
//
            imgTaken?.setImageBitmap(result)
        }
    }

    // progress bar visibility

    private fun setProgressBar(show: Boolean){

        if (show){

            LinearLayoutProgress.setVisibility(View.VISIBLE)

            progressBar.setVisibility(View.VISIBLE) // to show progress bar

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else if (!show)
        {
            LinearLayoutProgress.setVisibility(View.GONE)

            progressBar.setVisibility(View.GONE) // to hide progress bar

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }


    // set visibility of ui widgets


    private fun displayUIWidgets(display: Boolean)
    {
        if (display)
        {
            imageTaken?.setVisibility(View.VISIBLE)
            button1?.setVisibility(View.VISIBLE)
            button2?.setVisibility(View.VISIBLE)
            button3?.setVisibility(View.VISIBLE)
            button4?.setVisibility(View.VISIBLE)

            textView.setVisibility(View.VISIBLE)

            txtWrongAnswers.setVisibility(View.VISIBLE)
            txtRightAnswers.setVisibility(View.VISIBLE)

        } else if (!display)
        {

            imageTaken?.setVisibility(View.INVISIBLE)
            button1?.setVisibility(View.INVISIBLE)
            button2?.setVisibility(View.INVISIBLE)
            button3?.setVisibility(View.INVISIBLE)
            button4?.setVisibility(View.INVISIBLE)

            textView.setVisibility(View.INVISIBLE)

            txtWrongAnswers.setVisibility(View.INVISIBLE)
            txtRightAnswers.setVisibility(View.INVISIBLE)
        }
    }

// Playing animations

/*
    private fun playAnimationOnView(view: View?, technique: Techniques) {

        YoYo.with(technique)
            .duration(700)
            .repeat(0)
            .playOn(view)
    }

 */


}
