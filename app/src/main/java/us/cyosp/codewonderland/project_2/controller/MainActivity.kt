package us.cyosp.codewonderland.project_2.controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import us.cyosp.codewonderland.project_2.R
import us.cyosp.codewonderland.project_2.model.Colony

class MainActivity : AppCompatActivity() {

    companion object {
        const val COLONY_DATA_ID = "colony_data"
    }

    fun newIntent(packageContext: Context, colony: Colony): Intent {
        val intent = Intent(packageContext, MainActivity::class.java)
        intent.putExtra(COLONY_DATA_ID, colony.encode())
        return intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // check for colony data from intent
        val colonyData: String? = this.intent?.extras?.getString(COLONY_DATA_ID)

        val fm = supportFragmentManager
        var fragment: Fragment? = fm.findFragmentById(R.id.fragment_container)

        if (fragment == null) {

            // if we have colony data, we pass it to the fragment
            if (colonyData != null) {
                // create new Bundle
                val bundle = Bundle()
                // put the colony data into bundle
                bundle.putString(COLONY_DATA_ID, colonyData)

                // establish fragment
                fragment = ColonyRecyclerFragment()
                // pass bundle data to fragment as argument
                fragment.arguments = bundle

            } else {
                // if we don't have colony data
                // we just create the empty fragment
                fragment = ColonyRecyclerFragment()
            }

            fm.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
}
