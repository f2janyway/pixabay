package com.box.coroutinex

data class Hits (
    var id: Float = 0f,
    var pageURL: String? = null,
    var type: String? = null,
    var tags: String? = null,
    var previewURL: String? = null,
    var previewWidth: Float = 0f,
    var previewHeight: Float = 0f,
    var webformatURL: String? = null,
    var webformatWidth: Float = 0f,
    var webformatHeight: Float = 0f,
    var largeImageURL: String? = null,
    var imageWidth: Float = 0f,
    var imageHeight: Float = 0f,
    var imageSize: Float = 0f,
    var views: Float = 0f,
    var downloads: Float = 0f,
    var favorites: Float = 0f,
    var likes: Float = 0f,
    var comments: Float = 0f,
    var user_id: Float = 0f,
    var user: String? = null,
    var userImageURL: String? = null
)