package com.example.mint.mcdone

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.mint.mcdone.fragment.AddMedicineFragment
import com.example.mint.mcdone.fragment.EditProfileFragment
import com.example.mint.mcdone.fragment.RemoveMedicineFragment
import com.example.mint.mcdone.fragment.UserHomeFragment
import com.example.mint.mcdone.model.AddMedicineSingleton
import com.example.mint.mcdone.model.HealthConditionDatabase
import com.example.mint.mcdone.util.FirestoreUtil
import com.example.mint.mcdone.util.replaceFragmenty
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.navigationView

/**
This is the home activity. This activity is responsible
for showing the user their home screen with their profile
picture and also connecting with the menu button for
further navigation.
*/

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var mDb: AddMedicineSingleton
    private lateinit var hDb: HealthConditionDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        //Navbar username setting accroding to the user name
        setNavbarUsername()

        //Setting up Userhome fragment to open as default
        replaceFragmenty(
                fragment = UserHomeFragment(),
                allowStateLoss = true,
                containerViewId = R.id.mainContent
        )

        // Initialize the room database
        mDb = AddMedicineSingleton.getInstance(applicationContext)
        hDb = HealthConditionDatabase.getInstance(applicationContext)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handling action bar item clicks here.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handling navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
//                replaceFragment(UserHomeFragment())
//                var name : String?
//
//                FirestoreUtil.getCurrentUser {
//                    name = it?.name
//                    user_name_navbar.setText(name)
//                }

                replaceFragmenty(
                        fragment = UserHomeFragment(),
                        allowStateLoss = true,
                        containerViewId = R.id.mainContent
                )
//                setTitle("Import")
            }
            R.id.nav_edit_profile -> {
                // Handle the item
//                sample_text.setText("Edit is pressed")
                replaceFragmenty(
                        fragment = EditProfileFragment(),
                        allowStateLoss = true,
                        containerViewId = R.id.mainContent
                )

            }
            R.id.nav_add_medicine-> {
                replaceFragmenty(
                        fragment = AddMedicineFragment(),
                        allowStateLoss = true,
                        containerViewId = R.id.mainContent
                )
            }
            R.id.nav_remove_medicine -> {
                replaceFragmenty(
                        fragment = RemoveMedicineFragment(),
                        allowStateLoss = true,
                        containerViewId = R.id.mainContent
                )
            }
            R.id.nav_show_medicine -> {
                val intent = Intent(this@MainActivity, ShowMedicineListActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_status -> {
                val intent = Intent(this@MainActivity, ShowClashReportActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_sync -> {
                val intent = Intent(this@MainActivity, SyncDataActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_delete_account -> {

                //Destroy local user database
                doAsync {

                    mDb.addMedicineDao().nukeTable()
                    hDb.healthConditionDao().nukeTable()

                    uiThread{

                    }
                }

                AuthUI.getInstance()
                        .delete(this@MainActivity)
                        .addOnCompleteListener {
                            startActivity(intentFor<SignInActivity>().newTask().clearTask())
                        }
            }
            R.id.nav_logout -> {

                //Destroy local user database
                doAsync {

                    mDb.addMedicineDao().nukeTable()
                    hDb.healthConditionDao().nukeTable()

                    uiThread{

                    }
                }

                AuthUI.getInstance()
                        .signOut(this@MainActivity)
                        .addOnCompleteListener{
                            startActivity(intentFor<SignInActivity>().newTask().clearTask())
                        }
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //Navbar username setting accroding to the user name
    fun setNavbarUsername(){
        var name : String?
        FirestoreUtil.getCurrentUser {
            name = it?.name
            user_name_navbar.setText(name)
        }
    }

}

