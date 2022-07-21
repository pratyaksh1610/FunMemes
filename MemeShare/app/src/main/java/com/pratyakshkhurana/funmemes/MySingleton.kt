package com.pratyakshkhurana.funmemes

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

//Singleton class is the one , it has only one instance
//so app can call one request at at time
//so we call using singleton and follow singleton pattern
class MySingleton constructor(context: Context) {
    companion object {
        //volatile to make thread safe, ensure safety
        @Volatile
        private var INSTANCE: MySingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MySingleton(context).also {
                    INSTANCE = it
                }
            }
    }
    private val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}