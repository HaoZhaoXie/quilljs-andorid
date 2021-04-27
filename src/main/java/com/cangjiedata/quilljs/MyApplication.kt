package com.cangjiedata.quilljs

import com.cangjiedata.baselibrary.BaseApplication
import okhttp3.OkHttpClient

/**
 * Create by Judge at 1/4/21
 */
class MyApplication : BaseApplication() {

    override fun getBaseUrl(): String {
        //九日哥
//        return "http://192.168.0.107:9999/"
        //司妹
//        return "http://192.168.0.181:9999/"
        //杜友
//        return "http://192.168.0.152:9999/"
        return "http://183.131.134.242:10167/api/"
    }

    override fun onConfigHttpClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
    }

    override fun inItIM() {
        super.inItIM()
    }

    override fun IMLoginOut() {
    }

    override fun initApp() {
        super.initApp()
    }
}