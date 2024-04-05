package com.hananrh.dslint.sample

import com.hananrh.dslint.sample.sdk2.SomeLibraryDSLApi
import com.hananrh.dslint.sample.sdk2.dslLibraryTest
import com.hananrh.dslint.sample.sdk2.extInner

fun create() {
	dslLibraryTest {
		id = "2414"
		name = "Hanan"
		inner {
			extInner()
		}
	}
}

var SomeLibraryDSLApi<*>.name: String
	get() = throw UnsupportedOperationException()
	set(value) {
		firstName = value
	}