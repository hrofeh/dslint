package com.hananrh.dslint.utils

fun String?.nullIfEmpty() = if (this.isNullOrEmpty()) null else this