package com.infusion.tenant.groovy.compiler;

import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.grails.compiler.injection.GrailsASTUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.lang.reflect.Modifier;

/**
 * Performs an ast transformation on a class - adds a tenantId property to the subject class.
 */
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class TenantASTTransformation implements ASTTransformation {
// ========================================================================================================================
//    Static Fields
// ========================================================================================================================

    private static final Log LOG = LogFactory.getLog(TenantASTTransformation.class);

// ========================================================================================================================
//    Public Instance Methods
// ========================================================================================================================

    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        for (ASTNode astNode : astNodes) {
            if (astNode instanceof ClassNode) {
                ClassNode classNode = (ClassNode) astNode;
                final boolean hasTenantId = GrailsASTUtils.hasOrInheritsProperty(classNode, "tenantId");
                if (!hasTenantId) {
                    LOG.info("[TenantASTTransformation] Adding property [tenantId] to class [" + classNode.getName() + "]");
                    classNode.addProperty("tenantId", Modifier.PUBLIC, new ClassNode(Integer.class), new ConstantExpression(0), null, null);
                }
            }
        }
    }
}
