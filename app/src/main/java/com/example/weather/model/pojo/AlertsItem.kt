package com.example.weather.model.pojo

data class AlertsItem(
	val start: Int? = null,
	val description: String? = null,
	val senderName: String? = null,
	val end: Int? = null,
	val event: String? = null,
	val tags: List<String?>? = null
)
