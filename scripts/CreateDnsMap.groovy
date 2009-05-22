includeTargets << grailsScript("Init")

target(main: "This will create the default domain class") {
  def ant = new AntBuilder();


  ant.mkdir(dir: 'grails-app/domain/tenant')
  new File("grails-app/domain/tenant/DomainTenantMap.groovy").write('''
package tenant
import com.infusion.tenant.groovy.compiler.MultiTenant

/**
 * Maps domain name to tenantId
 */
@MultiTenant
class DomainTenantMap {

  String domainName
  Integer mappedTenantId

  static constraints = {}


}
''')
}

setDefaultTarget(main)

