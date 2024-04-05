package com.hananrh.dslint.sample.sdk2

import com.hananrh.dslint.annotations.DSLMandatory
import com.hananrh.dslint.annotations.DSLint

fun <T> dslLibraryTest(block: SomeLibraryDSLApi<T>.() -> Unit) = SomeLibraryDslApiImpl<T>().apply(block)

@DSLint
interface SomeLibraryDSLApi<T> {

    object Group {
        const val NAME = "name"
    }

    @set:DSLMandatory(message = "ID please")
    var id: String

    @set:DSLMandatory(group = Group.NAME)
    var firstName: String

    @set:DSLMandatory(group = Group.NAME)
    var fullName: String

    var optionalProp: String

    @DSLMandatory
    fun inner(block: InnerLibraryDSL<T>.() -> Unit)
}

class SomeLibraryDslApiImpl<T> : SomeLibraryDSLApi<T> {
    override lateinit var id: String
    override lateinit var firstName: String
    override lateinit var fullName: String
    override lateinit var optionalProp: String
    override fun inner(block: InnerLibraryDSL<T>.() -> Unit) {

    }
}

@DSLint
interface InnerLibraryDSL<T> {

    @set:DSLMandatory
    var innerProp: String

    @DSLMandatory
    fun innerProp2()
}

class InnerDSLImpl<T> : InnerLibraryDSL<T> {
    override lateinit var innerProp: String
    override fun innerProp2() {
    }
}