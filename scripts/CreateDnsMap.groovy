includeTargets << grailsScript("Init")

target(main: "This will create the default domain class") {
  def ant = new AntBuilder();


  ant.mkdir(dir: 'grails-app/domain/tenant')
  new File("grails-app/domain/tenant/DomainTenantMap.groovy").write('''
package tenant

/**
 * Maps domain name to tenantId
 */
class DomainTenantMap {
  String domainName
  Integer mappedTenantId
  String name
  static constraints = {}


}
''')
}

setDefaultTarget(main)

