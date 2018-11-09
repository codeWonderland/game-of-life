package us.cyosp.codewonderland.project_2.model

import us.cyosp.codewonderland.project_2.controller.ColonyRecyclerFragment

class Cell(var mColor: Int) {

    // Age of cell //
    var mAge = 0

    //private var mCacheBreaker = 0

    // Cell state //
    // -Alive: true
    // -Dead: false
    var mAlive = false

    //fun breakCache() = mCacheBreaker++

    // Increment age of cell //
    fun incAge() {
        this.mAge++
    }

    // Set cell to alive //
    fun alive() {
        this.mAlive = true
    }

    // Set cell to dead //
    fun dead() {
        this.mAlive = false
    }

    // Swap current state //
    // -Alive if dead, Dead if Alive
    fun swap() {
        this.mAlive = this.mAlive.not()
    }
}