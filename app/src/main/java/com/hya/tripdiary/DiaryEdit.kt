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
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class DiaryEdit : AppCompatActivity() {

    private var diaryData = listOf<Diary>()

    private var tempFile: File? = null
    var originalBm: Bitmap? = null
    var selectedDateData: String? = null

    private var appDatabase: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_register)

        appDatabase = AppDatabase.getInstance(this)

        val id = intent.getStringExtra("id")
        selectedDateData = intent.getStringExtra("SelectedDay")

        selectedDateText.text = selectedDateData

        val r = Runnable {
            try {
                diaryData = appDatabase?.diaryDao()?.getDetail(selectedId = id.toInt())!!

                val title = diaryData[0].title.toString()
                val content = diaryData[0].contents.toString()

                addTitle.setText(title)
                addContent.setText(content)

                val filePath = Environment.getExternalStorageDirectory().toString() + "/TripDiary/" + selectedDateData + "/" + id + ".jpg"
                originalBm = BitmapFactory.decodeFile(filePath)

                imageView.setImageBitmap(originalBm)

            } catch (e: Exception) {
                Log.d("tag", "Error - $e")
            }
        }

        val thread = Thread(r)
        thread.start()

        val addRunnable = Runnable {
            try{
                val title = addTitle.text.toString()
                val content = addContent.text.toString()
                appDatabase?.diaryDao()?.updateDetail(selectedId = id.toInt(), title = title, content = content)!!

            }catch (e: Exception) {
                Log.d("tag", "Error - $e")
            }
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

                saveBitmapToJpeg(id)

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
    fun saveBitmapToJpeg(id : String) {
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
    }
}