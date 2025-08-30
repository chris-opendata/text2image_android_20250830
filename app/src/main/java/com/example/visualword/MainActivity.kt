package com.example.visualword

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.visualword.databinding.ActivityMainBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var oti: OpenAiText2Image? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//{old
//        val navView: BottomNavigationView = binding.navView

//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)
//}

        oti = OpenAiText2Image(applicationContext)
//        oti = OpenAiText2ImageTest(applicationContext)
        val myRecyclerView: RecyclerView = findViewById(R.id.chatbox)

        // Sample Data
        val items = mutableListOf<TextImageDataItem>() // Create an empty list

        // Create the adapter
        val adapter = T2IViewAdapter(items, ImageDisplaySize(150,150))

        // Set the adapter to the RecyclerView
        myRecyclerView.adapter = adapter

        // Set the layout manager
        myRecyclerView.layoutManager = LinearLayoutManager(this)

        // Send prompt button event listener
        val sendButton = findViewById<ImageView>(R.id.sendBtn)
        sendButton.setOnClickListener(listener)
    }

    private val listener = View.OnClickListener { view ->
        when (view.id) {
            R.id.sendBtn -> {
                val queryText = findViewById<EditText>(R.id.queryText)
                if (queryText.text.toString().isNotEmpty()) {
                    // calling get response to get the response.
                    val myRecyclerView: RecyclerView = findViewById(R.id.chatbox)
                    val currentAdapter = myRecyclerView.adapter as? T2IViewAdapter
                    oti?.query(queryText.text.toString(), currentAdapter)
                } else {
                    Toast.makeText(this, "Please enter your query..", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}