package us.cyosp.codewonderland.project_2.controller

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
        var ALIVE: Int = Color.GREEN
        var DEAD: Int = Color.GRAY
    }

    private var mRowCount = 20
    private var mColCount = 20
    private var mLifeSpan = 20

    private var mColonyRecyclerView: RecyclerView? = null
    private var mAdapter: ColonyAdapter? = null

    private var mRunButton: Button? = null
    private var mResetButton: Button? = null

    private var mColony = Colony(mRowCount, mColCount, mLifeSpan)

    private var mRunning: Boolean = false

    private var mTimer: Timer? = null

    private val mTimerDelay: Long = 200
    private val mTimerPeriod: Long = 200

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

        mRunButton = view.findViewById(R.id.run_sim_button) as Button
        mRunButton!!.setOnClickListener {
            this.mRunning = this.mRunning.not()

            if (this.mRunning) {
                mTimer = Timer()
                mRunButton!!.text = activity!!.getText(R.string.stop_sim)
                run()
            } else {
                mRunButton!!.text = activity!!.getText(R.string.run_sim)
                mTimer!!.cancel()
            }
        }

        mResetButton = view.findViewById(R.id.reset_sim) as Button
        mResetButton!!.setOnClickListener {
            mTimer?.cancel()
            if (mRunButton!!.text == activity!!.getText(R.string.stop_sim)) {
                mRunButton!!.text = activity!!.getText(R.string.run_sim)
                if (this.mRunning) {
                    this.mRunning = false
                }
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

                updateUI()
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

    private fun run() {
        mTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateColony()
                activity!!.runOnUiThread { updateUI() }
            }
        }, mTimerDelay, mTimerPeriod)


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

        private var mCell: Cell

        init {
            mCell = Cell(mLifeSpan)
            itemView.setOnClickListener(this)

            updateColor()
        }

        fun bind(cell: Cell) {
            mCell = cell

            updateColor()
        }

        override fun onClick(view: View) {
            mCell.swapState()

            updateColor()
        }

        fun updateColor() {
            val color = if (mCell.isAlive()) {
                ALIVE
            } else {
                DEAD
            }

            itemView.setBackgroundColor(color)
        }
    }

    private inner class ColonyAdapter :
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
            holder.bind(mColony.extract()[x][y])
        }
    }
}
