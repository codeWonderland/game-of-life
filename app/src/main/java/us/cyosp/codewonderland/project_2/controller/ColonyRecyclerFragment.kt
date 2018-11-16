package us.cyosp.codewonderland.project_2.controller

/*
    Project Creators:
    Dylan Blanchard, Alice Easter

    Project-2: The Game of Life
 */

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import us.cyosp.codewonderland.project_2.R
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Button
import android.widget.SeekBar
import us.cyosp.codewonderland.project_2.model.*
import java.util.*
import top.defaults.colorpicker.ColorPickerPopup
import java.io.*
import us.cyosp.codewonderland.project_2.util.ColorConverter
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


class ColonyRecyclerFragment : Fragment() {

    companion object {
        // Time delay before init //
        var sTimerDelay: Long = 100

        // Time in between each iteration //
        var sTimerPeriod: Long = 200

        // Static IDs for menu actions //
        private const val READ_REQUEST_CODE: Int = 42
        private const val WRITE_REQUEST_CODE: Int = 43
        private const val DELETE_REQUEST_CODE: Int = 44

        private const val FILE_TYPE: String = "pattern/file"
    }

    // Running flag for button toggle //
    private var mRunning: Boolean = false

    // Timer for game loop //
    private var mTimer: Timer? = null

    // Colony of cells //
    // -Grid with given width and height
    // -Given life span for each cell
    private var mColony: Colony? = null

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

    // Speed Slider //
    // - Change sim rate speed
    private var mSlider: SeekBar? = null

    // Edit grid width text input //
    // NOTE: This currently does not work.
    //    code left in to show what we were trying to do
    //    but we could not get activity to recreate it self.
    private var mWidthTextField: EditText? = null
    private var mHeightTextField: EditText? = null

    // Called on creation //
    // -Sets view to have a menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        // get available data from  //
        // - Colony data as JSON string
        // - grid height
        // - grid width
        val colonyData: String? = arguments?.getString(MainActivity.COLONY_DATA_ID)
        val colonyHeight: Int? = arguments?.getInt(MainActivity.COLONY_HEIGHT_ID)
        val colonyWidth: Int? = arguments?.getInt(MainActivity.COLONY_WIDTH_ID)

        // Set grid width and height //
        if (colonyHeight == null || colonyWidth == null) {
            // Create colony width and height default 20x20 //
            mColony = Colony(20, 20)
        } else {
            // Create colony width and height as given //
            mColony = Colony(colonyWidth, colonyHeight)
        }

        // if we have colony data //
        if (colonyData != null) {
            // we decode that information //
            mColony!!.decode(colonyData)
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
        mColonyRecyclerView!!.layoutManager = GridLayoutManager(activity, mColony!!.mWidth)

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

        // Initialize Speed Slider //
        mSlider = view.findViewById(R.id.slider_sim_rate) as SeekBar

        // Set slider to current speed //
        // - Speed between 1s - 10s
        // - Takes slider max + 1 and subtracts by current time period converted to seconds from milliseconds
        // - Max + 1 is to prevent the speed being set to 0 if time is set to max of slider (10s / 10,000ms)
        mSlider!!.progress = ((mSlider!!.max + 1) - (sTimerPeriod.toInt() / 100))

        // Set onChangeListener for slider //
        mSlider!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            // Current time //
            // - Set to current time period
            var period = sTimerPeriod

            // On change of slider //
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Change time period to new period selected //
                // - Takes max of slider + 1 and subtracts current progress (1-10) then converts to ms and to long
                // - max + 1 to prevent data from going to 0
                // - subtracting progress to flip value as the slider view shows slow to fast
                //      but slider progress would make it fast to slow.
                period = (((mSlider!!.max + 1) - progress) * 100).toLong()
            }

            // Required overload for slider action //
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            // Set current time delay to new time delay on release of click //
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Change timer delay to new delay //
                sTimerPeriod = period

