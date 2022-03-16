package com.qrmarker

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BackEndConnection(var context: Context) {
    private lateinit var requestQue: RequestQueue

    fun createOrganization(
        title: String
    ) {
        val jsonObject = JSONObject()
        jsonObject.put("title", title)
        requestQue = Volley.newRequestQueue(context)
        val url = "https://qrmarker-api.herokuapp.com/api/v1/orgs"
        val request = object : JsonObjectRequest(Method.POST, url, jsonObject, {
            try {
                val message = it.getString("message")
                if (message == "Success") {
                    (context as Organizations).created(
                        1,
                        it.getJSONObject("payload").getString("_id"),
                        it.getJSONObject("payload").getString("title")
                    )
                } else {
                    (context as Organizations).created(-1, "", "")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                (context as Organizations).created(-1, "", "")
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            (context as Organizations).created(-1, "", "")
        }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer 1234"
                return header
            }
        }
        requestQue.add(request)
    }

    fun createRoom(
        id: String,
        title: String
    ) {
        val jsonObject = JSONObject()
        jsonObject.put("title", title)
        requestQue = Volley.newRequestQueue(context)
        val url = "https://qrmarker-api.herokuapp.com/api/v1/codes/$id"
        val request = object : JsonObjectRequest(Method.POST, url, jsonObject, {
            try {
                val message = it.getString("message")
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
            } catch (e: JSONException) {
                e.printStackTrace()
                (context as Rooms).created(-1, "", "", "")
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            (context as Rooms).created(-1, "", "", "")
        }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer 1234"
                return header
            }
        }
        requestQue.add(request)
    }

    fun deleteOrganization(
        id: String,
        position: Int
    ) {
        requestQue = Volley.newRequestQueue(context)
        val url = "https://qrmarker-api.herokuapp.com/api/v1/orgs/$id"
        val request = object : JsonObjectRequest(Method.DELETE, url, JSONObject(), {
            try {
                val message = it.getString("message")
                if (message == "Success") {
                    (context as Organizations).deleted(1, position)
                } else {
                    (context as Organizations).deleted(-1, position)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                (context as Organizations).deleted(-1, position)
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            (context as Organizations).deleted(-1, position)
        }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer 1234"
                return header
            }
        }
        requestQue.add(request)
    }

    fun deleteRoom(
        id: String,
        position: Int
    ) {
        requestQue = Volley.newRequestQueue(context)
        val url = "https://qrmarker-api.herokuapp.com/api/v1/codes/$id"
        val request = object : JsonObjectRequest(Method.DELETE, url, JSONObject(), {
            try {
                val message = it.getString("message")
                if (message == "Success") {
                    (context as Rooms).deleted(1, position)
                } else {
                    (context as Rooms).deleted(-1, position)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                (context as Rooms).deleted(-1, position)
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            (context as Rooms).deleted(-1, position)
        }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer 1234"
                return header
            }
        }
        requestQue.add(request)
    }

    fun getAllOrganization() {
        requestQue = Volley.newRequestQueue(context)
        val url = "https://qrmarker-api.herokuapp.com/api/v1/orgs"
        val request = object : JsonObjectRequest(Method.GET, url, JSONObject(), {
            try {
                val message = it.getString("message")
                if (message == "Success") {
                    (context as Organizations).gotten(
                        1,
                        it.getJSONObject("payload").getJSONArray("docs")
                    )
                } else {
                    (context as Organizations).gotten(-1, JSONArray())
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                (context as Organizations).gotten(-1, JSONArray())
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            (context as Organizations).gotten(-1, JSONArray())
        }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer 1234"
                return header
            }
        }
        requestQue.add(request)
    }

    fun getSpecificOrganization(id: String) {
        requestQue = Volley.newRequestQueue(context)
        val url = "https://qrmarker-api.herokuapp.com/api/v1/orgs/$id"
        val request = object : JsonObjectRequest(Method.GET, url, JSONObject(), {
            try {
                val message = it.getString("message")
                if (message == "Success") {
                    (context as Rooms).gotten(
                        1,
                        it.getJSONObject("payload").getJSONArray("codes")
                    )
                } else {
                    (context as Rooms).gotten(-1, JSONArray())
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                (context as Rooms).gotten(-1, JSONArray())
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            (context as Rooms).gotten(-1, JSONArray())
        }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer 1234"
                return header
            }
        }
        requestQue.add(request)
    }

    fun getSpecificRoom(id: String) {
        requestQue = Volley.newRequestQueue(context)
        val url = "https://qrmarker-api.herokuapp.com/api/v1/codes/$id"
        val request = object : JsonObjectRequest(Method.GET, url, JSONObject(), {
            try {
                val message = it.getString("message")
                if (message == "Success") {
                    (context as RoomDetails).gotten(
                        1,
                        it.getJSONObject("payload").getString("title"),
                        it.getJSONObject("payload").getString("status"),
                        it.getJSONObject("payload").getString("createdAt"),
                        it.getJSONObject("payload").getString("updatedAt")
                    )
                } else {
                    (context as RoomDetails).gotten(-1, "", "", "", "")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                (context as RoomDetails).gotten(-1, "", "", "", "")
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            (context as RoomDetails).gotten(-1, "", "", "", "")
        }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer 1234"
                return header
            }
        }
        requestQue.add(request)
    }

    fun verifyRoom(id: String) {
        requestQue = Volley.newRequestQueue(context)
        val url = "https://qrmarker-api.herokuapp.com/api/v1/codes/$id/mark"
        val request = object : JsonObjectRequest(Method.GET, url, JSONObject(), {
            try {
                val message = it.getString("message")
                if (message == "Success") {
                    (context as RoomDetails).updated(1)
                } else {
                    (context as RoomDetails).updated(-1)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                (context as RoomDetails).updated(-1)
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            (context as RoomDetails).updated(-1)
        }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer 1234"
                return header
            }
        }
        requestQue.add(request)
    }

    fun unverifyRoom(id: String) {
        requestQue = Volley.newRequestQueue(context)
        val url = "https://qrmarker-api.herokuapp.com/api/v1/codes/$id/unmark"
        val request = object : JsonObjectRequest(Method.GET, url, JSONObject(), {
            try {
                val message = it.getString("message")
                if (message == "Success") {
                    (context as RoomDetails).updated(1)
                } else {
                    (context as RoomDetails).updated(-1)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                (context as RoomDetails).updated(-1)
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            (context as RoomDetails).updated(-1)
        }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer 1234"
                return header
            }
        }
        requestQue.add(request)
    }
}