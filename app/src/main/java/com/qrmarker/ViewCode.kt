package com.qrmarker

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class ViewCode : AppCompatActivity() {
    private var id: String = ""
    private lateinit var back: ImageView
    private lateinit var code: ImageView

    @SuppressLint("SetWorldReadable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_code)

        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("id") != null) {
                id = bundle.getString("id")!!
            }
        }

        code = findViewById(R.id.code)
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(
                id, BarcodeFormat.QR_CODE,
                resources.getDimension(R.dimen.image_dimen).toInt(),
                resources.getDimension(R.dimen.image_dimen).toInt()
            )
            code.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        back = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.share).setOnClickListener {
            val bm = (code.drawable as BitmapDrawable).bitmap
            try {
                val file = File(externalCacheDir.toString() + "/image.jpg")
                val out: OutputStream = FileOutputStream(file)
                bm.compress(CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            } catch (e: java.lang.Exception) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.share_error_message),
                    Toast.LENGTH_LONG
                ).show()
            }
            val i = Intent(Intent.ACTION_SEND)
            i.type = "*/*"
            i.putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(
                    this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    File(externalCacheDir.toString() + "/image.jpg")
                )
            )
            startActivity(Intent.createChooser(i, "Send image"))
        }
    }
}