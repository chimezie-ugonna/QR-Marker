package com.qrmarker.controller.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.qrmarker.R
import com.qrmarker.adapter.ListAdapter
import com.qrmarker.models.BackEndConnection
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class History : AppCompatActivity() {
    private lateinit var list: RecyclerView
    private lateinit var names: ArrayList<String>
    private lateinit var statuses: ArrayList<String>
    private lateinit var comments: ArrayList<String>
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var empty: RelativeLayout
    private lateinit var load: RelativeLayout
    private lateinit var error: RelativeLayout
    private lateinit var dividerItemDecoration: DividerItemDecoration
    private lateinit var la: ListAdapter
    private var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("id") != null) {
                id = bundle.getString("id")!!
            }
        }

        swipe = findViewById(R.id.swipe)
        swipe.setColorSchemeResources(R.color.black)
        swipe.setOnRefreshListener {
            loadData()
            swipe.isRefreshing = false
        }

        empty = findViewById(R.id.empty)
        load = findViewById(R.id.load)
        error = findViewById(R.id.error)
        list = findViewById(R.id.list)
        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        (Objects.requireNonNull(list.itemAnimator) as SimpleItemAnimator).supportsChangeAnimations =
            false
        dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider)!!)
        list.addItemDecoration(dividerItemDecoration)

        findViewById<ImageView>(R.id.back).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.view_code).setOnClickListener {
            val i = Intent(this, ViewCode::class.java)
            i.putExtra("id", id)
            startActivity(i)
        }

        loadData()
    }

    private fun loadData() {
        list.visibility = View.GONE
        empty.visibility = View.GONE
        error.visibility = View.GONE
        load.visibility = View.VISIBLE

        names = ArrayList()
        statuses = ArrayList()
        comments = ArrayList()

        BackEndConnection(
            this,
            "getHistory",
            Request.Method.GET,
            "codes/$id/history",
            JSONObject(),
            -1
        )
    }

    fun gotten(i: Int, jsonArray: JSONArray) {
        when (i) {
            1 -> {
                if (jsonArray.length() > 0) {
                    for (j in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(j)
                        val jsonObject2 = jsonObject.getJSONObject("user")
                        names.add(jsonObject2.getString("fullName"))
                        comments.add(jsonObject.getString("comment"))
                        if (jsonObject.getBoolean("verified")) {
                            statuses.add(
                                "${getString(R.string.verified_this_room_on)} ${
                                    timeAgo(
                                        jsonObject.getString("createdAt")
                                    )
                                }"
                            )
                        } else {
                            statuses.add(
                                "${getString(R.string.unverified_this_room_on)} ${
                                    timeAgo(
                                        jsonObject.getString("createdAt")
                                    )
                                }"
                            )
                        }
                    }

                    list.visibility = View.VISIBLE
                    empty.visibility = View.GONE
                } else {
                    list.visibility = View.GONE
                    empty.visibility = View.VISIBLE
                }
                error.visibility = View.GONE
                la = ListAdapter(this, names, statuses, comments, null, null, null)
                list.adapter = la
            }
            2 -> {
                list.visibility = View.GONE
                empty.visibility = View.VISIBLE
                error.visibility = View.GONE
            }
            else -> {
                list.visibility = View.GONE
                empty.visibility = View.GONE
                error.visibility = View.VISIBLE
            }
        }
        load.visibility = View.GONE
    }

    private fun timeAgo(value: String): String {
        val od = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
        od.timeZone = TimeZone.getTimeZone("UTC")
        val odD: Date = od.parse(value) as Date
        return SimpleDateFormat("dd.MM.yy h:mm a", Locale.ENGLISH).format(odD)
    }
}