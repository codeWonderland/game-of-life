package us.cyosp.codewonderland.project_2.model

class Cell {
    var mAlive: Boolean = false

    fun swapState() {
        mAlive = mAlive.not()
    }
}