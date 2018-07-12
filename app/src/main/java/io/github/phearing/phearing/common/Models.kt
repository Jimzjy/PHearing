package io.github.phearing.phearing.common

import android.graphics.drawable.Drawable

data class NewsData(val title: String, val content: String, val image: Drawable?)

data class User(val id: Int, val token: String, val username: String, val sex: Boolean,
                val deaf: Boolean, val hearingAid: Boolean, val cochlearImplant: Boolean, val avatar: Drawable?)

data class Base<T>(val count: Int, val next: String, val previous: String, val results: List<T>)

data class PostLite(val title: String, val excerpt: String, val image: Drawable?)

data class Post(val title: String, val content: String, val images: List<Drawable?>)