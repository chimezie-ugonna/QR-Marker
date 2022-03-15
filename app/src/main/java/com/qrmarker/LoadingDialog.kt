package com.qrmarker

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

class LoadingDialog(
    private val context: Context,
    private var message: String = context.resources.getString(R.string.loading)
) {
    private var dialog: AlertDialog.Builder = AlertDialog.Builder(context)
    private var alertDialog: AlertDialog
    private var text: TextView

    init {
        dialog.setCancelable(false)
        val inflater1 = LayoutInflater.from(context)
        val v: View = inflater1.inflate(R.layout.loading, null, false)
        text = v.findViewById(R.id.text)
        dialog.setView(v)
        alertDialog = dialog.create()
    }

    fun show() {
        text.text = message
        alertDialog.show()
    }

    fun dismiss() {
        if (alertDialog.isShowing) {
            alertDialog.dismiss()
        }
    }
}