package com.hananrh.dslint.sample

import com.ironsource.aura.dslint.annotations.DSLExtension

fun create() {
	dslTest {
		extId = "2414"
		firstName = "Hanan"
		inner {
			innerProp = ""
			extInnerProp2()
		}
	}
}

@DSLExtension("innerProp2")
fun InnerDSL.extInnerProp2() {
	innerProp2()
}

@set:DSLExtension("id")
var SomeDSLApi.extId: String
	get() = throw UnsupportedOperationException()
	set(value) {
		this.id = value
	}