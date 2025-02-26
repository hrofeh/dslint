package com.hananrh.dslint.utils

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiWildcardType
import com.intellij.psi.impl.source.PsiClassReferenceType
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.getParameterForArgument
import org.jetbrains.uast.kotlin.KotlinULambdaExpression

fun ULambdaExpression.getReceiverType(
    callExpression: UCallExpression
): PsiClass? {
    // TODO - for some reason this is sometimes null
    val parameter = callExpression.getParameterForArgument(this)?.type
    parameter ?: return null

    val receiverType = (parameter as PsiClassReferenceType).parameters.getOrNull(0)

    if (receiverType !is PsiWildcardType) {
        return null
    }

    return (receiverType.bound as PsiClassType).resolve()
}

/**
 * Google lint api bug workaround.
 * KotlinULambdaExpression impl drops the last statement and puts an implicit return statement instead.
 */
@Suppress("UnstableApiUsage")
val ULambdaExpression.bodyExpressions: List<UExpression>
    get() {
        val statements = (body.sourcePsi as? KtBlockExpression)?.statements
        if (statements.isNullOrEmpty()) return emptyList()
        return ArrayList<UExpression>(statements.size).also { result ->
            statements.mapTo(result) {
                (this as KotlinULambdaExpression).baseResolveProviderService.baseKotlinConverter.convertOrEmpty(
                    it,
                    this
                )
            }
        }
    }