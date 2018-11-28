package com.github.huksley.zeebe.workers

data class KafkaMessage (
    val orderId: String,
    val from: String?,
    val source: String?
)