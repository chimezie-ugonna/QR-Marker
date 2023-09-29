package com.qrmarker.controller.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.qrmarker.models.LoadingDialog
import com.qrmarker.models.Session
import org.json.JSONArray
import org.json.JSONObject
import java.util.Objects

class Organizations : AppCompatActivity() {
    lateinit var loadingDialog: LoadingDialog
    private lateinit var list: RecyclerView
    private lateinit var names: ArrayList<String>
    private lateinit var statuses: ArrayList<String>
    private lateinit var ids: ArrayList<String>
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var empty: RelativeLayout
    private lateinit var load: RelativeLayout
    private lateinit var error: RelativeLayout
    private lateinit var alert: AlertDialog
    private lateinit var add: ImageView
    private lateinit var dividerItemDecoration: DividerItemDecoration
    private lateinit var la: ListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organizations)

        loadingDialog = LoadingDialog(this)
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

        add = findViewById(R.id.add)
        if (Session(this).userType() == "user") {
            add.visibility = View.GONE
        }
        add.setOnClickListener {
            val v = LayoutInflater.from(this).inflate(R.layout.add_item, null, false)
            v.findViewById<TextView>(R.id.title).text = getString(R.string.create_organization)
            v.findViewById<TextView>(R.id.description).text =
                getString(R.string.enter_name_of_organization)
            val name = v.findViewById<EditText>(R.id.name)
            val dialog = AlertDialog.Builder(this)
            dialog.setCancelable(false)
            dialog.setPositiveButton(
                resources.getString(R.string.create)
            ) { _, _ -> }

            dialog.setNegativeButton(
                resources.getString(R.string.cancel)
            ) { d, _ -> d.dismiss() }
            dialog.setView(v)
            alert = dialog.create()
            alert.show()
            alert.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    if (name.text.isNotEmpty()) {
                        loadingDialog.show()
                        BackEndConnection(
                            this, "createOrganization",
                            Request.Method.POST,
                            "orgs",
                            JSONObject().put("title", name.text.toString()),
                            -1
                        )
                    } else {
                        name.error = getString(R.string.empty_field_error)
                    }
                }
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
        ids = ArrayList()

        BackEndConnection(
            this,
            "getAllOrganization",
            Request.Method.GET,
            "orgs",
            JSONObject(),
            -1
        )
    }

    fun gotten(i: Int, jsonArray: JSONArray) {
        if (i == 1) {
            if (jsonArray.length() > 0) {
                for (j in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(j)
                    names.add(jsonObject.getString("title"))
                    ids.add(jsonObject.getString("_id"))
                    statuses.add("")
                }

                list.visibility = View.VISIBLE
                empty.visibility = View.GONE
                error.visibility = View.GONE
            } else {
                list.visibility = View.GONE
                empty.visibility = View.VISIBLE
                error.visibility = View.GONE
            }
            la = ListAdapter(this, names, statuses, ids, null, null, null)
            list.adapter = la
        } else {
            list.visibility = View.GONE
            empty.visibility = View.GONE
            error.visibility = View.VISIBLE
        }
        load.visibility = View.GONE
    }

    fun created(i: Int, id: String, title: String) {
        loadingDialog.dismiss()
        if (i == 1) {
            alert.dismiss()
            names.add(0, title)
            statuses.add(0, "")
            ids.add(0, id)
            la.notifyItemInserted(0)
            list.visibility = View.VISIBLE
            empty.visibility = View.GONE
            load.visibility = View.GONE
            error.visibility = View.GONE
            list.smoothScrollToPosition(0)
            Toast.makeText(this, getString(R.string.created_successfully), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, getString(R.string.creation_failed), Toast.LENGTH_LONG).show()
        }
    }

    fun deleted(i: Int, position: Int) {
        loadingDialog.dismiss()
        if (i == 1) {
            names.removeAt(position)
            statuses.removeAt(position)
            ids.removeAt(position)
            la.notifyItemRemoved(position)
            if (names.size == 0) {
                loadData()
            }
            Toast.makeText(this, getString(R.string.deleted_successfully), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, getString(R.string.deletion_failed), Toast.LENGTH_LONG).show()
        }
    }
}