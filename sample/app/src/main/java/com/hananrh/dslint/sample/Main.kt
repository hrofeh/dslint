package com.hananrh.dslint.sample

import com.hananrh.dslint.sample.sdk2.dslLibraryTest
import com.hananrh.dslint.sample.sdk2.name2

fun create() {
	dslLibraryTest {
		id = "2414"
		name2("Hanan")
		inner {
			innerProp = ""
			innerProp2()
		}
	}
}