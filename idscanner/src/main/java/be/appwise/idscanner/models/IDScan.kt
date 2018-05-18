package be.appwise.idscanner.models

import java.util.*

open class IDScan(
		var firstName: String = "",
		var lastName: String = "",
		var birthDate: Date = Date(),
		var gender: String = "",
		var nationality: String = "",
		var cardNumber: String = "",
		var validUntil: Date = Date(),
		var registerID: String = ""
)