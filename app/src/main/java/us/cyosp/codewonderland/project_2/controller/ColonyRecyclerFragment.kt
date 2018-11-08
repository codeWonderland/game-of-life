package us.cyosp.codewonderland.project_2.controller

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import us.cyosp.codewonderland.project_2.R
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Button
import us.cyosp.codewonderland.project_2.model.*
import java.util.*

class ColonyRecyclerFragment : Fragment() {

    companion object {
        var ALIVE: Int = R.color.alive_color
        var DEAD: Int = R.color.dead_color
    }

    private val mRowCount = 20
    private val mColCount = 20

    private var mColonyRecyclerView: RecyclerView? = null
    private var mAdapter: ColonyAdapter? = null

    private var mRunButton: Button? = null
    private var mResetButton: Button? = null

    private var mColony = Colony()

    private var mRunning: Boolean = false

    private var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recycler_view_colony, container, false)

        mColonyRecyclerView = view
            .findViewById(R.id.colony_recycler_view) as RecyclerView
        mColonyRecyclerView!!.layoutManager = GridLayoutManager(activity, 20)

        mColonyRecyclerView!!.addItemDecoration(DividerItemDecoration(activity,
            DividerItemDecoration.HORIZONTAL))

        mColonyRecyclerView!!.addItemDecoration(DividerItemDecoration(activity,
            DividerItemDecoration.VERTICAL))

        fun run() {
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    updateColony()
                    activity!!.runOnUiThread { updateUI() }
                }
            }, 250, 250)


        }


        mRunButton = view.findViewById(R.id.run_sim_button) as Button
        mRunButton!!.setOnClickListener {
            this.mRunning = this.mRunning.not()

            if (this.mRunning) {
                timer = Timer()
                mRunButton!!.text = activity!!.getText(R.string.stop_sim)
                run()
            } else {
                mRunButton!!.text = activity!!.getText(R.string.run_sim)
                timer.cancel()
            }
        }

        mResetButton = view.findViewById(R.id.reset_sim) as Button
        mResetButton!!.setOnClickListener {
            timer.cancel()
            if (mRunButton!!.text == activity!!.getText(R.string.stop_sim)) {
                mRunButton!!.text = activity!!.getText(R.string.run_sim)
            }
            fragmentManager!!.beginTransaction().replace(R.id.fragment_container, ColonyRecyclerFragment()).commit()
        }

        updateUI()

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_fragment, menu)
        // Todo: Add on create stuff for menu if needed. Remove TODO if not needed
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.color_picker -> {
                // TODO: Add color picker code here
            }
            R.id.save -> {
                // TODO: Add save pattern code here
            }
            R.id.load -> {
                // TODO: Add load pattern code here
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateColony() {
        mColony.nextGeneration(mColony.getLivingNeighbors())
    }

    private fun updateUI() {
        if (mAdapter == null) {
            this.mAdapter = ColonyAdapter()
            mColonyRecyclerView!!.adapter = mAdapter
        } else {
            activity!!.runOnUiThread { mAdapter!!.notifyDataSetChanged() }
        }
    }

    private inner class CellView(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(
            inflater.inflate(
                R.layout.recycler_view_cell,
                parent,
                false
            )
        ),
        View.OnClickListener {

        private var mCell: Cell? = null

        init {
            itemView.setOnClickListener(this)
            itemView.setBackgroundColor(ColonyRecyclerFragment.DEAD)
            // TODO: determine fields
        }

        fun bind(cell: Cell) {
            mCell = cell
            itemView.setBackgroundColor(mCell!!.getColor())
        }

        override fun onClick(view: View) {
            mCell!!.swapState()
            itemView.setBackgroundColor(mCell!!.getColor())
        }
    }

    private inner class ColonyAdapter() :
        RecyclerView.Adapter<CellView>() {

        override fun getItemCount(): Int {
            return mRowCount * mColCount
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellView {
            val layoutInflater = LayoutInflater.from(activity)
            return CellView(layoutInflater, parent)
        }

        override fun onBindViewHolder(holder: CellView, position: Int) {
            val y = position / mRowCount
            val x = position % mColCount

            val cell = mColony.extract()[x][y]
            holder.bind(cell)
        }
    }
}
