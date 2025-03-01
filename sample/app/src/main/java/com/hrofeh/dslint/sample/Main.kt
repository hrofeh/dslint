package com.hrofeh.dslint.sample

import com.hrofeh.dslint.sample.sdk2.dslLibraryTest
import com.hrofeh.dslint.sample.sdk2.name2

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