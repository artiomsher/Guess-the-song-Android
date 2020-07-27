package com.example.guessthesong

class MyModel {
    private var name: String? = null
    private var image_drawable: Int = 0
    private var title: String? = null
    private var lyric: String? = null
    private var artist: String? = null

    fun getNames(): String {
        return name.toString()
    }

    fun setNames(name: String) {
        this.name = name
    }

    fun getImage_drawables(): Int {
        return image_drawable
    }
    fun setImage_drawables(image_drawable: Int) {
        this.image_drawable = image_drawable
    }
    fun setTitle(title: String) {
        this.title = title
    }
    fun getTitles(): String {
        return title.toString()
    }
    fun setLyric(lyric: String) {
        this.lyric = lyric
    }
    fun getLyrics(): String {
        return lyric.toString()
    }
    fun setArtist(artist: String) {
        this.artist = artist
    }
    fun getArtists(): String {
        return artist.toString()
    }
}