package us.cyosp.codewonderland.project_2.controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import us.cyosp.codewonderland.project_2.R
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Button
import us.cyosp.codewonderland.project_2.model.*
import java.util.*
import top.defaults.colorpicker.ColorPickerPopup
import java.io.*
import us.cyosp.codewonderland.project_2.util.ColorConverter

class ColonyRecyclerFragment : Fragment() {

    companion object {
        var ALIVE: Int = Color.GREEN
        var DEAD: Int = Color.GRAY

        // the lower the number, the faster the pulse
        // make sure the number divides 100 evenly
        private const val MAX_OPACITY = 5

        // SHOULD ALWAYS STAY 0
        private const val MIN_OPACITY = 0

        // determines if we should be
        // incrementing or decrementing
        // the current opacity
        private var sOpRising = false

        // current opacity in percent
        // divided by 10 for easy updates
        var sOpactiy: Int = MAX_OPACITY

        // Time delay before init //
        var sTimerDelay: Long = 200

        // Time in between each iteration //
        var sTimerPeriod: Long = 200

        private const val READ_REQUEST_CODE: Int = 42
        private const val WRITE_REQUEST_CODE: Int = 43
        private const val DELETE_REQUEST_CODE: Int = 44
    }

    // Running flag for button toggle //
    private var mRunning: Boolean = false

    // Timer for game loop //
    private var mTimer: Timer? = null


    // Width and Height of grid //
    private var mRowCount = 20
    private var mColCount = 20

    // Cell life span //
    private var mLifeSpan = 20

    // Colony of cells //
    // -Grid with given width and height
    // -Given life span for each cell
    private var mColony = Colony(mRowCount, mColCount)

    // Colony grid recycler view //
    private var mColonyRecyclerView: RecyclerView? = null
    // Recycler view adapter //
    private var mAdapter: ColonyAdapter? = null

    // Run Button //
    // -Toggles run and stop for main game loop

    private var mRunButton: Button? = null
    // Reset Button //
    // -Resets simulation
    private var mResetButton: Button? = null

    // Called on creation //
    // -Sets view to have a menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        // get available data from arguments
        val colonyData: String? = arguments?.getString(MainActivity.COLONY_DATA_ID)

