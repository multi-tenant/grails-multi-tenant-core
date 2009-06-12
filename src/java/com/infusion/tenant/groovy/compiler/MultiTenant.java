package com.infusion.tenant.groovy.compiler;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

/**
 * Annotation used to mark domain classes that should be converted to multi-tenant.
 *
 * Currently, this annotation will add a tenantId property to the class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@GroovyASTTransformationClass("com.infusion.tenant.groovy.compiler.TenantASTTransformation")
public @interface MultiTenant {
}
