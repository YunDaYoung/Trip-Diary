package com.hya.tripdiary

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hya.tripdiary.database.AppDatabase
import com.hya.tripdiary.database.Diary
import kotlinx.android.synthetic.main.activity_diary_register.*
import java.io.*


class DiaryRegister : AppCompatActivity() {

    private var tempFile: File? = null
    var originalBm: Bitmap? = null
    var selectedDateData: String? = null

    private var appDatabase: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_register)

        imageView.setImageResource(R.drawable.pictureicon)

        appDatabase = AppDatabase.getInstance(this)
        selectedDateData = intent.getStringExtra("SelectedDay")

        selectedDateText.text = selectedDateData

        val addRunnable = Runnable {
            val newDiary = Diary(
                id = null,
                title = addTitle.text.toString(),
                contents = addContent.text.toString(),
                time = selectedDateData
            )
            appDatabase?.diaryDao()?.insert(newDiary)
        }

        addPhoto.setOnClickListener {
            goToAlbum()
        }

        cancelBtn.setOnClickListener {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
            finish()
        }

        saveBtn.setOnClickListener {

            if(addTitle.text.toString().isBlank()){
                Toast.makeText(this, "Please add title", Toast.LENGTH_SHORT).show()
            }
            else if(originalBm == null){
                Toast.makeText(this, "Please add image", Toast.LENGTH_SHORT).show()
            }
            else if(addContent.text.toString().isBlank()){
                Toast.makeText(this, "Please add content", Toast.LENGTH_SHORT).show()
            }
            else {
//                saveDiary(content.toString(), title.toString())

                val addThread = Thread(addRunnable)
                addThread.start()

                saveBitmapToJpeg()

                finish()

            }
        }
    }

    override fun onDestroy() {
        AppDatabase.destroyInstance()
        super.onDestroy()
    }

    //Import Image from Gallery
    fun goToAlbum(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
        startActivityForResult(intent, 1)
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try{
            if(requestCode == 1){
                val photoUri = data!!.data
                var cursor: Cursor? = null

                try {
                    val proj =
                        arrayOf(MediaStore.Images.Media.DATA)
                    assert(photoUri != null)
                    cursor = contentResolver.query(photoUri!!, proj, null, null, null)

                    assert(cursor != null)
                    val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                    cursor.moveToFirst()

                    tempFile = File(cursor.getString(column_index))

                }finally {
                    if (cursor != null) {
                        cursor.close()
                    }
                }

                setImage()
            }
        }catch (e:KotlinNullPointerException){
            Toast.makeText(this,"No selected image", Toast.LENGTH_SHORT).show()
        }
    }

    //Showing Image by ImageView
    fun setImage(){
        val options = BitmapFactory.Options()
        originalBm = BitmapFactory.decodeFile(tempFile!!.absolutePath, options)
        imageView.setImageBitmap(originalBm)
    }

    //Saving the selected image
    fun saveBitmapToJpeg() {
        val r = Runnable {
            try {
                val id = appDatabase?.diaryDao()?.getMaxId()!!
                val filename = id.toString() + ".jpg"

                val dirPath : String = Environment.getExternalStorageDirectory().toString() + "/TripDiary/" + selectedDateData
                val file = File(dirPath)
                if( !file.exists() )  // 원하는 경로에 폴더가 있는지 확인 Check Folder of my path
                    file.mkdirs()

                try{
                    val out = FileOutputStream(file.toString() + "/" + filename)    //filename 변경
                    originalBm?.compress(Bitmap.CompressFormat.JPEG, 100, out)

                    out.close()
                } catch (e : FileNotFoundException){
                    Toast.makeText(this, "FileNotFoundException1", Toast.LENGTH_SHORT).show()
                } catch (e : IOException){
                    Toast.makeText(this, "IOException1", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.d("tag", "Error - $e")
            }
        }
        val thread = Thread(r)
        thread.start()
    }
}
