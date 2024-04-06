package com.hananrh.dslint.sample.sdk2

import com.hananrh.dslint.annotations.DSLExtension

@DSLExtension("firstName")
inline fun <reified T> SomeLibraryDSLApi<T>.name2(name: String) {
	firstName = name
}