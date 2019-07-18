package com.example.may1.messeger.Models

class ChatMessage( val id: String, val fromID: String, val toID: String, val text: String, val timestamp: Long) {
    constructor() : this("", "","","",-1)
}