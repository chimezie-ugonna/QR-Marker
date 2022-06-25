package com.qrmarker.models

import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.qrmarker.controller.activities.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BackEndConnection(
    var context: Context, var type: String,
    var method: Int,
    var extension: String,
    var jsonObject: JSONObject,
    var position: Int,
    var url: String = "https://qrmarker-api.herokuapp.com/api/v1/"
) {

    init {
        val requestQue = Volley.newRequestQueue(context)
        val request = object : JsonObjectRequest(method, "$url$extension", jsonObject, {
            try {
                val status = it.getInt("status")
                when (type) {
                    "logIn" -> {
                        if (status in 200..299) {
                            KeyStore(context).encryptData(
                                it.getJSONObject("payload").getString("token")
                            )
                            Session(context).fullName(
                                it.getJSONObject("payload").getJSONObject("user")
                                    .getString("fullName")
                            )
                            Session(context).email(
                                it.getJSONObject("payload").getJSONObject("user").getString("email")
                            )
                            Session(context).userType(
                                it.getJSONObject("payload").getJSONObject("user")
                                    .getString("userType")
                            )
                            Session(context).phoneNumber(
                                it.getJSONObject("payload").getJSONObject("user")
                                    .getString("phoneNumber")
                            )
                            (context as MainActivity).loggedIn(1, "")
                        } else {
                            (context as MainActivity).loggedIn(-1, "")
                        }
                    }
                    "register" -> {
                        if (status in 200..299) {
                            KeyStore(context).encryptData(
                                it.getJSONObject("payload").getString("token")
                            )
                            Session(context).fullName(
                                it.getJSONObject("payload").getJSONObject("user")
                                    .getString("fullName")
                            )
                            Session(context).email(
                                it.getJSONObject("payload").getJSONObject("user").getString("email")
                            )
                            Session(context).userType(
                                it.getJSONObject("payload").getJSONObject("user")
                                    .getString("userType")
                            )
                            Session(context).phoneNumber(
                                it.getJSONObject("payload").getJSONObject("user")
                                    .getString("phoneNumber")
                            )
                            (context as MainActivity).registered(1, "")
                        } else {
                            (context as MainActivity).registered(-1, "")
                        }
                    }
                    "createOrganization" -> {
                        if (status in 200..299) {
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
                        if (status in 200..299) {
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
                        if (status in 200..299) {
                            (context as Organizations).deleted(1, position)
                        } else {
                            (context as Organizations).deleted(-1, position)
                        }
                    }
                    "assignUser" -> {
                        if (status in 200..299) {
                            (context as UserList).assigned(1)
                        } else {
                            (context as UserList).assigned(-1)
                        }
                    }
                    "deleteRoom" -> {
                        if (status in 200..299) {
                            (context as Rooms).deleted(1, position)
                        } else {
                            (context as Rooms).deleted(-1, position)
                        }
                    }
                    "getAllUsers" -> {
                        if (status in 200..299) {
                            (context as UserList).gotten(
                                1,
                                it.getJSONObject("payload").getJSONArray("docs"), JSONObject()
                            )
                        } else {
                            (context as UserList).gotten(-1, JSONArray(), JSONObject())
                        }
                    }
                    "getAllOrganization" -> {
                        if (status in 200..299) {
                            (context as Organizations).gotten(
                                1,
                                it.getJSONObject("payload").getJSONArray("docs")
                            )
                        } else {
                            (context as Organizations).gotten(-1, JSONArray())
                        }
                    }
                    "getHistory" -> {
                        if (status in 200..299) {
                            try {
                                (context as History).gotten(
                                    1,
                                    it.getJSONObject("payload").getJSONArray("docs")
                                )
                            } catch (e: JSONException) {
                                (context as History).gotten(
                                    2,
                                    JSONArray()
                                )
                            }
                        } else {
                            (context as History).gotten(-1, JSONArray())
                        }
                    }
                    "getAssignedUser" -> {
                        if (status in 200..299) {
                            try {
                                (context as UserList).gotten(
                                    1,
                                    JSONArray(),
                                    it.getJSONObject("payload").getJSONObject("assignee")
                                )
                            } catch (e: JSONException) {
                                (context as UserList).gotten(
                                    2,
                                    JSONArray(),
                                    JSONObject()
                                )
                            }
                        } else {
                            (context as UserList).gotten(-1, JSONArray(), JSONObject())
                        }
                    }
                    "removeAssignee" -> {
                        if (status in 200..299) {
                            (context as UserList).removed(1)
                        } else {
                            (context as UserList).removed(-1)
                        }
                    }
                    "getSpecificOrganization" -> {
                        if (status in 200..299) {
                            (context as Rooms).gotten(
                                1,
                                it.getJSONObject("payload").getJSONArray("codes")
                            )
                        } else {
                            (context as Rooms).gotten(-1, JSONArray())
                        }
                    }
                    "getSpecificRoom" -> {
                        if (status in 200..299) {
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
                        if (status in 200..299) {
                            if (context is RoomDetails) {
                                (context as RoomDetails).updated(1)
                            } else {
                                (context as Rooms).verifiedOrUnverifiedRoomS(1, position, type)
                            }
                        } else {
                            if (context is RoomDetails) {
                                (context as RoomDetails).updated(-1)
                            } else {
                                (context as Rooms).verifiedOrUnverifiedRoomS(-1, position, type)
                            }
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                when (type) {
                    "logIn" -> {
                        (context as MainActivity).loggedIn(-1, "")
                    }
                    "register" -> {
                        (context as MainActivity).registered(-1, "")
                    }
                    "createOrganization" -> {
                        (context as Organizations).created(-1, "", "")
                    }
                    "createRoom" -> {
                        (context as Rooms).created(-1, "", "", "")
                    }
                    "assignUser" -> {
                        (context as UserList).assigned(-1)
                    }
                    "deleteOrganization" -> {
                        (context as Organizations).deleted(-1, position)
                    }
                    "deleteRoom" -> {
                        (context as Rooms).deleted(-1, position)
                    }
                    "getAllUsers" -> {
                        (context as UserList).gotten(-1, JSONArray(), JSONObject())
                    }
                    "getAllOrganization" -> {
                        (context as Organizations).gotten(-1, JSONArray())
                    }
                    "getHistory" -> {
                        (context as History).gotten(-1, JSONArray())
                    }
                    "getAssignedUser" -> {
                        (context as UserList).gotten(-1, JSONArray(), JSONObject())
                    }
                    "removeAssignee" -> {
                        (context as UserList).removed(-1)
                    }
                    "getSpecificOrganization" -> {
                        (context as Rooms).gotten(-1, JSONArray())
                    }
                    "getSpecificRoom" -> {
                        (context as RoomDetails).gotten(-1, "", "", "", "", "")
                    }
                    "verifyRoom", "unverifyRoom" -> {
                        if (context is RoomDetails) {
                            (context as RoomDetails).updated(-1)
                        } else {
                            (context as Rooms).verifiedOrUnverifiedRoomS(-1, position, type)
                        }
                    }
                }
            }
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            var message = ""
            if (error.networkResponse != null && error.networkResponse.data != null) {
                message = String(error.networkResponse.data)
            }
            when (type) {
                "logIn" -> {
                    (context as MainActivity).loggedIn(-1, message)
                }
                "register" -> {
                    (context as MainActivity).registered(-1, message)
                }
                "createOrganization" -> {
                    (context as Organizations).created(-1, "", "")
                }
                "createRoom" -> {
                    (context as Rooms).created(-1, "", "", "")
                }
                "assignUser" -> {
                    (context as UserList).assigned(-1)
                }
                "deleteOrganization" -> {
                    (context as Organizations).deleted(-1, position)
                }
                "deleteRoom" -> {
                    (context as Rooms).deleted(-1, position)
                }
                "getAllUsers" -> {
                    (context as UserList).gotten(-1, JSONArray(), JSONObject())
                }
                "getAllOrganization" -> {
                    (context as Organizations).gotten(-1, JSONArray())
                }
                "getHistory" -> {
                    (context as History).gotten(-1, JSONArray())
                }
                "getAssignedUser" -> {
                    (context as UserList).gotten(-1, JSONArray(), JSONObject())
                }
                "removeAssignee" -> {
                    (context as UserList).removed(-1)
                }
                "getSpecificOrganization" -> {
                    (context as Rooms).gotten(-1, JSONArray())
                }
                "getSpecificRoom" -> {
                    (context as RoomDetails).gotten(-1, "", "", "", "", "")
                }
                "verifyRoom", "unverifyRoom" -> {
                    if (context is RoomDetails) {
                        (context as RoomDetails).updated(-1)
                    } else {
                        (context as Rooms).verifiedOrUnverifiedRoomS(-1, position, type)
                    }
                }
            }
        }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                if (Session(context).encryptedTokenIv() != "" && Session(context).encryptedToken() != "") {
                    val decryptedData = KeyStore(context).decryptData()
                    header["Authorization"] = "Bearer $decryptedData"
                }
                return header
            }
        }
        requestQue.add(request)
    }
}