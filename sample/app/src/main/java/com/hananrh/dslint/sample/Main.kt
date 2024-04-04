package com.hananrh.dslint.sample

fun create() {
	dslTest {
		id = "2414"
		name = "Hanan"
		inner {
			extInner()
		}
	}
}

fun InnerDSL.extInner() {
	innerProp = ""
	innerProp2()
}

var SomeDSLApi.name: String
	get() = throw UnsupportedOperationException()
	set(value) {
		firstName = value
	}