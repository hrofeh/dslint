package com.hananrh.dslint.sample

import com.ironsource.aura.dslint.annotations.DSLExtension

fun create() {
	dslTest {
		id = "2414"
		name = "Hanan"
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

@set:DSLExtension("firstName")
var SomeDSLApi.name: String
	get() = throw UnsupportedOperationException()
	set(value) {
		this.id = value
	}