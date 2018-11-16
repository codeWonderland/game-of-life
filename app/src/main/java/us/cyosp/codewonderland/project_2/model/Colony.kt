package us.cyosp.codewonderland.project_2.model

/*
    Project Creators:
    Dylan Blanchard, Alice Easter

    Project-2: The Game of Life
 */

import android.graphics.Color
import com.google.gson.Gson

class Colony(width: Int, height: Int) {

    companion object {
        // Color for alive cells //
        // - Default Green
        var sAliveColor: Int = Color.GREEN

        // Color for dead cells //
        // - Default Gray
        var sDeadColor: Int = Color.GRAY

        // the lower the number, the faster the pulse
        // make sure the number divides 100 evenly
        const val MAX_OPACITY = 5

        // SHOULD ALWAYS STAY 0
        const val MIN_OPACITY = 0

        // determines if we should be
        // incrementing or decrementing
        // the current opacity
        var sOpRising = false

        // current opacity in percent
        // divided by 10 for easy updates
        var sOpacity: Int = MAX_OPACITY

        // Cell life span //
        // - Change this to make cells live longer
        // - Currently living 100 generations
        private var sLifeSpan = 100

    }
    var mHeight = height
    var mWidth = width

    // Grid of cells //
    // -Initialized with given data //
    private var mCells =  Array(mHeight) { Array(mWidth) { Cell(sDeadColor) }}

    // Get grid of cells //
    fun extract(): Array<Array<Cell>> {
        return this.mCells
    }

    // Update cell color for each cell //
    fun updateColors() {
        for (i in 0 until mHeight) {
            for (j in 0 until mWidth) {
                // Set color for cell state //
                // - Alive color if alive
                // - Dead color if dead
                mCells[i][j].mColor =
                        if (mCells[i][j].mAlive) sAliveColor
                        else sDeadColor
            }
        }
    }

    // Get grid of living neighbors for colony //
    fun getLivingNeighbors(): Array<Array<Int>> {

        // Initialize grid for living neighbors //
        // -Default set to zero
        val livingNeighborsCount = Array(mHeight) { Array(mWidth) { 0 } }

        // Counts the number of neighbors a cell has and stores it in the array
        for (i in 0 until mHeight) {
            for (j in 0 until mWidth) {

                // Variables to save positions left and right of row and column
                val leftOfRow = i + mHeight - 1
                val rightOfRow = i + 1
                val leftOfColumn = j + mWidth - 1
                val rightOfColumn = j + 1

                // Checks to see if the cells are alive or dead. If they are alive
                // it increments the count for living neighbors.
                if (this.mCells[i][j].mAlive) {
                    livingNeighborsCount[leftOfRow % mHeight][leftOfColumn % mWidth]++
                    livingNeighborsCount[leftOfRow % mHeight][j % mWidth]++
                    livingNeighborsCount[(i + mHeight - 1) % mHeight][rightOfColumn % mWidth]++
                    livingNeighborsCount[i % mHeight][leftOfColumn % mWidth]++
                    livingNeighborsCount[i % mHeight][rightOfColumn % mWidth]++
                    livingNeighborsCount[rightOfRow % mHeight][leftOfColumn % mWidth]++
                    livingNeighborsCount[rightOfRow % mHeight][j % mWidth]++
                    livingNeighborsCount[rightOfRow % mHeight][rightOfColumn % mWidth]++
                }
            }
        }

        // Return grid of living neighbors count //
        return livingNeighborsCount
    }

    // Run simulation for next generation for given grid of living neighbors //
    fun nextGeneration(livingNeighbors: Array<Array<Int>>): Boolean {
        for (i in 0 until mHeight) {
            for (j in 0 until mWidth) {
                // Get count of neighbors for cell //
                val count = livingNeighbors[i][j]

                when {
                    // Neighbors over 3 //
                    // -Cell dies
                    count > 3 -> this.mCells[i][j].mAlive = false

                    // Neighbors under 2 //
                    // -Cell dies
                    count < 2 -> this.mCells[i][j].mAlive = false

                    // Neighbors is 3 //
                    // -Cell created
                    count == 3 -> this.mCells[i][j].mAlive = true

                    // Neighbors is 2 //
                    // -Ignored
                }

                if (mCells[i][j].mAge >= sLifeSpan) {
                    mCells[i][j].mAlive = false
                } else {
                    mCells[i][j].mAge++
                }
            }
        }

        // Check if any cells are still alive //
        for (i in 0 until mHeight) {
            for (j in 0 until mWidth) {
                // Check if cell is alive //
                if (mCells[i][j].mAlive) {
                    // Cells are still alive //
                    return true
                }
            }
        }
        // No cells are alive //
        // - Reset sim
        return false
    }

    // Encode colony data to JSON //
    // - Returns JSON string
    fun encode(): String {
        // Crate temp array for cell state //
        val data = Array(mHeight) { Array(mWidth) {false}}

        for(i in 0 until mHeight) {
            for (j in 0 until mWidth) {
                // Set cell state to temp array //
                data[i][j] = this.mCells[i][j].mAlive
            }
        }

        // Convert array of Boolean states to JSON //
        // - Returns JSON string
        return Gson().toJson(data)
    }

    // Decode JSON string to colony data //
    fun decode(data: String) {
        // Decode JSON string to Boolean Grid //
        val dataMap = Gson().fromJson(data, Array<Array<Boolean>>::class.java)

        // Check array size to see if grid size matches //
        // Note: This is not needed as width and height change code does not work
        if (dataMap.size != mHeight) {
            mHeight = dataMap.size
        }

        // Check array width if grid size matches //
        // Note: This is not needed as width and height change code does not work
        if (dataMap[0].size != mWidth) {
            mWidth = dataMap[0].size
        }

        // Set cell state to given state from JSON string //
        for (i in 0 until mHeight) {
            for (j in 0 until mWidth) {
                this.mCells[i][j].mAlive = dataMap[i][j]
            }
        }
    }
}