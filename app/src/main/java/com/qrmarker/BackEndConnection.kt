package com.qrmarker

import android.content.Context
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BackEndConnection(var context: Context) {

    fun connect(
        type: String,
        method: Int,
        extension: String,
        jsonObject: JSONObject,
        position: Int
    ) {
        val requestQue = Volley.newRequestQueue(context)
        val url = "https://qrmarker-api.herokuapp.com/api/v1/$extension"
        val request = object : JsonObjectRequest(method, url, jsonObject, {
            try {
                val message = it.getString("message")
                when (type) {
                    "createOrganization" -> {
                        if (message == "Success") {
                            (context as Organizations).created(
                                1,
                                it.getJSONObject("payload").getString("_id"),
                                it.getJSONObject("payload").getString("title")
                            )
                        } else {
                            (context as Organizations).created(-1, "", "")
                        }
                    }
                    "createRoom" -> {
                        if (message == "Success") {
                            (context as Rooms).created(
                                1,
                                it.getJSONObject("payload").getString("_id"),
                                it.getJSONObject("payload").getString("title"),
                                it.getJSONObject("payload").getString("status")
                            )
                        } else {
                            (context as Rooms).created(-1, "", "", "")
                        }
                    }
                    "deleteOrganization" -> {
                        if (message == "Success") {
                            (context as Organizations).deleted(1, position)
                        } else {
                            (context as Organizations).deleted(-1, position)
                        }
                    }
                    "deleteRoom" -> {
                        if (message == "Success") {
                            (context as Rooms).deleted(1, position)
                        } else {
                            (context as Rooms).deleted(-1, position)
                        }
                    }
                    "getAllOrganization" -> {
                        if (message == "Success") {
                            (context as Organizations).gotten(
                                1,
                                it.getJSONObject("payload").getJSONArray("docs")
                            )
                        } else {
                            (context as Organizations).gotten(-1, JSONArray())
                        }
                    }
                    "getSpecificOrganization" -> {
                        if (message == "Success") {
                            (context as Rooms).gotten(
                                1,
                                it.getJSONObject("payload").getJSONArray("codes")
                            )
                        } else {
                            (context as Rooms).gotten(-1, JSONArray())
                        }
                    }
                    "getSpecificRoom" -> {
                        if (message == "Success") {
                            (context as RoomDetails).gotten(
                                1,
                                it.getJSONObject("payload").getJSONObject("org").getString("title"),
                                it.getJSONObject("payload").getString("title"),
                                it.getJSONObject("payload").getString("status"),
                                it.getJSONObject("payload").getString("createdAt"),
                                it.getJSONObject("payload").getString("updatedAt")
                            )
                        } else {
                            (context as RoomDetails).gotten(-1, "", "", "", "", "")
                        }
                    }
                    "verifyRoom", "unverifyRoom" -> {
                        if (message == "Success") {
                            (context as RoomDetails).updated(1)
                        } else {
                            (context as RoomDetails).updated(-1)
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                when (type) {
                    "createOrganization" -> {
                        (context as Organizations).created(-1, "", "")
                    }
                    "createRoom" -> {
                        (context as Rooms).created(-1, "", "", "")
                    }
                    "deleteOrganization" -> {
                        (context as Organizations).deleted(-1, position)
                    }
                    "deleteRoom" -> {
                        (context as Rooms).deleted(-1, position)
                    }
                    "getAllOrganization" -> {
                        (context as Organizations).gotten(-1, JSONArray())
                    }
                    "getSpecificOrganization" -> {
                        (context as Rooms).gotten(-1, JSONArray())
                    }
                    "getSpecificRoom" -> {
                        (context as RoomDetails).gotten(-1, "", "", "", "", "")
                    }
                    "verifyRoom", "unverifyRoom" -> {
                        (context as RoomDetails).updated(-1)
                    }
                }
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            when (type) {
                "createOrganization" -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.unauthorized),
                            Toast.LENGTH_LONG
                        ).show()
                        if (context is Organizations) {
                            (context as Organizations).finish()
                        }
                    } else {
                        (context as Organizations).created(-1, "", "")
                    }
                }
                "createRoom" -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.unauthorized),
                            Toast.LENGTH_LONG
                        ).show()
                        if (context is Rooms) {
                            (context as Rooms).finish()
                        }
                    } else {
                        (context as Rooms).created(-1, "", "", "")
                    }
                }
                "deleteOrganization" -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.unauthorized),
                            Toast.LENGTH_LONG
                        ).show()
                        if (context is Organizations) {
                            (context as Organizations).finish()
                        }
                    } else {
                        (context as Organizations).deleted(-1, position)
                    }
                }
                "deleteRoom" -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.unauthorized),
                            Toast.LENGTH_LONG
                        ).show()
                        if (context is Rooms) {
                            (context as Rooms).finish()
                        }
                    } else {
                        (context as Rooms).deleted(-1, position)
                    }
                }
                "getAllOrganization" -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.unauthorized),
                            Toast.LENGTH_LONG
                        ).show()
                        if (context is Organizations) {
                            (context as Organizations).finish()
                        }
                    } else {
                        (context as Organizations).gotten(-1, JSONArray())
                    }
                }
                "getSpecificOrganization" -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.unauthorized),
                            Toast.LENGTH_LONG
                        ).show()
                        if (context is Rooms) {
                            (context as Rooms).finish()
                        }
                    } else {
                        (context as Rooms).gotten(-1, JSONArray())
                    }
                }
                "getSpecificRoom" -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.unauthorized),
                            Toast.LENGTH_LONG
                        ).show()
                        if (context is RoomDetails) {
                            (context as RoomDetails).finish()
                        }
                    } else {
                        (context as RoomDetails).gotten(-1, "", "", "", "", "")
                    }
                }
                "verifyRoom", "unverifyRoom" -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.unauthorized),
                            Toast.LENGTH_LONG
                        ).show()
                        if (context is RoomDetails) {
                            (context as RoomDetails).finish()
                        }
                    } else {
                        (context as RoomDetails).updated(-1)
                    }
                }
            }
        }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer ${Session(context).password()}"
                return header
            }
        }
        requestQue.add(request)
    }
}