                // Check if sim is running //
                if (mRunning) {
                    // Stop timer to change delay //
                    mTimer?.cancel()

                    // Create new timer for new delay //
                    mTimer = Timer()

                    // Restart sim //
                    run()
                }
            }
        })

        /*
            This section of code is only left in for example of what we were trying to do.
            We could not get the change of width and height to work.
         */
        mWidthTextField = view.findViewById(R.id.edit_text_width)

        mWidthTextField!!.hint = mColony!!.mWidth.toString()

        mWidthTextField!!.addTextChangedListener(object : TextWatcher {
            var width: Int = 0
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                width = mColony!!.mWidth
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                width = s.toString().toInt()
            }

            override fun afterTextChanged(s: Editable) {
                val intent = MainActivity().newIntent(context!!, Colony(width, mColony!!.mHeight))
                activity!!.startActivity(intent)
            }
        })

        mHeightTextField = view.findViewById(R.id.edit_text_height)

        mHeightTextField!!.hint = mColony!!.mHeight.toString()

        mHeightTextField!!.addTextChangedListener(object : TextWatcher {
            var height: Int = 0
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                height = mColony!!.mHeight
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                height = s.toString().toInt()
            }

            override fun afterTextChanged(s: Editable) {
                val intent = MainActivity().newIntent(context!!, Colony(mColony!!.mWidth, height))
                activity!!.startActivity(intent)
            }
        })

            // Update UI //
            updateUI()

            return view
        }

    // On create for menu //
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_fragment, menu)
    }

    // Item select action on menu //
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        // Check which item is selected //
        when (item!!.itemId) {

            R.id.clone_activity -> {
                val intent = MainActivity().newIntent(context!!, mColony!!)
                activity!!.startActivity(intent)
            }

            // Color Picker Alive option selected //
            R.id.color_picker_alive -> {
                pickColor(Colony.sAliveColor.toString(), Colony.sAliveColor)
            }

            // Color Picker Dead option selected //
            R.id.color_picker_dead -> {
                pickColor(Colony.sDeadColor.toString(), Colony.sDeadColor)
            }

            // Save option selected //
            R.id.save -> {
                createFile(FILE_TYPE, "Untitled")
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
            mCell = Cell(Colony.sDeadColor)

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
                Colony.sAliveColor,
                Colony.sOpacity * (100 / Colony.MAX_OPACITY)
            )

            mCell.mColor = if(mCell.mAlive) aliveColor
                            else Colony.sDeadColor
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
            return mColony!!.mHeight * mColony!!.mWidth
        }

        // Creation initializer for Adapter //
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellView {
            val layoutInflater = LayoutInflater.from(activity)
            return CellView(layoutInflater, parent)
        }

        // Bind colony grid to RecyclerView grid //
        override fun onBindViewHolder(holder: CellView, position: Int) {

            // Get x and y coordinate for cell //
            val y = position / mColony!!.mHeight
            val x = position % mColony!!.mWidth

            // Bind cell in colony at coordinate to view //
            holder.bind(mColony!!.extract()[x][y])
        }
    }

    // On result from menu //
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        // Check result is ok //
        if (resultCode == Activity.RESULT_OK) {
            // Check which menu item was selected //
            when(requestCode) {
                // Read pattern from given file //
                READ_REQUEST_CODE -> {
                    // Get data from URI //
                    resultData?.data?.also { uri ->
                        // Load data from file //
                        loadDataFromFile(uri)
                    }
                }
                // Write patter to file //
                WRITE_REQUEST_CODE -> {
                    // Get file from URI //
                    resultData?.data?.also {uri ->
                        // Save data to file //
                        saveDataToFile(uri)
                    }
                }
                // Delete given file //
                DELETE_REQUEST_CODE -> {
                    // Get file from URI //
                    resultData?.data?.also { uri ->
                        // Delete file //
                        DocumentsContract.deleteDocument(activity!!.contentResolver, uri)
                    }
                }
            }
        }
    }

    // Run colony life update //
    private fun updateColony(): Boolean {
        if (Colony.sOpacity == Colony.MAX_OPACITY) {
            Colony.sOpRising = false

        } else if (Colony.sOpacity == Colony.MIN_OPACITY) {
            Colony.sOpRising = true
        }
        Colony.sOpacity = if (Colony.sOpRising) {
            ++Colony.sOpacity

        } else {
            --Colony.sOpacity
        }

        // Get all living neighbors and set next generation for given set //
        return mColony!!.nextGeneration(mColony!!.getLivingNeighbors())
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
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                // Update colony for next generation //
                // - gets if any cells are still alive
                val stillAlive = updateColony()

                // Check if any cells are still alive //
                if (!stillAlive) {
                    // Stop game loop if running //
                    mTimer?.cancel()

                    activity!!.runOnUiThread{
                        // Change Run Button text if needed to reflect state //
                        if (mRunButton!!.text == activity!!.getText(R.string.stop_sim)) {
                            mRunButton!!.text = activity!!.getText(R.string.run_sim)
                            // Change to not running if running //
                            if (mRunning) {
                                mRunning = false
                            }
                        }
                    }

                    // Reload view //
                    fragmentManager!!.beginTransaction().replace(R.id.fragment_container, ColonyRecyclerFragment()).commit()
                }

                // Update UI //
                updateUI()
            }
        }, sTimerDelay, sTimerPeriod)
    }

    // Pick color from color picker //
    private fun pickColor(name: String, preColor: Int) {
        // Create color picker //
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
                // OnPickColor form color picker //
                override fun onColorPicked(color: Int) {
                    // Change color for cells //
                    changeColor(name, color)
                }

                // Needed override for color picker //
                override fun onColor(color: Int, fromUser: Boolean) {}
            })
    }

    // Get files for file picker //
    private fun pickFile() {
        // Create file picker //
        // - Open Document Action
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            // Only get files that can be opened //
            addCategory(Intent.CATEGORY_OPENABLE)

            // Files of any type //
            type = FILE_TYPE
        }

        // Start file picker intent //
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    // Create files for file picker //
    private fun createFile(mimeType: String, fileName: String) {
        // Create file picker //
        // - Create Document Action
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            // Only get files that can be opened //
            addCategory(Intent.CATEGORY_OPENABLE)

            // Create a file with the requested MIME type //
            type = mimeType

            // Give intent file name as title //
            putExtra(Intent.EXTRA_TITLE, fileName)
        }

        // Start file picker intent //
        startActivityForResult(intent, WRITE_REQUEST_CODE)
    }

    // Delete file for file picker //
    private fun deleteFile() {
        // Create file picker //
        // - Open Document Action
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            // Only get files that can be opened //
            addCategory(Intent.CATEGORY_OPENABLE)

            // Get file for given file type //
            type = FILE_TYPE
        }

        // Start file picker intent //
        startActivityForResult(intent, DELETE_REQUEST_CODE)
    }

    // Change color for given cell type //
    // - Name of type (Alive, Dead)
    // - New color
    private fun changeColor(name: String, color: Int) {
        // For given name //
        when(name) {
            // Set alive color //
            Colony.sAliveColor.toString() -> Colony.sAliveColor = color

            // Set dead color //
            Colony.sDeadColor.toString() -> Colony.sDeadColor = color
        }

        // Update colors for cells //
        mColony!!.updateColors()

        // Update UI //
        updateUI()
    }

    // Save data to given file //
    private fun saveDataToFile(uri: Uri?) {
        // Create file output stream for URI //
        activity!!.contentResolver.openOutputStream(uri!!).use { outputStream ->
            // Create writer for given stream //
            BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                // Write data to file //
                // - Colony data encoded to JSON
                writer.write(mColony!!.encode())
            }
        }
    }

    // Load data from given file //
    private fun loadDataFromFile(uri: Uri?) {
        // Create file input stream for URI //
        activity!!.contentResolver.openInputStream(uri!!).use { inputStream ->
            // Create reader for stream //
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                // Read data from file //
                // - Decode JSON data to Colony
                mColony!!.decode(reader.readText())
            }
        }

        // Update UI //
        updateUI()
    }
}
