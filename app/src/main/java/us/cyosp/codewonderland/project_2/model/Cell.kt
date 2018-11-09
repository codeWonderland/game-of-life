package us.cyosp.codewonderland.project_2.model

import android.graphics.Color
import us.cyosp.codewonderland.project_2.R

data class Cell(val lifeSpan: Int) {
    private val mLIFESPAN = lifeSpan

    private var mAge = 0
    private var mAlive = false

    fun age(): Int {
        return this.mAge
    }

    fun incAge() {
        this.mAge++
    }

    fun state(): Boolean {
        return this.mAlive
    }

    fun alive() {
        this.mAlive = true
    }

    fun dead() {
        this.mAlive = false
    }

    fun swap() {
        this.mAlive = this.mAlive.not()
    }

  /*  init {
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

    private fun incAge() = this.mAge++*/

}

fun Cell.getAge(): Int {
    return this.age()
}

fun Cell.isAlive(): Boolean {
    return this.state()
}

fun Cell.age() {
    this.incAge()
}

fun Cell.swapState() {
    this.swap()
}

fun Cell.setAlive() {
    this.alive()
}

fun Cell.setDead() {
    this.dead()
}