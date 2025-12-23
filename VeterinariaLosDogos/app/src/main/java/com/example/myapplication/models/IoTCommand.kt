package com.example.myapplication.models

class IoTCommand {
    var command: String? = null
    var timestamp: Long = 0

    constructor()

    constructor(command: String?, timestamp: Long) {
        this.command = command
        this.timestamp = timestamp
    }
}

