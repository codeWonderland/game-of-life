package us.cyosp.codewonderland.project_2.model

class Cell(private val mLifeSpan: Int) {

    // Age of cell //
    private var mAge = 0

    private var mCacheBreaker = 0

    // Cell state //
    // -Alive: true
    // -Dead: false
    private var mAlive = false

    fun breakCache() = mCacheBreaker++

    // Get age of cell //
    fun age(): Int {
        return this.mAge
    }

    // Increment age of cell //
    fun incAge() {
        this.mAge++
    }

    // Get cell state //
    fun state(): Boolean {
        return this.mAlive
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

// Get cell age //
fun Cell.getAge(): Int {
    return this.age()
}

// Check cell state //
fun Cell.isAlive(): Boolean {
    return this.state()
}

// Increment cell age //
// TODO: Should probably rename this...
fun Cell.age() {
    this.incAge()
}

// Swap cell state //
fun Cell.swapState() {
    this.swap()
}

// Set cell to alive //
fun Cell.setAlive() {
    this.alive()
}

// Set cell to dead //
fun Cell.setDead() {
    this.dead()
}