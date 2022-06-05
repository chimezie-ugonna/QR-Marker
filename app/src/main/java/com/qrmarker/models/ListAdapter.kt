package com.qrmarker.models

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.qrmarker.R
import com.qrmarker.activities.Organizations
import com.qrmarker.activities.Rooms
import com.qrmarker.activities.UserList
import com.qrmarker.activities.ViewCode
import org.json.JSONObject

class ListAdapter(
    private val context: Context,
    private val names: ArrayList<String>,
    private val statuses: ArrayList<String>,
    private val ids: ArrayList<String>,
    private val emails: ArrayList<String>?,
    private val types: ArrayList<String>?,
    private val phoneNumbers: ArrayList<String>?
) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.parent.setOnClickListener {
            if (context is Organizations) {
                val i = Intent(context, Rooms::class.java)
                i.putExtra("organizationId", ids[holder.adapterPosition])
                context.startActivity(i)
            } else if (context is UserList) {
                if (context.type == "") {
                    context.loadingDialog.show()
                    BackEndConnection(
                        context,
                        "assignUser",
                        Request.Method.POST,
                        "orgs/${context.organizationId}/assign",
                        JSONObject().put("userId", ids[holder.adapterPosition]),
                        -1
                    )
                }
            } else {
                if (Session(context).userType() != "user") {
                    val i = Intent(context, ViewCode::class.java)
                    i.putExtra("id", ids[holder.adapterPosition])
                    context.startActivity(i)
                }
            }
        }
        if (context is Organizations) {
            holder.name.text = names[holder.adapterPosition].replaceFirstChar { it.uppercase() }
            holder.status.visibility = View.GONE
            holder.status.text = ""
            if (Session(context).userType() == "user") {
                holder.delete.visibility = View.GONE
            }
        } else {
            if (context is UserList) {
                if (context.type != "") {
                    holder.details.visibility = View.VISIBLE
                    holder.normal.visibility = View.GONE
                    holder.fullName.text =
                        names[holder.adapterPosition].replaceFirstChar { it.uppercase() }
                    holder.email.text = emails?.get(holder.adapterPosition) ?: ""
                    holder.type.text = types?.get(holder.adapterPosition) ?: ""
                    holder.phoneNumber.text = phoneNumbers?.get(holder.adapterPosition) ?: ""
                } else {
                    holder.name.text =
                        names[holder.adapterPosition].replaceFirstChar { it.uppercase() }
                    holder.details.visibility = View.GONE
                    holder.normal.visibility = View.VISIBLE
                    holder.status.visibility = View.VISIBLE
                    holder.status.text = statuses[holder.adapterPosition]
                    holder.status.setTextColor(ContextCompat.getColor(context, R.color.dark_grey))
                }
                holder.delete.visibility = View.GONE
            } else {
                if (context is Rooms) {
                    if (context.selectStatus != "") {
                        holder.box.visibility = View.VISIBLE
                    } else {
                        holder.box.visibility = View.GONE
                    }
                    holder.box.setOnClickListener {
                        if (holder.box.isChecked) {
                            context.selectedRooms.add(ids[holder.adapterPosition])
                            context.selectedRoomsPositions.add(holder.adapterPosition)
                        } else {
                            context.selectedRooms.remove(ids[holder.adapterPosition])
                            context.selectedRoomsPositions.remove(holder.adapterPosition)
                        }
                    }
                }
                holder.name.text = names[holder.adapterPosition].replaceFirstChar { it.uppercase() }
                holder.status.visibility = View.VISIBLE
                if (statuses[holder.adapterPosition].equals("pending", true)) {
                    holder.status.text = context.resources.getString(R.string.unverified)
                        .replaceFirstChar { it.uppercase() }
                    holder.status.setTextColor(ContextCompat.getColor(context, R.color.red))
                } else {
                    holder.status.text = context.resources.getString(R.string.verified)
                        .replaceFirstChar { it.uppercase() }
                    holder.status.setTextColor(ContextCompat.getColor(context, R.color.green))
                }
                if (Session(context).userType() == "user") {
                    holder.delete.visibility = View.GONE
                }
            }
        }
        holder.delete.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
            dialog.setCancelable(false)
            if (context is Organizations) {
                dialog.setTitle(context.resources.getString(R.string.delete_org))
                dialog.setMessage(context.resources.getString(R.string.delete_org_confirm))
                dialog.setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                    context.loadingDialog.show()
                    BackEndConnection(
                        context,
                        "deleteOrganization",
                        Request.Method.DELETE,
                        "orgs/${ids[holder.adapterPosition]}",
                        JSONObject(),
                        holder.adapterPosition
                    )
                }
            } else if (context is Rooms) {
                dialog.setTitle(context.resources.getString(R.string.delete_room))
                dialog.setMessage(context.resources.getString(R.string.delete_room_confirm))
                dialog.setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                    context.loadingDialog.show()
                    BackEndConnection(
                        context,
                        "deleteRoom",
                        Request.Method.DELETE,
                        "codes/${ids[holder.adapterPosition]}",
                        JSONObject(),
                        holder.adapterPosition
                    )
                }
            }
            dialog.setNegativeButton(context.resources.getString(R.string.no)) { d, _ ->
                d.dismiss()
            }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return names.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var parent: RelativeLayout = v.findViewById(R.id.parent)
        var name: TextView = v.findViewById(R.id.name)
        var status: TextView = v.findViewById(R.id.status)
        var delete: ImageView = v.findViewById(R.id.delete)
        var normal: LinearLayout = v.findViewById(R.id.normal)
        var details: LinearLayout = v.findViewById(R.id.details)
        var fullName: TextView = v.findViewById(R.id.full_name)
        var email: TextView = v.findViewById(R.id.email)
        var type: TextView = v.findViewById(R.id.type)
        var phoneNumber: TextView = v.findViewById(R.id.phone_number)
        var box: CheckBox = v.findViewById(R.id.box)
    }
}