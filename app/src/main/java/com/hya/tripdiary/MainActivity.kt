package com.hya.tripdiary

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hya.tripdiary.R.color.serenity
import com.hya.tripdiary.fragment.DiaryCalendar
import com.hya.tripdiary.fragment.CashCalendar
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private val multiplePermissionsCode = 100 // Permission RequestCode

    private val requiredPermission = arrayOf( // If you need another permission Add Here
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()
        makeMainFolder()
        supportFragmentManager.beginTransaction()
            .replace(R.id.view, DiaryCalendar())
            .commit()

        diaryBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.view, DiaryCalendar())
                .commit()
            diaryBtn.setBackgroundColor(Color.rgb(146, 168, 209))
            diaryBtn.setTextColor(Color.WHITE)
            cashbookBtn.setBackgroundColor(Color.WHITE)
            cashbookBtn.setTextColor(Color.rgb(146, 168, 209))
        }

        cashbookBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.view, CashCalendar())
                .commit()
            cashbookBtn.setBackgroundColor(Color.rgb(146, 168, 209))
            cashbookBtn.setTextColor(Color.WHITE)
            diaryBtn.setBackgroundColor(Color.WHITE)
            diaryBtn.setTextColor(Color.rgb(146, 168, 209))
        }

        mapFab.setOnClickListener {
            val nextIntent = Intent(this, MapActivity::class.java)
            startActivity(nextIntent)
        }
    }

    private fun checkPermissions() {
        val rejectedPermissionList = ArrayList<String>()

        for(permission in requiredPermission){ // Check all permission
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                rejectedPermissionList.add(permission)
            }
        }

        if(rejectedPermissionList.isNotEmpty()) {
            val array = arrayOfNulls<String>(rejectedPermissionList.size)
            ActivityCompat.requestPermissions(this, rejectedPermissionList.toArray(array), multiplePermissionsCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            multiplePermissionsCode -> {
                if(grantResults.isNotEmpty()) {
                    for((i, _) in permissions.withIndex()) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    //Make MainFile in storage
    fun makeMainFolder(){
        val dirPath : String = Environment.getExternalStorageDirectory().toString() + "/TripDiary"
        val file = File(dirPath)
        if( !file.exists() )  //Check Folder of my path
            file.mkdirs()
    }

    private var time: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis()
            Toast.makeText(applicationContext, "Press back again to exit", Toast.LENGTH_SHORT)
                .show()
        } else if (System.currentTimeMillis() - time < 2000) {
            ActivityCompat.finishAffinity(this)
        }
    }
}
