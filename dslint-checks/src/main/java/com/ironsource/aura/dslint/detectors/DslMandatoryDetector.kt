@file:Suppress("UnstableApiUsage")

package com.ironsource.aura.dslint.detectors

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PropertyUtilBase
import com.ironsource.aura.dslint.DSLintAnnotation
import com.ironsource.aura.dslint.utils.methodBodyBlock
import com.ironsource.aura.dslint.utils.methodReceiverQualifiedName
import com.ironsource.aura.dslint.utils.nullIfEmpty
import com.ironsource.aura.dslint.utils.propertyReceiverQualifiedName
import com.ironsource.aura.dslint.utils.propertySetterBody
import com.ironsource.aura.dslint.utils.resolveStringAttributeValue
import org.jetbrains.uast.UBinaryExpression
import org.jetbrains.uast.UBlockExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UIdentifier
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.UReferenceExpression
import org.jetbrains.uast.util.isAssignment
import org.jetbrains.uast.util.isMethodCall
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
class DslMandatoryDetector : DSLintDetector() {

	companion object {
		val ISSUE = Issue.create(
			"Missing DSL attribute",
			"Mandatory DSL attribute not defined",
			"Mandatory DSL attribute not defined",
			Category.CORRECTNESS, 6,
			Severity.ERROR,
			Implementation(DslMandatoryDetector::class.java, Scope.JAVA_FILE_SCOPE)
		)
	}

	override fun visitDslLintLambda(
		context: JavaContext,
		node: ULambdaExpression,
		dslLintClass: PsiClass
	) {
		val dslProperties = getDslMandatoryProperties(dslLintClass)
		val dslPropertiesCalls = getDslMandatoryCallsCount(dslProperties, node.body as UBlockExpression, dslLintClass)

		dslProperties.keys.filter {
			!dslPropertiesCalls.containsKey(it)
		}.forEach {
			reportMissingMandatoryAttribute(dslProperties, it, context, node)
		}
	}

	private fun reportMissingMandatoryAttribute(
		dslPropertiesDefs: Map<String, List<DSLMandatoryAttribute>>,
		group: String,
		context: JavaContext,
		node: ULambdaExpression
	) {
		val message = dslPropertiesDefs[group]?.getOrNull(0)?.message
			?: "\"${group}\" attribute must be defined"
		context.report(
			ISSUE,
			node,
			context.getLocation(node as UElement),
			message,
			getFix(dslPropertiesDefs[group]!!)
		)
	}

	private fun getFix(groupAttributes: List<DSLMandatoryAttribute>): LintFix {
		val group = LintFix.create().group()
		groupAttributes.forEach {
			val setSuffix = if (it.type == Type.PROPERTY) " = " else "{\n}"
			group.add(
				LintFix.create()
					.name("Define \"${it.name}\" attribute")
					.replace()
					.text("{")
					.with("{\n${it.name}$setSuffix")
					.reformat(true)
					.build()
			)
		}

		return group.build()
	}

	private fun getDslMandatoryCallsCount(
		dslProperties: Map<String, List<DSLMandatoryAttribute>>,
		blockBody: UBlockExpression,
		dslLintClass: PsiClass
	): Map<String, Int> {
		val dslPropsCalls = getDslPropertiesCallsCount(dslProperties, blockBody).toMutableMap()
		getExtensionPropertiesDslMandatoryCallsCount(blockBody, dslLintClass, dslProperties).forEach {
			dslPropsCalls += it
		}

		val dslMethodCalls = getDslMandatoryMethodCallsCount(dslProperties, blockBody).toMutableMap()
		getExtensionMethodsDslMandatoryCallsCount(blockBody, dslLintClass, dslProperties).forEach {
			dslMethodCalls += it
		}

		return dslPropsCalls + dslMethodCalls
	}

