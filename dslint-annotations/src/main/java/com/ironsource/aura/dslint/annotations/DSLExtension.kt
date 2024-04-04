package com.ironsource.aura.dslint.annotations

/**
 * DSLint library annotation.
 * Use to denote an extension function or property that set a required property on the DSL.
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_SETTER)
annotation class DSLExtension(val satisfies: String)