        // if we have colony data
        if (colonyData != null) {
            // we decode that information
            mColony.decode(colonyData)
        }
    }

    // Called on creation of RecyclerView //
    // - Initializes objects for RecyclerView
    //      * Recycler View and Adapter
    //      * Run Button
    //      * Reset Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recycler_view_colony, container, false)

        // Initialize RecyclerView //
        mColonyRecyclerView = view
            .findViewById(R.id.colony_recycler_view) as RecyclerView
        mColonyRecyclerView!!.layoutManager = GridLayoutManager(activity, mColCount)

        // Set cell divider horizontally //
        mColonyRecyclerView!!.addItemDecoration(DividerItemDecoration(activity,
            DividerItemDecoration.HORIZONTAL))

        // Set cell divider vertically //
        mColonyRecyclerView!!.addItemDecoration(DividerItemDecoration(activity,
            DividerItemDecoration.VERTICAL))

        // Initialize Run Button //
        mRunButton = view.findViewById(R.id.run_sim_button) as Button

        // Initialize onClick Listener //
        mRunButton!!.setOnClickListener {
            // Flip running state //
            // -Set true if false, false if true
            this.mRunning = this.mRunning.not()

            // Main game loop check //
            if (this.mRunning) {
                // Create a new loop timer //
                mTimer = Timer()
                // Change text of button to reflect state change //
                mRunButton!!.text = activity!!.getText(R.string.stop_sim)
                // Run game loop //
                run()
            } else {
                // Stopping game loop //

                // Change text of button to reflect state change //
                mRunButton!!.text = activity!!.getText(R.string.run_sim)

                // Stop game loop timer //
                mTimer!!.cancel()
            }
        }

        // Initialize Reset Button //
        mResetButton = view.findViewById(R.id.reset_sim) as Button

        // Initialize onClick Listener //
        mResetButton!!.setOnClickListener {
            // Stop game loop if running //
            mTimer?.cancel()

            // Change Run Button text if needed to reflect state //
            if (mRunButton!!.text == activity!!.getText(R.string.stop_sim)) {
                mRunButton!!.text = activity!!.getText(R.string.run_sim)
                // Change to not running if running //
                if (this.mRunning) {
                    this.mRunning = false
                }
            }

            // Reload view //
            fragmentManager!!.beginTransaction().replace(R.id.fragment_container, ColonyRecyclerFragment()).commit()
        }

        // Update UI //
        updateUI()

        return view
    }

    // On create for menu //
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_fragment, menu)
        // Todo: Add on create stuff for menu if needed. Remove TODO if not needed
    }

    // Item select action on menu //
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        // Check which item is selected //
        when (item!!.itemId) {

            R.id.clone_activity -> {
                val intent = MainActivity().newIntent(context!!, mColony)
                activity!!.startActivity(intent)
            }

            // Color Picker Alive option selected //
            R.id.color_picker_alive -> {
                pickColor(ALIVE.toString(), ALIVE)
            }

            // Color Picker Dead option selected //
            R.id.color_picker_dead -> {
                pickColor(DEAD.toString(), DEAD)
            }

            // Save option selected //
            R.id.save -> {
                createFile("patter/file", "Untitled")
            }

            // Load option selected //
            R.id.load -> {
                pickFile()
            }

            // Delete option selected
            R.id.delete -> {
                deleteFile()
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    // Action on Resume of app //
    override fun onResume() {
        super.onResume()

        // Reload UI //
        updateUI()
    }

    // RecyclerView Cell View Class //
    private inner class CellView(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(
            inflater.inflate(
                R.layout.recycler_view_cell,
                parent,
                false
            )
        ),
        View.OnClickListener {

        // Cell for this Recycler cell //
        private var mCell: Cell

        // Constructor //
        init {
            // Create a new default Cell
            mCell = Cell(DEAD)

            // Set Recycler cell to have onClick option //
            itemView.setOnClickListener(this)

            // Update Cell color //
            updateColor()
        }

        // Bind cell to RecyclerView //
        fun bind(cell: Cell) {
            // Set given cell to this views Cell //
            mCell = cell

            // Update cell color //
            updateColor()
        }

        // Create onClickListener for cell //
        override fun onClick(view: View) {
            // Swap state of cell //
            // -Alive if dead, Dead if Alive
            mCell.swap()

            // Update cell color //
            updateColor()
        }

        // Update cell color for this cell //
        fun updateColor() {
            // Update opacity of living cells
            val aliveColor = ColorConverter.convertIntoColor(
                ColonyRecyclerFragment.ALIVE,
                ColonyRecyclerFragment.sOpactiy * (100 / MAX_OPACITY)
            )

            mCell.mColor = if(mCell.mAlive) aliveColor
                            else ColonyRecyclerFragment.DEAD
            // Set cell color //
            itemView.setBackgroundColor(mCell.mColor)
        }
    }

    // RecyclerView Adapter //
    private inner class ColonyAdapter :
        RecyclerView.Adapter<CellView>() {

        // Get number of items in RecyclerView //
        override fun getItemCount(): Int {

            // Width * Height //
            return mRowCount * mColCount
        }

        // Creation initializer for Adapter //
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellView {
            val layoutInflater = LayoutInflater.from(activity)
            return CellView(layoutInflater, parent)
        }

        // Bind colony grid to RecyclerView grid //
        override fun onBindViewHolder(holder: CellView, position: Int) {

            // Get x and y coordinate for cell //
            val y = position / mRowCount
            val x = position % mColCount

            // Bind cell in colony at coordinate to view //
            holder.bind(mColony.extract()[x][y])
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                READ_REQUEST_CODE -> {
                    resultData?.data?.also { uri ->
                        loadDataFromFile(uri)
                    }
                }
                WRITE_REQUEST_CODE -> {
                    resultData?.data?.also {uri ->
                        saveDataToFile(uri)
                    }
                }
                DELETE_REQUEST_CODE -> {
                    resultData?.data?.also { uri ->
                        DocumentsContract.deleteDocument(activity!!.contentResolver, uri)
                    }
                }
            }
        }
    }

    // Run colony life update //
    private fun updateColony(): Boolean {
        if (sOpactiy == MAX_OPACITY) {
            sOpRising = false

        } else if (sOpactiy == MIN_OPACITY) {
            sOpRising = true
        }
        sOpactiy = if (sOpRising) {
            ++sOpactiy

        } else {
            --sOpactiy
        }

        // Get all living neighbors and set next generation for given set //
        return mColony.nextGeneration(mColony.getLivingNeighbors())
    }

    // Updated UI //
    private fun updateUI() {
        // Check if an adapter exists //
        // -Create a new one if non exists
        if (mAdapter == null) {
            this.mAdapter = ColonyAdapter()
            mColonyRecyclerView!!.adapter = mAdapter
        } else {
            // Update UI //
            activity!!.runOnUiThread { mAdapter!!.notifyDataSetChanged() }
        }
    }

    // Run main game loop //
    private fun run() {
        // Start a new timer //
        // -Stats after given mTimerDelay
        // -Delay between iterations are mTimerPeriod
        mTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // Update colony for next genoration //
                // TODO: Handle if all cells die to aging
                val stillAlive = updateColony()

                // Update UI //
                updateUI()
            }
        }, sTimerDelay, sTimerPeriod)
    }

    private fun pickColor(name: String, preColor: Int) {
        ColorPickerPopup.Builder(activity)
            .initialColor(preColor) // Set initial color
            .enableBrightness(true) // Enable brightness slider or not
            .enableAlpha(true) // Enable alpha slider or not
            .okTitle("Choose")
            .cancelTitle("Cancel")
            .showIndicator(true)
            .showValue(true)
            .build()
            .show(object : ColorPickerPopup.ColorPickerObserver {
                override fun onColorPicked(color: Int) {
                    changeColor(name, color)
                }

                override fun onColor(color: Int, fromUser: Boolean) {
                    // TODO: Determine necessity
                }
            })
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)

            type = "*/*"
        }

        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    private fun createFile(mimeType: String, fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            // Filter to only show results that can be "opened", such as
            // a file (as opposed to a list of contacts or timezones).
            addCategory(Intent.CATEGORY_OPENABLE)

            // Create a file with the requested MIME type.
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, fileName)
        }

        startActivityForResult(intent, WRITE_REQUEST_CODE)
    }

    private fun deleteFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)

            type = "*/*"
        }

        startActivityForResult(intent, DELETE_REQUEST_CODE)
    }

    private fun changeColor(name: String, color: Int) {
        when(name) {
            ALIVE.toString() -> ALIVE = color
            DEAD.toString() -> DEAD = color
        }

        mColony.updateColors()
        updateUI()
    }

    private fun saveDataToFile(uri: Uri?) {
        activity!!.contentResolver.openOutputStream(uri!!).use { outputStream ->
            BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                writer.write(mColony.encode())
            }
        }
    }

    private fun loadDataFromFile(uri: Uri?) {
        activity!!.contentResolver.openInputStream(uri!!).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                mColony.decode(reader.readText())
            }
        }

        updateUI()
    }
}
