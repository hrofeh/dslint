package com.hananrh.dslint.utils

import com.intellij.psi.impl.source.PsiClassReferenceType
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.uast.UBinaryExpression
import org.jetbrains.uast.UBlockExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.UTypeReferenceExpression
import org.jetbrains.uast.toUElement
import org.jetbrains.uast.tryResolve

internal val UBinaryExpression.property
	get() = if (leftOperand.tryResolve() is KtProperty) {
		leftOperand.tryResolve() as KtProperty
	} else (((leftOperand.tryResolve().toUElement() as UMethod?)?.sourcePsi) as? KtPropertyAccessor)?.property

internal val UBinaryExpression.propertyReceiverQualifiedName
	get() = property?.receiverTypeReference.qualifiedName

val UBinaryExpression.propertySetterBody
	get() = property?.setter?.bodyBlockExpression.toUElement() as UBlockExpression?

internal val UCallExpression.methodBodyBlock
	get() = (resolve().toUElement() as? UMethod)?.uastBody as UBlockExpression?

internal val UCallExpression.methodReceiverQualifiedName
	get() = (resolve().toUElement()?.sourcePsi as? KtCallableDeclaration)?.receiverTypeReference.qualifiedName

internal val KtTypeReference?.qualifiedName
	get() = ((toUElement() as? UTypeReferenceExpression)?.type as? PsiClassReferenceType)?.resolve()?.qualifiedName