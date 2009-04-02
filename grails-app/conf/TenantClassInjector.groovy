import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.classgen.GeneratorContext
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.grails.commons.GrailsResourceUtils
import org.apache.commons.logging.Log
import org.codehaus.groovy.grails.compiler.injection.GrailsASTUtils
import org.apache.commons.logging.LogFactory
import java.lang.reflect.Modifier
import org.codehaus.groovy.ast.expr.ConstantExpression

/**
 * ClassInjector that injects a "tenantId" field on each domain object.  Used during the compile phase.
 *
 * This class is used by the custom grails that added a hook into the GrailsCompiler class for extensions
 * of ClassInjector.  Because it doesn't exist in the current release of grails (1.1), this class will never
 * do anything.
 *
 * I'm leaving it in the project in case someone wants to use the modded version of grails.  It makes it so
 * you don't have to annotate EVERY domain class.
 */
public class TenantClassInjector implements ClassInjector {

  private static final Log LOG = LogFactory.getLog(TenantClassInjector.class);

  public void performInjection(SourceUnit source, GeneratorContext context, ClassNode classNode) {
    final boolean hasTenantId = GrailsASTUtils.hasOrInheritsProperty(classNode, "tenantId");
    if (!hasTenantId) {
      System.out.println("[GrailsDomainInjector] Adding property [tenantId] to class [" + classNode.getName() + "]");
      classNode.addProperty("tenantId", Modifier.PUBLIC, new ClassNode(Integer.class), new ConstantExpression(0), null, null);
    }
  }


  public boolean shouldInject(URL url) {
    System.out.println("\t[GrailsDomainInjector] " + url + ":" + GrailsResourceUtils.isDomainClass(url))
    return GrailsResourceUtils.isDomainClass(url);
  }
}