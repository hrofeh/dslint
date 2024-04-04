package com.ironsource.aura.dslint.utils

import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.uast.UBinaryExpression
import org.jetbrains.uast.UBlockExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.UTypeReferenceExpression
import org.jetbrains.uast.toUElement
import org.jetbrains.uast.tryResolve

internal val UBinaryExpression.propertySetterBody
	get() = propertySetter?.uastBody as UBlockExpression?

internal val UBinaryExpression.propertyReceiverQualifiedName
	get() = ((propertySetter?.sourcePsi) as? KtPropertyAccessor)?.property?.receiverTypeReference.qualifiedName

internal val UBinaryExpression.propertySetter
	get() = leftOperand.tryResolve().toUElement() as UMethod?

internal val UCallExpression.methodBodyBlock
	get() = (resolve().toUElement() as? UMethod)?.uastBody as UBlockExpression?

internal val UCallExpression.methodReceiverQualifiedName
	get() = (resolve().toUElement()?.sourcePsi as? KtCallableDeclaration)?.receiverTypeReference.qualifiedName

internal val KtTypeReference?.qualifiedName
	get() = (toUElement() as? UTypeReferenceExpression)?.type?.canonicalText