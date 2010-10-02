package grails.plugin.multitenant.core.domain

class DomainTenantMap {

  static constraints = {}
  String domainName
  Integer mappedTenantId
}