	private fun getExtensionMethodsDslMandatoryCallsCount(
		blockBody: UBlockExpression,
		dslLintClass: PsiClass,
		dslPropertiesDefs: Map<String, List<DSLMandatoryAttribute>>
	) = blockBody.expressions
		.filterIsInstance<UCallExpression>()
		.filter { it.isMethodCall() }
		.filter {
			it.methodReceiverQualifiedName == dslLintClass.qualifiedName
		}
		.mapNotNull { it.methodBodyBlock }
		.map { methodBodyBlock ->
			getDslMandatoryCallsCount(dslPropertiesDefs, methodBodyBlock, dslLintClass)
		}

	private fun getExtensionPropertiesDslMandatoryCallsCount(
		blockBody: UBlockExpression,
		dslLintClass: PsiClass,
		dslPropertiesDefs: Map<String, List<DSLMandatoryAttribute>>
	) = blockBody.expressions
		.filterIsInstance<UBinaryExpression>()
		.filter { it.isAssignment() }
		.filter {
			it.propertyReceiverQualifiedName == dslLintClass.qualifiedName
		}
		.mapNotNull {
			it.propertySetterBody
		}
		.map { propertySetterBody ->
			getDslMandatoryCallsCount(dslPropertiesDefs, propertySetterBody, dslLintClass)
		}

	// Returns mapping of group name to calls count
	private fun getDslMandatoryMethodCallsCount(
		dslProperties: Map<String, List<DSLMandatoryAttribute>>,
		blockBody: UBlockExpression
	): Map<String, Int> {
		return blockBody.expressions
			.filterIsInstance<UCallExpression>()
			.filter { it.isMethodCall() }
			.mapNotNull {
				getAttributeGroup(getInvokedDslMethodName(it), dslProperties)
			}
			.groupingBy { it }
			.eachCount()
	}

	private fun getInvokedDslMethodName(callExpression: UCallExpression) = callExpression.methodName!!

	// Returns mapping of group name to calls count
	private fun getDslPropertiesCallsCount(
		dslProperties: Map<String, List<DSLMandatoryAttribute>>,
		blockBody: UBlockExpression
	): Map<String, Int> {
		return blockBody.expressions
			.filterIsInstance<UBinaryExpression>()
			.filter { it.isAssignment() }
			.mapNotNull {
				getAttributeGroup(getAssignedDSLAttribute(it), dslProperties)
			}
			.groupingBy { it }
			.eachCount()
	}

	private fun getAssignedDSLAttribute(it: UBinaryExpression) = (((it.leftOperand as UReferenceExpression).referenceNameElement) as UIdentifier).name

	private fun getAttributeGroup(
		attributeName: String,
		dslProperties: Map<String, List<DSLMandatoryAttribute>>
	): String? {
		dslProperties.forEach { (group, properties) ->
			val propertiesName = properties.map { it.name }
			if (propertiesName.contains(attributeName)) {
				return group
			}
		}
		return null
	}

	// Return mapping of group name to mandatory properties, properties with no group are grouped by their name
	private fun getDslMandatoryProperties(clazz: PsiClass): Map<String, List<DSLMandatoryAttribute>> {
		return clazz.allMethods
			.filter {
				it.hasAnnotation(DSLintAnnotation.DslMandatory.name)
			}
			.map {
				createDslMandatoryAttribute(it)
			}
			.groupBy {
				if (!it.group.isNullOrEmpty()) it.group else it.name
			}
	}

	private fun createDslMandatoryAttribute(method: PsiMethod): DSLMandatoryAttribute {
		val annotation = method.getAnnotation(DSLintAnnotation.DslMandatory.name)
		val group = annotation.resolveStringAttributeValue(DSLintAnnotation.DslMandatory.Attributes.group)
			.nullIfEmpty()
		val message = annotation.resolveStringAttributeValue(DSLintAnnotation.DslMandatory.Attributes.message)
			.nullIfEmpty()
		val isPropertySetter = PropertyUtilBase.isSetterName(method.name)
		val type = if (isPropertySetter) Type.PROPERTY else Type.METHOD
		val name = if (isPropertySetter) PropertyUtilBase.getPropertyName(method)!! else method.name
		return DSLMandatoryAttribute(name, type, group, message)
	}
}

data class DSLMandatoryAttribute(
	val name: String,
	val type: Type,
	val group: String?,
	val message: String?
)

enum class Type {
	METHOD, PROPERTY
}