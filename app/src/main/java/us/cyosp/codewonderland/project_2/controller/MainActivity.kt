package us.cyosp.codewonderland.project_2.controller

/*
    Project Creators:
    Dylan Blanchard, Alice Easter

    Project-2: The Game of Life
 */

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import us.cyosp.codewonderland.project_2.R
import us.cyosp.codewonderland.project_2.model.Colony

class MainActivity : AppCompatActivity() {

    // Static data for activity recreate intent //
    companion object {
        const val COLONY_DATA_ID = "colony_data"
        const val COLONY_WIDTH_ID = "colony_width"
        const val COLONY_HEIGHT_ID = "colony_height"
    }

    // Creates a new intent for MainActivity //
    fun newIntent(packageContext: Context, colony: Colony): Intent {
        // New intent for MainActivity //
        val intent = Intent(packageContext, MainActivity::class.java)

        // Give intent colony data encoded as JSON //
        intent.putExtra(COLONY_DATA_ID, colony.encode())

        // Give intent colony height //
        intent.putExtra(COLONY_HEIGHT_ID, colony.mHeight)

        // Given intent colony width //
        intent.putExtra(COLONY_WIDTH_ID, colony.mWidth)

        // Return new intent //
        return intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // check for colony data from intent //
        val colonyData: String? = this.intent?.extras?.getString(COLONY_DATA_ID)

        // check for colony width from intent //
        val colonyWidth: Int? = this.intent?.extras?.getInt(COLONY_WIDTH_ID)

        // check for colony height from intent //
        val colonyHeight: Int? = this.intent?.extras?.getInt(COLONY_HEIGHT_ID)

        // Get fragment manager //
        val fm = supportFragmentManager

        // Get fragment for ID //
        var fragment: Fragment? = fm.findFragmentById(R.id.fragment_container)

        if (fragment == null) {
            // create new Bundle //
            val bundle = Bundle()

            // if we have colony data, we pass it to the fragment //
            if (colonyData != null) {

                // put the colony data into bundle //
                bundle.putString(COLONY_DATA_ID, colonyData)

                // put the colony width into bundle //
                if (colonyWidth != null) {
                    bundle.putInt(COLONY_WIDTH_ID, colonyWidth)
                }

                // put the colony height into bundle //
                if (colonyHeight != null) {
                    bundle.putInt(COLONY_HEIGHT_ID, colonyHeight)
                }

                // establish fragment //
                fragment = ColonyRecyclerFragment()

                // pass bundle data to fragment as argument //
                fragment.arguments = bundle

            } else {
                // if we don't have colony data
                // we just create the empty fragment
                fragment = ColonyRecyclerFragment()
            }

            // Create intent from fragment //
            fm.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
}
