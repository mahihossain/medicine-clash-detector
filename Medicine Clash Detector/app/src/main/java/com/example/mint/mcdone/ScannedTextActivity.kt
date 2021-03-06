package com.example.mint.mcdone

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.mint.mcdone.model.AddMedicine
import com.example.mint.mcdone.model.AddMedicineSingleton
import com.example.mint.mcdone.model.MedicineDatabase
import com.example.mint.mcdone.util.FirestoreUtil
import kotlinx.android.synthetic.main.activity_scanned_text.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

/**
This activity process the scanned image to the text for saving
it in the database. This class is responsible for converting image
to text.
*/

class ScannedTextActivity : AppCompatActivity() {

    private lateinit var prePopulatedDB: MedicineDatabase
    private lateinit var mDb: AddMedicineSingleton

    var flag = false
    var s = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanned_text)

        // Initialize the room database
        prePopulatedDB = MedicineDatabase.getInstance(applicationContext)
        mDb = AddMedicineSingleton.getInstance(applicationContext)


        val scannedText  = intent.getStringExtra("scannedText")

//        val value = "kotlin \n Java \n Ace"
//        val splittedText = value.split(" ")
//        val splittedText = scannedText.lines()
        val splittedText = scannedText.split(" ", "  ", "   ", ".","\n")
//        val ss = ". marjuk  .  MaXpro   rasel "
//        val splittedText = ss.split(" ","  ","   ",".","\n")
        scannedTextView.setText(scannedText)
//        scannedTextView.setText(value)

        button_back.setOnClickListener {
            val intent = Intent(this@ScannedTextActivity, ImageScannerActivity::class.java)
            startActivity(intent)
        }

        button_proceed.setOnClickListener {
            doAsync {
                // Get the list from database
                val prePopulatedList = prePopulatedDB.medicineDao().getMedicines()

                for (medicine in splittedText){
                    for (prePopMed in prePopulatedList){
                        if(medicine.toLowerCase() == prePopMed.brandName.toLowerCase()){


                            // Initialize a new addMedicine
                            val addMedicine = AddMedicine(id = null,
                                    bName = medicine
                            )
//                            // Put the addMedicine in database
                            mDb.addMedicineDao().insert(addMedicine)

                            //Put the addMedicine in firestore
                            FirestoreUtil.addCurrentUserMedicine(addMedicine.bName)

                            flag =true
                            s = medicine

                        }
                        if (flag){
                            break
                        }
                    }

                    if (flag){
                        break
                    }

                }

                uiThread {

                    if (flag){
                        flag = false
                        toast("Medicine inserted in the list")
                    }
                    else{
                        toast("Invalid input")
                    }

                }
            }
        }
    }
}
