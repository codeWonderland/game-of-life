package us.cyosp.codewonderland.project_2.model

import android.graphics.Color

class Cell() {

    private val mLIFESPAN = 10

    private var mAge: Int
    private var mAlive: Boolean
    private var mColor: Int

    // TODO: Use color some how ???
    private val mAliveColor = Color.GREEN
    private val mDeadColor = Color.GRAY

    init {
        this.mAge = 0
        this.mAlive = false
        this.mColor = mDeadColor
    }

    fun swapState() {
        this.mAlive = this.mAlive.not()
        if(this.mAlive) {
            this.mColor = mAliveColor
        } else {
            this.mColor = mDeadColor
        }
    }

    fun setAlive() {
        this.mAlive = true
        this.mColor = mAliveColor
    }

    fun setDead() {
        this.mAlive = false
        this.mColor = mDeadColor
    }

    fun getColor(): Int {
        return this.mColor
    }

    fun getState(): Boolean {
        return this.mAlive
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