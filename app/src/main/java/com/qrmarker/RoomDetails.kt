package com.qrmarker

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class RoomDetails : AppCompatActivity() {
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var list: LinearLayout
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var empty: RelativeLayout
    private lateinit var load: RelativeLayout
    private lateinit var error: RelativeLayout
    private lateinit var verifyCon: RelativeLayout
    private lateinit var verify: Button
    private lateinit var name: TextView
    private lateinit var status: TextView
    private lateinit var updated: TextView
    private lateinit var created: TextView
    private lateinit var back: ImageView
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
        name = findViewById(R.id.name)
        status = findViewById(R.id.status)
        updated = findViewById(R.id.updated)
        created = findViewById(R.id.created)

        back = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }

        verify.setOnClickListener {
            loadingDialog.show()
            if (verify.text.equals(resources.getString(R.string.verify))) {
                BackEndConnection(this).verifyRoom(id)
            } else {
                BackEndConnection(this).unverifyRoom(id)
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

        BackEndConnection(this).getSpecificRoom(id)
    }

    fun gotten(i: Int, title: String, status: String, created: String, updated: String) {
        if (i == 1) {
            name.text = title.replaceFirstChar { it.uppercase() }
            if (status.equals("pending", true)) {
                this.status.text =
                    resources.getString(R.string.pending).replaceFirstChar { it.uppercase() }
                this.updated.text = resources.getString(R.string.unverified)
                verify.text = resources.getString(R.string.verify)
            } else {
                this.status.text =
                    resources.getString(R.string.verified).replaceFirstChar { it.uppercase() }
                this.updated.text = updated
                verify.text = resources.getString(R.string.unverify)
            }
            this.created.text = created

            list.visibility = View.VISIBLE
            empty.visibility = View.GONE
            load.visibility = View.GONE
            error.visibility = View.GONE
            verifyCon.visibility = View.VISIBLE
        } else {
            list.visibility = View.GONE
            empty.visibility = View.GONE
            error.visibility = View.VISIBLE
            load.visibility = View.GONE
            verifyCon.visibility = View.GONE
        }
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
}