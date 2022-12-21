package com.iris.socialmedia.model

class EtatModel(
    var id:String = "etat0",
    var user_id: String = "vide",
    var publication_id: String = "vide",
    var type: String = "favorite/comment",
    var content: String? = null,
    var date: String? = null,
    var exist: String? = null
)