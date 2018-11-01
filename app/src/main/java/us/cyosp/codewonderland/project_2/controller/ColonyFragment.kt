package us.cyosp.codewonderland.project_2.controller

import android.graphics.Color
import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.graphics.drawable.ClipDrawable.VERTICAL
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.cyosp.codewonderland.project_2.R
import android.support.v7.widget.RecyclerView
import android.util.Log
import us.cyosp.codewonderland.project_2.model.*

class ColonyFragment : Fragment() {

    companion object {
        var ALIVE: Int = Color.GREEN
        var DEAD: Int = Color.DKGRAY
    }

    private var mColonyRecyclerView: RecyclerView? = null
    private var mAdapter: ColonyAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_colony, container, false)

        mColonyRecyclerView = view
            .findViewById(R.id.colony_recycler_view) as RecyclerView
        mColonyRecyclerView!!.layoutManager = GridLayoutManager(activity, 20)

        mColonyRecyclerView!!.addItemDecoration(DividerItemDecoration(activity,
            DividerItemDecoration.HORIZONTAL))

        mColonyRecyclerView!!.addItemDecoration(DividerItemDecoration(activity,
            DividerItemDecoration.VERTICAL))

        updateUI()

        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateColony() {

    }

    private fun updateUI() {
        val colony = Colony[activity!!]
        val cells = colony.extract()

        if (mAdapter == null) {
            mAdapter = ColonyAdapter(cells)
            mColonyRecyclerView!!.adapter = mAdapter
        } else {
            mAdapter!!.notifyDataSetChanged()
        }
    }

    private inner class CellView(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(
            inflater.inflate(
                R.layout.fragment_cell,
                parent,
                false
            )
        ),
        View.OnClickListener {

        private var mCell: Cell? = null

        init {
            itemView.setOnClickListener(this)
            itemView.setBackgroundColor(ColonyFragment.DEAD)
            // determine fields
        }

        fun bind(cell: Cell) {
            mCell = cell
            // determine color stuffs
        }

        override fun onClick(view: View) {
            mCell!!.swapState()

            if (mCell!!.mAlive) {
                itemView.setBackgroundColor(ColonyFragment.ALIVE)
            } else {
                itemView.setBackgroundColor(ColonyFragment.DEAD)
            }
        }
    }

    private inner class ColonyAdapter(private val mCells: ArrayList<ArrayList<Cell>>) :
        RecyclerView.Adapter<CellView>() {

        override fun getItemCount(): Int {
            return 400
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellView {
            val layoutInflater = LayoutInflater.from(activity)
            return CellView(layoutInflater, parent)
        }

        override fun onBindViewHolder(holder: CellView, position: Int) {
            val cell = mCells[position / 20][position % 20]
            holder.bind(cell)
        }
    }
}
