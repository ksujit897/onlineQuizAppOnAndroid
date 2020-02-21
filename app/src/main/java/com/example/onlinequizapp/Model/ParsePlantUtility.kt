package com.example.onlinequizapp.Model

import org.json.JSONArray
import org.json.JSONObject

class ParsePlantUtility {


    fun parsePlantObjectFromJSONData(/*search: String?*/): List<Plant>? {

        var allPlantObjects: ArrayList<Plant> = ArrayList()
        var downloadingObject =
            DownloadingObject()
        var topLevelPlantJSONData =
            downloadingObject.downloadJSONDataFrom("https://plantplaces.com/perl/mobile/flashcard.pl")

        var topLevelPlantJSONObject: JSONObject = JSONObject(topLevelPlantJSONData)

        var plantObjectsArray: JSONArray = topLevelPlantJSONObject.getJSONArray("values")

        var index: Int = 0

        while (index < plantObjectsArray.length()) {
            var plantObject: Plant = Plant()
            var jsonObject = plantObjectsArray.getJSONObject(index)

            with(jsonObject) {

                plantObject.genus = getString("genus")
                plantObject.species = getString("species")
                plantObject.cultivar = getString("cultivar")
                plantObject.common = getString("common")
                plantObject.pictureName = getString("picture_name")
                plantObject.description = getString("description")
                plantObject.difficulty = getInt("difficulty")
                plantObject.id = getInt("id")
            }


            allPlantObjects.add(plantObject)

            index++
        }

        return allPlantObjects
    }
}