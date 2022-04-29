package com.qrmarker

import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RoomDetails : AppCompatActivity() {
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var list: LinearLayout
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var empty: RelativeLayout
    private lateinit var load: RelativeLayout
    private lateinit var error: RelativeLayout
    private lateinit var verifyCon: RelativeLayout
    private lateinit var verify: Button
    private lateinit var status: TextView
    private lateinit var updated: TextView
    private var id: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_details)

        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("id") != null) {
                id = bundle.getString("id")!!
            }
        }

        loadingDialog = LoadingDialog(this)
        swipe = findViewById(R.id.swipe)
        swipe.setColorSchemeResources(R.color.black)
        swipe.setOnRefreshListener {
            loadData(true)
            swipe.isRefreshing = false
        }

        empty = findViewById(R.id.empty)
        load = findViewById(R.id.load)
        error = findViewById(R.id.error)
        verifyCon = findViewById(R.id.verifyCon)
        verify = findViewById(R.id.verify)
        list = findViewById(R.id.list)
        status = findViewById(R.id.status)
        updated = findViewById(R.id.updated)

        findViewById<ImageView>(R.id.back).setOnClickListener {
            finish()
        }

        verify.setOnClickListener {
            loadingDialog.show()
            if (verify.text.equals(resources.getString(R.string.verify))) {
                BackEndConnection(
                    this,
                    "verifyRoom",
                    Request.Method.GET,
                    "codes/$id/mark",
                    JSONObject(),
                    -1
                )
            } else {
                BackEndConnection(
                    this,
                    "unverifyRoom",
                    Request.Method.GET,
                    "codes/$id/unmark",
                    JSONObject(),
                    -1
                )
            }
        }
        loadData(true)
    }

    private fun loadData(show: Boolean) {
        if (show) {
            list.visibility = View.GONE
            empty.visibility = View.GONE
            error.visibility = View.GONE
            verifyCon.visibility = View.GONE
            load.visibility = View.VISIBLE
        }

        BackEndConnection(
            this,
            "getSpecificRoom",
            Request.Method.GET,
            "codes/$id",
            JSONObject(),
            -1
        )
    }

    fun gotten(
        i: Int,
        org: String,
        title: String,
        status: String,
        created: String,
        updated: String
    ) {
        if (i == 1) {
            findViewById<TextView>(R.id.org).text = org.replaceFirstChar { it.uppercase() }
            findViewById<TextView>(R.id.name).text = title.replaceFirstChar { it.uppercase() }
            if (status.equals("pending", true)) {
                this.status.text =
                    resources.getString(R.string.pending).replaceFirstChar { it.uppercase() }
                this.updated.text = resources.getString(R.string.unverified)
                verify.text = resources.getString(R.string.verify)
            } else {
                this.status.text =
                    resources.getString(R.string.verified).replaceFirstChar { it.uppercase() }
                this.updated.text = timeAgo(updated)
                verify.text = resources.getString(R.string.unverify)
            }
            findViewById<TextView>(R.id.created).text = timeAgo(created)

            list.visibility = View.VISIBLE
            error.visibility = View.GONE
            verifyCon.visibility = View.VISIBLE
        } else {
            list.visibility = View.GONE
            error.visibility = View.VISIBLE
            verifyCon.visibility = View.GONE
        }
        empty.visibility = View.GONE
        load.visibility = View.GONE
    }

    fun updated(i: Int) {
        loadingDialog.dismiss()
        if (i == 1) {
            loadData(false)
            Toast.makeText(this, getString(R.string.updated_successfully), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_LONG).show()
        }
    }

    private fun timeAgo(value: String): String {
        var ago = value
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        try {
            val time: Long = sdf.parse(value).time
            val now = System.currentTimeMillis()
            ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)
                .toString()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ago
    }
}