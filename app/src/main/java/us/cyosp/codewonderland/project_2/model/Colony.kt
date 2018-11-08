package us.cyosp.codewonderland.project_2.model

import android.content.Context
import kotlin.collections.ArrayList

class Colony(context: Context) {
    companion object {
        var sColony: Colony? = null

        operator fun get(context: Context): Colony {
            if (sColony == null) {
                sColony = Colony(context)
            }

            return sColony!!
        }
    }

    private var mCells =  Array(20) { Array(20) { Cell() }}

    fun extract(): Array<Array<Cell>> {
        return this.mCells
    }

    fun getLivingNeighbors(): Array<Array<Int>> {
        val rows = 20
        val columns = 20
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
                if (this.mCells[i][j].getState()) {
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

        return livingNeighborsCount
    }

    fun nextGeneration(livingNeighbors: Array<Array<Int>>) {
        for (i in 0 until 20) {
            for (j in 0 until 20) {
                val count = livingNeighbors[i][j]

                when {
                    count > 3 -> this.mCells[i][j].setDead()
                    count < 2 -> this.mCells[i][j].setDead()
                    count == 3 -> this.mCells[i][j].setAlive()
                }

                this.mCells[i][j].age()
            }
        }
    }
}