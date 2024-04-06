package com.hananrh.dslint.annotations

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.FUNCTION)
annotation class DSLExtension(val extensionFor: String)