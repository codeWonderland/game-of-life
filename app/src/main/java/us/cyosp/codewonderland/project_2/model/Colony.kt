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

    private var mCells = ArrayList<ArrayList<Cell>>()

    fun extract(): ArrayList<ArrayList<Cell>> {
        return mCells
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
                if (mCells[i][j].mAlive) {
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

    init {
        for (i in 0 until 20) {
            mCells.add(ArrayList())

            for (j in 0 until 20) {
                val cell = Cell()
                // do init cell code

                mCells[i].add(cell)
            }
        }
    }
}