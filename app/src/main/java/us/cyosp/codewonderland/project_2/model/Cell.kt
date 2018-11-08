package us.cyosp.codewonderland.project_2.model

import android.graphics.Color
import us.cyosp.codewonderland.project_2.R

class Cell {

    private val mLIFESPAN = 10

    private var mAge: Int

    var mAlive: Boolean

    init {
        this.mAge = 0
        this.mAlive = false
    }

    fun swapState() {
        this.mAlive = this.mAlive.not()
    }

    fun setAlive() {
        this.mAlive = true
    }

    fun setDead() {
        this.mAlive = false
    }

    fun age() {
        if (!checkAge()) {
            incAge()
        } else {
            resetCell()
        }
    }

    private fun resetCell() {
        this.mAge = 0
        this.setDead()
    }

    private fun checkAge() = this.mAge >= mLIFESPAN

    private fun incAge() = this.mAge++

}