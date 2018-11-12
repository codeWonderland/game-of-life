package us.cyosp.codewonderland.project_2.model

import android.graphics.Color
import com.google.gson.Gson

class Colony {

    companion object {
        var sAliveColor: Int = Color.GREEN
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

        // Width and Height of grid //
        var sRowCount = 20
        var sColCount = 20
    }

    // Grid of cells //
    // -Initialized with given data //
    private var mCells =  Array(sRowCount) { Array(sColCount) { Cell(sDeadColor) }}

    // Get grid of cells //
    fun extract(): Array<Array<Cell>> {
        return this.mCells
    }

    fun updateColors() {
        for (i in 0 until sRowCount) {
            for (j in 0 until sColCount) {
                mCells[i][j].mColor =
                        if (mCells[i][j].mAlive) sAliveColor
                        else sDeadColor
            }
        }
    }

    // Get grid of living neighbors for colony //
    // TODO: Stretch Goal - Living neighbors function
    fun getLivingNeighbors(): Array<Array<Int>> {

        // Initialize grid for living neighbors //
        // -Default set to zero
        val livingNeighborsCount = Array(sRowCount) { Array(sColCount) { 0 } }

        // Counts the number of neighbors a cell has and stores it in the array
        for (i in 0 until sRowCount) {
            for (j in 0 until sColCount) {

                // Variables to save positions left and right of row and column
                val leftOfRow = i + sRowCount - 1
                val rightOfRow = i + 1
                val leftOfColumn = j + sColCount - 1
                val rightOfColumn = j + 1

                // Checks to see if the cells are alive or dead. If they are alive
                // it increments the count for living neighbors.
                if (this.mCells[i][j].mAlive) {
                    livingNeighborsCount[leftOfRow % sRowCount][leftOfColumn % sColCount]++
                    livingNeighborsCount[leftOfRow % sRowCount][j % sColCount]++
                    livingNeighborsCount[(i + sRowCount - 1) % sRowCount][rightOfColumn % sColCount]++
                    livingNeighborsCount[i % sRowCount][leftOfColumn % sColCount]++
                    livingNeighborsCount[i % sRowCount][rightOfColumn % sColCount]++
                    livingNeighborsCount[rightOfRow % sRowCount][leftOfColumn % sColCount]++
                    livingNeighborsCount[rightOfRow % sRowCount][j % sColCount]++
                    livingNeighborsCount[rightOfRow % sRowCount][rightOfColumn % sColCount]++
                }
            }
        }

        // Return grid of living neighbors count //
        return livingNeighborsCount
    }

    // Run simulation for next generation for given grid of living neighbors //
    fun nextGeneration(livingNeighbors: Array<Array<Int>>): Boolean {
        for (i in 0 until sRowCount) {
            for (j in 0 until sColCount) {
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

                // TODO: Turn this on after testing
                // lifespan code works, but doesn't help to test
                // increment age of cell //
                //this.mCells[i][j].age()
            }
        }

        // Return if any cells are still alive //
        // TODO: Implement check for if cells are still alive
        // -This is to help make death due to age clearer
        return true
    }

    fun encode(): String {
        val data = Array(sRowCount) { Array(sColCount) {false}}
            for(i in 0 until sRowCount) {
                for (j in 0 until sColCount) {
                    data[i][j] = this.mCells[i][j].mAlive
            }
        }

        return Gson().toJson(data)
    }

    fun decode(data: String) {
        val dataMap = Gson().fromJson(data, Array<Array<Boolean>>::class.java)

        if (dataMap.size != sRowCount) {
            sRowCount = dataMap.size
        }

        if (dataMap[0].size != sColCount) {
            sColCount = dataMap[0].size
        }

        for (i in 0 until sRowCount) {
            for (j in 0 until sColCount) {
                this.mCells[i][j].mAlive = dataMap[i][j]
            }
        }
    }
}