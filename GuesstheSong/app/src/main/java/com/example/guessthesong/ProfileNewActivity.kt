package com.example.guessthesong

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProfileNewActivity : AppCompatActivity() {
    private var songsAndLyrics = arrayListOf<String>()
    private val myTitleList = arrayListOf<String>()
    private val list = ArrayList<MyModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        adapterImplementation()
        val buttonToMap = findViewById<Button>(R.id.button)
        buttonToMap.setOnClickListener{
            val i = Intent(this@ProfileNewActivity, MapsActivity::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(i)
        }

    }
    private fun adapterImplementation(){
        val markers = intent.getStringArrayListExtra(getString(R.string.lyrandsongs))
        if(markers != null) {
            songsAndLyrics = markers
        }
        var imageModelArrayList: ArrayList<MyModel> = populateList()
        setContentView(R.layout.activity_profile_new)
        val recyclerView = findViewById<View>(R.id.my_recycler_view) as? RecyclerView
        val layoutManager = LinearLayoutManager(this)
        if (recyclerView != null) {
            recyclerView.layoutManager = layoutManager
        }
        val mAdapter = Adapter(imageModelArrayList)
        if (recyclerView != null) {
            recyclerView.adapter = mAdapter
        }
    }
    private fun populateList(): ArrayList<MyModel> {

        val myLyricsList = arrayListOf<String>()
        val myArtistList = arrayListOf<String>()
        // find if lyrics are from the same song and concatenate them
        val myImageList = R.drawable.note
        val myImageNameList = getString(R.string.unknown_song)

        for(i in 0 until songsAndLyrics.size){
            try {
                for (j in i + 1 until songsAndLyrics.size) {
                    if (songsAndLyrics[i].substringBefore('/') == songsAndLyrics[j].substringBefore(
                            '/'
                        )
                    ) {
                        songsAndLyrics[i] += "\n"
                        songsAndLyrics[i] += songsAndLyrics[j].substringAfter('/')
                        songsAndLyrics.removeAt(j)
                    }
                }
            }catch (e:Exception){}
        }
          // get titles, lyrics and artist separately using substrings
        for (i in 0 until songsAndLyrics.size) {
            myLyricsList.add(songsAndLyrics[i].substringAfter('/'))
            var substArtist = songsAndLyrics[i].substringBefore('/')
            substArtist = substArtist.substringBefore('(')
            substArtist = substArtist.replace('_', ' ')
            myArtistList.add(substArtist)
            var substTitle = songsAndLyrics[i].substringBefore('/')
            substTitle = substTitle.substringAfter('(')
            substTitle = substTitle.substringBefore(')')
            substTitle = substTitle.replace('_', ' ')
            substTitle = substTitle.substringBefore('.')
            myTitleList.add(substTitle)
        }

        for (i in 0 until  myLyricsList.size) {
            val imageModel = MyModel()
            imageModel.setNames(myImageNameList)
            imageModel.setImage_drawables(myImageList)
            imageModel.setTitle(myTitleList[i])
            imageModel.setLyric(myLyricsList[i])
            imageModel.setArtist(myArtistList[i])
            list.add(imageModel)
        }
        return list
    }

    override fun onResume() {

        super.onResume()
        if(intent.getStringArrayListExtra(getString(R.string.lyrandsongs))!= null) {
            val markers = intent.getStringArrayListExtra(getString(R.string.lyrandsongs))

        }
        val guessed = intent.getStringExtra(getString(R.string.guessed))
        if(guessed != null){
            for(model in list){
                if(model.getTitles() == guessed){
                    //model.setNames(guessed)
                    val index = list.indexOf(model)
                    list[index].setNames(guessed)
                }
            }
            /*for (i in 0 until  myLyricsList.size) {
                val imageModel = MyModel()
                imageModel.setNames(myImageNameList)
                imageModel.setImage_drawables(myImageList)
                imageModel.setTitle(myTitleList[i])
                imageModel.setLyric(myLyricsList[i])
                imageModel.setArtist(myArtistList[i])
                list.add(imageModel)
            }*/
            var imageModelArrayList: ArrayList<MyModel> = list
            val recyclerView = findViewById<View>(R.id.my_recycler_view) as? RecyclerView
            val layoutManager = LinearLayoutManager(this)
            if (recyclerView != null) {
                recyclerView.layoutManager = layoutManager
            }
            val mAdapter = Adapter(imageModelArrayList)
            if (recyclerView != null) {
                recyclerView.adapter = mAdapter
            }

        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
    }


}
