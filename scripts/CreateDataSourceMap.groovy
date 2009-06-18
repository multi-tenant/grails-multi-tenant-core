includeTargets << grailsScript("Init")

target(main: "This will create the default domain class") {
  def ant = new AntBuilder();


  ant.mkdir(dir: 'grails-app/domain/tenant')
  new File("grails-app/domain/tenant/DataSourceTenantMap.groovy").write('''
package tenant

/**
 * Maps data source name to tenantId
 */
class DataSourceTenantMap {

  String dataSource
  Integer mappedTenantId

  static constraints = {}


}
''')
}

setDefaultTarget(main)

