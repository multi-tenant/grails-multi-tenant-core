includeTargets << grailsScript("Init")

target(main: "This will create the default domain class") {
  def ant = new AntBuilder();

  ant.mkdir(dir: 'grails-app/domain/tenant')
  ant.copy(file: 'scripts/resources/DomainTenantMap.tocopy', tofile: 'grails-app/domain/tenant/DomainTenantMap.groovy')

}

setDefaultTarget(main)
