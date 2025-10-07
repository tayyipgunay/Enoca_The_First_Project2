package com.tayyipgunay.firststajproject.presentation.common.events

enum class MessageType {
    Success,
    Error,
    Info,
    Warning
}

enum class MessageChannel {
    Toast,
    Snackbar,
    Dialog
}