package com.valensiya.myapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import org.json.JSONObject


class MainActivity : AppCompatActivity(){
    private var locationHelper: GPSHelper? = null
    private var locationCallback: LocationCallback? = null
    private lateinit var tvCity: TextView
    private lateinit var btnGPS: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCity = findViewById(R.id.textViewCity)
        val buttonChange: Button = findViewById(R.id.buttonChooseCity)
        buttonChange.setBackgroundResource(R.color.button);
        buttonChange.setOnClickListener {
            val intent = Intent(this, ChooseCityActivity::class.java)
                startActivityForResult(intent, 1)
        }

        locationHelper = GPSHelper(this)
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationHelper?.locationRequest == null) { return }
                val location = locationResult?.lastLocation
                parseGPS(location?.latitude, location?.longitude)
                if (locationResult != null) {
                    for (loc in locationResult.locations) {
                        //parseGPS(loc.latitude, loc.longitude)
                    }
                }
            }
        }
        locationHelper?.startLocationUpdates(locationCallback)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE), 2)
        } else {
            locationHelper?.getLocation {lat, long -> parseGPS(lat, long)}
        }


        btnGPS = findViewById(R.id.getLocation)
        btnGPS.setOnClickListener {
            locationHelper?.getLocation {lat, long -> parseGPS(lat, long)}
        }
    }

    private fun parseGPS(lat: Double?, long: Double?) {
        if (lat == null || long == null) return
        locationHelper?.stopLocationUpdates(locationCallback)
        // println("Lat: $lat, Long: $long")
        btnGPS.setImageResource(R.drawable.near_me_18dp)
        requestJSON(getUrl(lat,long))
    }
    override fun onActivityResult( requestCode: Int, resultCode: Int, data: Intent? ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) { return }
        val city = data.getStringExtra("city")
        tvCity.setText("$city")
        btnGPS.setImageResource(R.drawable.near_me_disabled_18dp)
        requestJSON(getUrl(city!!))
    }

    private fun getUrl(lat : Double, lon : Double) : String{
        return "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&lang=ru&appid=96130c474a1cba9f126dba69955d342e"
    }

    private fun getUrl(city : String) : String{
        return "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&lang=ru&appid=96130c474a1cba9f126dba69955d342e"
    }

    private fun requestJSON(url: String) {
        url.httpGet().responseString { _, _, result ->
            when (result) {
                is Result.Failure -> {
                    runOnUiThread {
                        result.getException().message.toString()
                    }
                }
                is Result.Success -> {
                    runOnUiThread {
                        weatherShow(result.get())
                    }
                }
            }
        }
    }

    private fun weatherShow(json:String){
        val weather = Weather(json)

        tvCity.setText(weather.name)
        findViewById<TextView>(R.id.dt).text = weather.dt
        findViewById<TextView>(R.id.description).text = weather.description
        findViewById<ImageView>(R.id.icon).setImageResource(weather.icon)
        findViewById<TextView>(R.id.temp).text = "${weather.temp}°"
        findViewById<TextView>(R.id.tempMinMax).text = "Макс. ${weather.temp_max}°, мин. ${weather.temp_min}°"
        findViewById<TextView>(R.id.sunrise).text = weather.sunrise
        findViewById<TextView>(R.id.sunset).text = weather.sunset
        findViewById<TextView>(R.id.pressure).text = "${weather.pressure} мм.рт.ст"
        findViewById<TextView>(R.id.humidity).text = "${weather.humidity} %"
        findViewById<TextView>(R.id.windSpeed).text = "${weather.wind_direction} ${weather.windSpeed} км/ч"
        findViewById<TextView>(R.id.temp_feel).text = "${weather.temp_feel}°"
        findViewById<TextView>(R.id.visibility).text = "${weather.visibility} км"
        findViewById<TextView>(R.id.clouds).text = "${weather.clouds}%"
    }

    override fun onPause() {
        super.onPause()
        locationHelper?.stopLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show()
                    locationHelper?.getLocation {lat, long -> parseGPS(lat, long)}
                } else {
                    Toast.makeText(this, "Разрешение не получено", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
            }
        }
    }

}