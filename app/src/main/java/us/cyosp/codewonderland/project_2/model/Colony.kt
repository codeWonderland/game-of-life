package us.cyosp.codewonderland.project_2.model

import android.content.Context
import us.cyosp.codewonderland.project_2.R
import kotlin.collections.ArrayList

class Colony(width: Int, height: Int, lifeSpan: Int) {

    // Grid of cells //
    // -Initialized with given data //
    private var mCells =  Array(width) { Array(height) { Cell(lifeSpan) }}

    // Get grid of cells //
    fun extract(): Array<Array<Cell>> {
        return this.mCells
    }

    // Get grid of living neighbors for colony //
    // TODO: Stretch Goal - Living neighbors function
    fun getLivingNeighbors(): Array<Array<Int>> {
        // Get grid width and height //
        val rows = mCells.size
        val columns = mCells[0].size

        // Initialize grid for living neighbors //
        // -Default set to zero
        val livingNeighborsCount = Array(rows) { Array(columns) { 0 } }

        // Counts the number of neighbors a cell has and stores it in the array
        for (i in 0 until rows) {
            for (j in 0 until columns) {

                // Variables to save positions left and right of row and column
                val leftOfRow = i + rows - 1
                val rightOfRow = i + 1
                val leftOfColumn = j + columns - 1
                val rightOfColumn = j + 1

                // Checks to see if the cells are alive or dead. If they are alive
                // it increments the count for living neighbors.
                if (this.mCells[i][j].isAlive()) {
                    livingNeighborsCount[leftOfRow % rows][leftOfColumn % columns]++
                    livingNeighborsCount[leftOfRow % rows][j % columns]++
                    livingNeighborsCount[(i + rows - 1) % rows][rightOfColumn % columns]++
                    livingNeighborsCount[i % rows][leftOfColumn % columns]++
                    livingNeighborsCount[i % rows][rightOfColumn % columns]++
                    livingNeighborsCount[rightOfRow % rows][leftOfColumn % columns]++
                    livingNeighborsCount[rightOfRow % rows][j % columns]++
                    livingNeighborsCount[rightOfRow % rows][rightOfColumn % columns]++
                }
            }
        }

        // Return grid of living neighbors count //
        return livingNeighborsCount
    }

    // Run simulation for next generation for given grid of living neighbors //
    fun nextGeneration(livingNeighbors: Array<Array<Int>>): Boolean {
        for (i in 0 until 20) {
            for (j in 0 until 20) {
                // Get count of neighbors for cell //
                val count = livingNeighbors[i][j]
                when {
                    // Neighbors over 3 //
                    // -Cell dies
                    count > 3 -> this.mCells[i][j].setDead()

                    // Neighbors under 2 //
                    // -Cell dies
                    count < 2 -> this.mCells[i][j].setDead()

                    // Neighbors is 3 //
                    // -Cell created
                    count == 3 -> this.mCells[i][j].setAlive()

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