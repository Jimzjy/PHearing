package io.github.phearing.phearing.common

import android.graphics.drawable.Drawable

data class User(val id: Int, val username: String, val birthYear: Int, val phoneNumber: String,
                val sex: Boolean, val deaf: Boolean, val hearingAid: Boolean, val cochlearImplant: Boolean, val avatar: String?)

data class Page<T>(val count: Int, val next: String?, val previous: String?, val results: List<T>)

data class Token(val token: String, val userId: Int)

data class NewsListData(val url: String, val title: String, val excerpt: String, val image: String)

data class NewsListDataShow(val url: String, val title: String, val excerpt: String, val image: Drawable?)

data class NewsData(val pubTime: String, val title: String, val content: String)

data class NavigationData(val image: String, val contentUrl: String)

data class NavigationDataShow(val image: Drawable, val contentUrl: String)