package com.valensiya.myapp

import android.annotation.SuppressLint
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class Weather() {
    var lon : Double = 0.0
    var lat : Double = 0.0
    var temp : Int = 0
    var temp_feel : Int = 0
    var pressure : Int = 0 //Атмосферное давление гПа
    var humidity : Int = 0 //Влажность %
    var temp_min : Int = 0
    var temp_max : Int = 0
    var windSpeed : Int = 0 //Скорость ветра  метр / сек
    var wind_direction : String = "" //Направление ветра градусы (метеорологические)
    var dt : String = "" //время расчета данных
    var timezone : Int = 0 //сдвиг в секундах  от UTC
    var name : String = "" //город
    var sunrise: String = "" // Время восхода, unix, UTC
    var sunset: String = "" // Время заката, unix, UTC
    var clouds  : Int = 0 //облачность
    var visibility : Int = 0 //видимость
    var description : String = "" //описание
    var icon : Int = 0 //картинка

    constructor(json:String):this(){
        readJson(json)
    }

     private fun readJson(json:String) {
         val json = JSONObject(json)

         //получение параметров из корня JSON
         dt = getDate(json.optInt("dt", 0)+timezone)
         name = json.optString("name", "Belgorod")
         visibility = json.optInt("visibility", 0)/1000

         //получение параметров из объекта внутри JSON
         //val coord = json.getJSONObject("coord")
         //lon = coord.optInt("lon", 0)
         //lat = coord.optInt("lat", 0)

         clouds = json.getJSONObject("clouds").optInt("all", 0)
         description = json.getJSONArray("weather").getJSONObject(0).optString("description", "")
         icon = getIcon(json.getJSONArray("weather").getJSONObject(0).optString("icon", ""))

         val main = json.getJSONObject("main")
         temp = main.optInt("temp", 0)
         temp_feel = main.optInt("feels_like", 0)
         temp_min = main.optInt("temp_min", 0)
         temp_max = main.optInt("temp_max", 0)
         pressure = (main.optInt("pressure", 0)/1.3333).toInt()
         humidity = main.optInt("humidity", 0)

         val wind = json.getJSONObject("wind")
         windSpeed = (3.6 * wind.optInt("speed", 0)).toInt()
         wind_direction = degToCompass(wind.optInt("deg", 0))

         val sys = json.getJSONObject("sys")
         timezone = json.optInt("timezone", 0)
         sunrise = getTime(sys.optInt("sunrise", 0)+timezone)
         sunset = getTime(sys.optInt("sunset", 0)+timezone)

     }

    private fun getTime(time: Int): String {
        val sdf = SimpleDateFormat("HH:mm")
        val fDate = Date(time * 1000L)
        return sdf.format(fDate)
    }
    private fun getDate(time: Int): String {
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("ru"))
        val fDate = Date(time * 1000L)
        return sdf.format(fDate)
    }

    private fun degToCompass(num : Int) : String {
        val ind = Math.floor((num / 22.5) + 0.5);
        val arrDirection : Array<String> = arrayOf("С", "ССВ", "СВ", "ВСВ", "В", "ВЮВ", "ЮВ", "ЮЮВ", "Ю", "ЮЮЗ", "ЮЗ", "ЗЮЗ", "З", "ЗСЗ", "СЗ", "ССЗ")
        return arrDirection[((ind % 16).toInt())];
    }

    private fun getIcon(icon:String) : Int{
        var id: Int
        when (icon){
            "01d" -> id = R.drawable.icon_01d
            "01n" -> id = R.drawable.icon_01n
            "02d" -> id = R.drawable.icon_02d
            "02n" -> id = R.drawable.icon_02n
            "03d" -> id = R.drawable.icon_03d
            "03n" -> id = R.drawable.icon_02n
            "04d" -> id = R.drawable.icon_04d
            "04n" -> id = R.drawable.icon_02n
            "09d" -> id = R.drawable.icon_09d
            "09n" -> id = R.drawable.icon_09d
            "10d" -> id = R.drawable.icon_10d
            "10n" -> id = R.drawable.icon_10n
            "11d" -> id = R.drawable.icon_11d
            "11n" -> id = R.drawable.icon_11d
            "13d" -> id = R.drawable.icon_13d
            "13n" -> id = R.drawable.icon_13n
            "50d" -> id = R.drawable.icon_50d
            "50n" -> id = R.drawable.icon_50n
            else -> id = R.drawable.icon_01d
        }
        return id
    }
}