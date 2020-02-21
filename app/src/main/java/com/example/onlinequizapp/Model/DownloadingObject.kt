package com.example.onlinequizapp.Model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL


class DownloadingObject {

   @Throws(IOException::class)

   fun downloadJSONDataFrom(link: String):String
   {
       val stringBuilder:StringBuilder = StringBuilder()

       val url = URL(link)
       val urlConnection = url.openConnection() as HttpURLConnection

       try {
           val bufferedInputString: BufferedInputStream = BufferedInputStream(urlConnection.inputStream)

           val bufferedReader:BufferedReader = BufferedReader(InputStreamReader(bufferedInputString))

           var inputLine:String?
           inputLine = bufferedReader.readLine()

           while (inputLine != null )
           {
               stringBuilder.append(inputLine)
               inputLine = bufferedReader.readLine()
           }
       }

       finally {
           urlConnection.disconnect()
       }
       return stringBuilder.toString()
   }


    fun downloadPlantPicture(pictureName: String?):Bitmap?
    {
        var bitmap: Bitmap? = null

        val pictureLink = PLANTPLACES_COM + "/photos/$pictureName"

        val pictureURL = URL(pictureLink)
        val inputStream = pictureURL.openConnection().getInputStream()

        if (inputStream != null)
        {
            bitmap = BitmapFactory.decodeStream(inputStream)
        }

        return bitmap
    }

    companion object{
        val PLANTPLACES_COM = "https://www.plantplaces.com"
    }
}