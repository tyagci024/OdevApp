package com.example.stajpostdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stajpostdemo.ApiService
import com.example.stajpostdemo.MessageAdapter
import com.example.stajpostdemo.PostData
import com.example.stajpostdemo.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var buttonPost: Button
    private lateinit var mapImageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private lateinit var mapView: MapView
    private lateinit var closeButton: Button

    private val apiService: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { googleMap ->
            val efes = LatLng(37.9396, 27.3417)
            googleMap.addMarker(MarkerOptions().position(efes).title("Marker in Efes"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(efes))
        }

        editText = findViewById(R.id.editText)
        buttonPost = findViewById(R.id.button)
        mapImageView = findViewById(R.id.mapImageView)
        recyclerView = findViewById(R.id.chatRecycler)
        closeButton = findViewById(R.id.close_map_button)

        adapter = MessageAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        mapImageView.setOnClickListener {
            showImageInDialog()
        }

        closeButton.setOnClickListener {
            mapView.visibility = View.GONE
            closeButton.visibility = View.GONE
        }

        buttonPost.setOnClickListener {
            val inputText = editText.text.toString()
            val postData = PostData(inputText)

            CoroutineScope(Dispatchers.Main).launch {
                val response = withContext(Dispatchers.IO) { apiService.createPost(postData) }
                if (response.isSuccessful) {
                    editText.text.clear()
                    val receivedAnswer = response.body()
                    if (receivedAnswer != null) {
                        adapter.addMessage(postData)
                        adapter.addMessage(receivedAnswer)
                        if (inputText.contains("efes", ignoreCase = true)) {
                            mapImageView.visibility = View.VISIBLE
                            mapView.visibility = View.VISIBLE
                            closeButton.visibility = View.VISIBLE

                        } else {
                            mapImageView.visibility = View.GONE
                            mapView.visibility = View.GONE
                            closeButton.visibility = View.GONE

                        }
                    }
                } else {
                    println("Error in creating post. Error code: ${response.code()}")
                }
            }
        }
    }

    private fun showImageInDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_image, null)
        val closeButton = dialogView.findViewById<ImageButton>(R.id.closeButton)
        val dialogImage = dialogView.findViewById<ImageView>(R.id.dialogImageView)
        dialogImage.setImageResource(R.drawable.efes_map)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        closeButton.setOnClickListener{
            dialog.dismiss()
            mapImageView.visibility = View.GONE
        }
    }
}
