package us.cyosp.codewonderland.project_2.model

/*
    Project Creators:
    Dylan Blanchard, Alice Easter

    Project-2: The Game of Life
 */

class Cell(var mColor: Int) {

    // Age of cell //
    var mAge = 0

    // Cell state //
    // -Alive: true
    // -Dead: false
    var mAlive = false

    // Swap current state //
    // -Alive if dead, Dead if Alive
    fun swap() {
        this.mAlive = this.mAlive.not()
    }
}