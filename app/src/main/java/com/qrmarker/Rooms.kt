package com.qrmarker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class Rooms : AppCompatActivity() {
    lateinit var loadingDialog: LoadingDialog
    private lateinit var list: RecyclerView
    private lateinit var names: ArrayList<String>
    private lateinit var statuses: ArrayList<String>
    private lateinit var ids: ArrayList<String>
    lateinit var selectedRooms: ArrayList<String>
    lateinit var selectedRoomsPositions: ArrayList<Int>
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var empty: RelativeLayout
    private lateinit var load: RelativeLayout
    private lateinit var error: RelativeLayout
    private lateinit var alert: AlertDialog
    private lateinit var more: ImageView
    private lateinit var popupMenu: PopupMenu
    private lateinit var actions: LinearLayout
    private lateinit var dividerItemDecoration: DividerItemDecoration
    private lateinit var la: ListAdapter
    private var organizationId: String = ""
    var selectStatus: String = ""

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms)

        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.getString("organizationId") != null) {
                organizationId = bundle.getString("organizationId")!!
            }
        }

        actions = findViewById(R.id.actions)
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

        more = findViewById(R.id.more)
        if (Session(this).userType() == "user") {
            more.visibility = View.GONE
        }
        more.setOnClickListener {
            stopOperation()
            popupMenu = PopupMenu(this, more)
            popupMenu.inflate(R.menu.room_options)
            popupMenu.gravity = Gravity.END
            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    R.id.create_room -> {
                        val v = LayoutInflater.from(this).inflate(R.layout.add_item, null, false)
                        v.findViewById<TextView>(R.id.title).text = getString(R.string.create_room)
                        v.findViewById<TextView>(R.id.description).text =
                            getString(R.string.enter_name_of_room)
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
                                        this,
                                        "createRoom",
                                        Request.Method.POST,
                                        "codes/$organizationId",
                                        JSONObject().put("title", name.text.toString()),
                                        -1
                                    )
                                } else {
                                    name.error = getString(R.string.empty_field_error)
                                }
                            }
                    }
                    R.id.select_room -> {
                        selectedRooms = ArrayList()
                        selectedRoomsPositions = ArrayList()
                        selectStatus = "started"
                        la.notifyDataSetChanged()
                        actions.visibility = View.VISIBLE
                    }
                    R.id.assign -> {
                        val i = Intent(this, UserList::class.java)
                        i.putExtra("organizationId", organizationId)
                        startActivity(i)
                    }
                    R.id.view_assignees -> {
                        val i = Intent(this, UserList::class.java)
                        i.putExtra("organizationId", organizationId)
                        i.putExtra("type", "details")
                        startActivity(i)
                    }
                }
                true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popupMenu.setForceShowIcon(true)
            }
            popupMenu.show()
        }

        findViewById<ImageView>(R.id.verify).setOnClickListener {
            if (selectedRooms.size == 0) {
                Toast.makeText(
                    this,
                    getString(R.string.verifying_empty_room_message),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setCancelable(false)
                dialog.setTitle(R.string.verify)
                if (selectedRooms.size > 1) {
                    dialog.setMessage(getString(R.string.verify_confirmation_plural))
                } else {
                    dialog.setMessage(getString(R.string.verify_confirmation_singular))
                }
                dialog.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    loadingDialog.show()
                    verifyingOrUnverifyingRoomS("verify", 0)
                }
                dialog.setNegativeButton(resources.getString(R.string.no)) { d, _ ->
                    d.dismiss()
                }
                dialog.show()
            }
        }

        findViewById<ImageView>(R.id.unverify).setOnClickListener {
            if (selectedRooms.size == 0) {
                Toast.makeText(
                    this,
                    getString(R.string.unverifying_empty_room_message),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setCancelable(false)
                dialog.setTitle(R.string.unverify)
                if (selectedRooms.size > 1) {
                    dialog.setMessage(getString(R.string.unverify_confirmation_plural))
                } else {
                    dialog.setMessage(getString(R.string.unverify_confirmation_singular))
                }
                dialog.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    loadingDialog.show()
                    verifyingOrUnverifyingRoomS("unverify", 0)
                }
                dialog.setNegativeButton(resources.getString(R.string.no)) { d, _ ->
                    d.dismiss()
                }
                dialog.show()
            }
        }

        findViewById<ImageView>(R.id.stop).setOnClickListener {
            stopOperation()
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
            "getSpecificOrganization",
            Request.Method.GET,
            "orgs/$organizationId",
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
                    statuses.add(jsonObject.getString("status"))
                }

                list.visibility = View.VISIBLE
                empty.visibility = View.GONE
            } else {
                list.visibility = View.GONE
                empty.visibility = View.VISIBLE
            }
            error.visibility = View.GONE
            la = ListAdapter(this, names, statuses, ids, null, null, null)
            list.adapter = la
        } else {
            list.visibility = View.GONE
            empty.visibility = View.GONE
            error.visibility = View.VISIBLE
        }
        load.visibility = View.GONE
    }

    fun created(i: Int, id: String, title: String, status: String) {
        loadingDialog.dismiss()
        if (i == 1) {
            alert.dismiss()
            names.add(0, title)
            statuses.add(0, status)
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

    private fun verifyingOrUnverifyingRoomS(operation: String, position: Int) {
        if (operation == "verify") {
            BackEndConnection(
                this,
                "verifyRoom",
                Request.Method.GET,
                "codes/${selectedRooms[position]}/mark",
                JSONObject(),
                position
            )
        } else {
            BackEndConnection(
                this,
                "unverifyRoom",
                Request.Method.GET,
                "codes/${selectedRooms[position]}/unmark",
                JSONObject(),
                position
            )
        }
    }

    fun verifiedOrUnverifiedRoomS(i: Int, position: Int, type: String) {
        if (i == 1) {
            val newPosition = position + 1
            if (selectedRooms.size - 1 >= newPosition) {
                if (type == "verifyRoom") {
                    statuses[selectedRoomsPositions[position]] = "marked"
                    verifyingOrUnverifyingRoomS("verify", newPosition)
                } else {
                    statuses[selectedRoomsPositions[position]] = "pending"
                    verifyingOrUnverifyingRoomS("unverify", newPosition)
                }
            } else {
                if (type == "verifyRoom") {
                    statuses[selectedRoomsPositions[position]] = "marked"
                } else {
                    statuses[selectedRoomsPositions[position]] = "pending"
                }
                loadingDialog.dismiss()
                stopOperation()
                Toast.makeText(this, getString(R.string.updated_successfully), Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            loadingDialog.dismiss()
            Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun stopOperation() {
        selectedRooms = ArrayList()
        selectStatus = ""
        la.notifyDataSetChanged()
        actions.visibility = View.GONE
    }
}