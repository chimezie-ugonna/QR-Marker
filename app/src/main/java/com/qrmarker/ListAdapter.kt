package com.qrmarker

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import org.json.JSONObject

class ListAdapter(
    private val context: Context,
    private val names: ArrayList<String>,
    private val statuses: ArrayList<String>,
    private val ids: ArrayList<String>
) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.parent.setOnClickListener {
            if (context is Organizations) {
                val i = Intent(context, Rooms::class.java)
                i.putExtra("id", ids[holder.adapterPosition])
                context.startActivity(i)
            } else {
                val i = Intent(context, ViewCode::class.java)
                i.putExtra("id", ids[holder.adapterPosition])
                context.startActivity(i)
            }
        }
        holder.name.text = names[holder.adapterPosition].replaceFirstChar { it.uppercase() }
        if (context is Organizations) {
            holder.status.visibility = View.GONE
            holder.status.text = ""
        } else {
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
        }
        holder.delete.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
            dialog.setCancelable(false)
            if (context is Organizations) {
                dialog.setTitle(context.resources.getString(R.string.delete_org))
                dialog.setMessage(context.resources.getString(R.string.delete_org_confirm))
                dialog.setPositiveButton(context.resources.getString(R.string.yes)) { _, _ ->
                    context.loadingDialog.show()
                    BackEndConnection(context).connect(
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
                    BackEndConnection(context).connect(
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
    }
}