package com.example.guessthesong

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText


class SongActivity : AppCompatActivity() {
    override fun onNewIntent(intent: Intent) {
        val extras = intent.extras
    }

    private var numberOftries = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        numberOftries = 5
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        var lyr = intent.getStringExtra(getString(R.string.lyrics))
        var title = intent.getStringExtra(getString(R.string.title))
        var author = intent.getStringExtra(getString(R.string.artist))
        lyr = lyr.replace("\n", System.getProperty("line.separator"))
        val lyric = findViewById<TextView>(R.id.textView6)
        lyric.setText(lyr)
        val userLyric = findViewById<TextInputEditText>(R.id.text)
        val successButton1 = findViewById<Button>(R.id.button)
        successButton1.setOnClickListener {
            if (userLyric.text != null) {
                if (userLyric.text.toString() == title) {
                    val builder = AlertDialog.Builder(this@SongActivity)
                    builder.setTitle(getString(R.string.correct))
                    val message = builder.setMessage(getString(R.string.yes).plus(title).plus(getString(R.string.cong)))
                    builder.setPositiveButton(getString(R.string.woohoo)) { dialog, which ->
                        val i = Intent(this@SongActivity, ProfileNewActivity::class.java)
                        i.putExtra(getString(R.string.guessed), title)
                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        startActivity(i)
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                } else {
                    val builder = AlertDialog.Builder(this@SongActivity)
                    builder.setTitle(getString(R.string.incorrecter))
                    if (numberOftries == 0) {
                        builder.setMessage(
                                getString(R.string.no).plus(userLyric.text.toString()).plus(getString(R.string.noMessage)).plus(
                                        "\n"
                                ).plus(getString(R.string.hint).plus(author))
                        )
                        numberOftries = 5
                    } else {
                        builder.setMessage(
                                getString(R.string.no).plus(userLyric.text.toString()).plus(getString(R.string.noMessage)))
                        numberOftries--
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }
        }


    }

}
