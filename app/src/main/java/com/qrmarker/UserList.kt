package com.qrmarker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class UserList : AppCompatActivity() {
    lateinit var loadingDialog: LoadingDialog
    private lateinit var list: RecyclerView
    private lateinit var names: ArrayList<String>
    private lateinit var statuses: ArrayList<String>
    private lateinit var ids: ArrayList<String>
    private lateinit var emails: ArrayList<String>
    private lateinit var types: ArrayList<String>
    private lateinit var phoneNumbers: ArrayList<String>
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var empty: RelativeLayout
    private lateinit var load: RelativeLayout
    private lateinit var error: RelativeLayout
    private lateinit var dividerItemDecoration: DividerItemDecoration
    private lateinit var la: ListAdapter
    var organizationId: String = ""
    var type: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("organizationId") != null) {
                organizationId = bundle.getString("organizationId")!!
            }
            if (bundle.getString("type") != null) {
                type = bundle.getString("type")!!
            }
        }

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
        emails = ArrayList()
        types = ArrayList()
        phoneNumbers = ArrayList()

        if (type != "") {
            BackEndConnection(
                this,
                "getAssignedUser",
                Request.Method.GET,
                "orgs/$organizationId",
                JSONObject(),
                -1
            )
        } else {
            BackEndConnection(
                this,
                "getAllUsers",
                Request.Method.GET,
                "auth",
                JSONObject(),
                -1
            )
        }
    }

    fun gotten(i: Int, jsonArray: JSONArray, jsonObject_: JSONObject?) {
        if (i == 1) {
            if (type != "") {
                names.add(" ${jsonObject_?.getString("fullName")}")
                ids.add(" ${jsonObject_?.getString("_id")}")
                statuses.add(" ${jsonObject_?.getString("email")}")
                emails.add(" ${jsonObject_?.getString("email")}")
                types.add(" ${jsonObject_?.getString("userType")}")
                try {
                    phoneNumbers.add(" ${jsonObject_?.getString("phoneNumber")}")
                } catch (e: JSONException) {
                    phoneNumbers.add("")
                }
                list.visibility = View.VISIBLE
                empty.visibility = View.GONE
                error.visibility = View.GONE
            } else {
                if (jsonArray.length() > 0) {
                    for (j in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(j)
                        names.add(jsonObject.getString("fullName"))
                        ids.add(jsonObject.getString("_id"))
                        statuses.add(jsonObject.getString("email"))
                        emails.add(jsonObject.getString("email"))
                        types.add(jsonObject.getString("userType"))
                        try {
                            phoneNumbers.add(jsonObject.getString("phoneNumber"))
                        } catch (e: JSONException) {
                            phoneNumbers.add("")
                        }
                    }

                    list.visibility = View.VISIBLE
                    empty.visibility = View.GONE
                    error.visibility = View.GONE
                } else {
                    list.visibility = View.GONE
                    empty.visibility = View.VISIBLE
                    error.visibility = View.GONE
                }
            }
            la = ListAdapter(this, names, statuses, ids, emails, types, phoneNumbers)
            list.adapter = la
        } else {
            list.visibility = View.GONE
            empty.visibility = View.GONE
            error.visibility = View.VISIBLE
        }
        load.visibility = View.GONE
    }

    fun assigned(i: Int) {
        loadingDialog.dismiss()
        if (i == 1) {
            finish()
            Toast.makeText(this, getString(R.string.user_assigned_message), Toast.LENGTH_LONG)
                .show()
        } else {
            Toast.makeText(
                this,
                getString(R.string.user_assigned_failure_message),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}