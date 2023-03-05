package com.example.ledrgbesp8266firebasekotlin

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ledrgbesp8266firebasekotlin.library.ColorPicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView2 = findViewById<ImageView>(R.id.imageView2)
        Glide.with(this).load("data:image/png;base64,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").into(imageView2)

        var state: Int
        var cycle: Int
        val database = Firebase.database
        val databaseReference = database.reference

        val ledColorPicker: ColorPicker = findViewById(R.id.ledColorPicker)
        val ledToggleButton: ToggleButton = findViewById(R.id.ledToggleButton)
        val cycleToggleButton: ToggleButton = findViewById(R.id.cycleToggleButton)

        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                state = dataSnapshot.child("state").getValue<Int>()!!
                cycle = dataSnapshot.child("cycle").getValue<Int>()!!

                ledToggleButton.isChecked = state != 0
                cycleToggleButton.isChecked = cycle != 0

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        ledColorPicker.listener = object : ColorPicker.Listener{
            override fun colorChanged(color: Int) {
                val red: Int = Color.red(color)
                val green: Int = Color.green(color)
                val blue: Int = Color.blue(color)

                databaseReference.child("cycle").setValue(0)
                databaseReference.child("red").setValue(255-red)
                databaseReference.child("green").setValue(255-green)
                databaseReference.child("blue").setValue(255-blue)
            }
        }

        ledToggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                databaseReference.child("state").setValue(1)
            } else {
                databaseReference.child("state").setValue(0)
            }
        }

        cycleToggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                databaseReference.child("cycle").setValue(1)
            } else {
                databaseReference.child("cycle").setValue(0)
            }
        }

    }
}