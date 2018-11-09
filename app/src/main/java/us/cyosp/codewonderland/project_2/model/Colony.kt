package us.cyosp.codewonderland.project_2.model

import us.cyosp.codewonderland.project_2.controller.ColonyRecyclerFragment

class Colony(private val mRows: Int, private val mColumns: Int) {

    // Grid of cells //
    // -Initialized with given data //
    private var mCells =  Array(mRows) { Array(mColumns) { Cell(ColonyRecyclerFragment.DEAD) }}

    // Get grid of cells //
    fun extract(): Array<Array<Cell>> {
        return this.mCells
    }

    fun updateColors() {
        for (i in 0 until mRows) {
            for (j in 0 until mColumns) {
                mCells[i][j].mColor =
                        if (mCells[i][j].mAlive) ColonyRecyclerFragment.ALIVE
                        else ColonyRecyclerFragment.DEAD
            }
        }
    }

    // Get grid of living neighbors for colony //
    // TODO: Stretch Goal - Living neighbors function
    fun getLivingNeighbors(): Array<Array<Int>> {

        // Initialize grid for living neighbors //
        // -Default set to zero
        val livingNeighborsCount = Array(mRows) { Array(mColumns) { 0 } }

        // Counts the number of neighbors a cell has and stores it in the array
        for (i in 0 until mRows) {
            for (j in 0 until mColumns) {

                // Variables to save positions left and right of row and column
                val leftOfRow = i + mRows - 1
                val rightOfRow = i + 1
                val leftOfColumn = j + mColumns - 1
                val rightOfColumn = j + 1

                // Checks to see if the cells are alive or dead. If they are alive
                // it increments the count for living neighbors.
                if (this.mCells[i][j].mAlive) {
                    livingNeighborsCount[leftOfRow % mRows][leftOfColumn % mColumns]++
                    livingNeighborsCount[leftOfRow % mRows][j % mColumns]++
                    livingNeighborsCount[(i + mRows - 1) % mRows][rightOfColumn % mColumns]++
                    livingNeighborsCount[i % mRows][leftOfColumn % mColumns]++
                    livingNeighborsCount[i % mRows][rightOfColumn % mColumns]++
                    livingNeighborsCount[rightOfRow % mRows][leftOfColumn % mColumns]++
                    livingNeighborsCount[rightOfRow % mRows][j % mColumns]++
                    livingNeighborsCount[rightOfRow % mRows][rightOfColumn % mColumns]++
                }
            }
        }

        // Return grid of living neighbors count //
        return livingNeighborsCount
    }

    // Run simulation for next generation for given grid of living neighbors //
    fun nextGeneration(livingNeighbors: Array<Array<Int>>): Boolean {
        for (i in 0 until mRows) {
            for (j in 0 until mColumns) {
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
}