package com.hananrh.dslint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.hananrh.dslint.detectors.DslMandatoryDetector
import kotlin.contracts.ExperimentalContracts

/**
 * Contains references to all custom lint checks for config.
 */
@ExperimentalContracts
class LintRegistry : IssueRegistry() {

    override val api = CURRENT_API

    override val issues = listOf(DslMandatoryDetector.ISSUE)
}