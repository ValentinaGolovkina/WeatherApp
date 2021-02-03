package com.valensiya.myapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ChooseCityActivity:AppCompatActivity(), CityClickListener  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_city)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewCity)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val cities = resources.getStringArray(R.array.city_array)
        recyclerView.adapter = AdapterRecyclerView(this, cities, this)

    }

    override fun onCityClickListener(data:String) {
        //Toast.makeText(this,"Вы выбрали: $data", Toast.LENGTH_LONG).show()
        val intent = Intent()
        intent.putExtra("city", data)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}