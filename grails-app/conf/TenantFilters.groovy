import com.infusion.tenant.CurrentTenant
import com.infusion.tenant.TenantResolver
import com.infusion.tenant.event.TenantChangedEvent
import com.infusion.util.event.groovy.GroovyEventBroker
import org.codehaus.groovy.grails.commons.ConfigurationHolder


/**
 * Filters used to check for tenancy.
 */
public class TenantFilters {

  TenantResolver tenantResolver
  GroovyEventBroker eventBroker
  CurrentTenant currentTenant

  def filters = {
    //This filter allows you to set the current tenant directly from the request.  Eventually, it will
    //be replaced by something a little better, like an actual login...
    tenantFromRequest(controller: '*', action: '*') {
      before = {
        if (params.__tenantId != null) {
          session.tenantId = Integer.parseInt(params.__tenantId)
        }
      }
    }

    if (ConfigurationHolder.config.tenant.resolver.type == "request") {
      tenantIdentifier(controller: "*", action: "*") {
        before = {
          Integer tenantId = tenantResolver?.getTenantFromRequest(request)
          if (tenantId != null && tenantId > 0) {
            //Set the value in the session
            //session.tenantId = tenantId

            //Set the current tenant in case event handlers
            currentTenant.set tenantId
          }
        }
      }
    }

    tenantStatusCheck(controller: "*", action: "*") {
      before = {

      }
    }

    //This filter binds the current session's tenant with the current executing thread
//    tenantFilter(controller: '*', action: '*') {
//      before = {
//        //Always set to 0 to start
//        currentTenant.set 0
//        if (session.tenantId > 0) {
//          currentTenant.set session.tenantId
//        }
//      }
//
//      after = {
//        //If the current tenant was set anywhere during the execution, pick it up and set it here.
//        //Kinda dangerous, but we had to do it for the acegi integration
//        if (session.tenantId == null || session.tenantId == 0) {
//          session.tenantId = currentTenant.get()
//        }
//
//        //Had to remove this because it was getting called BEFORE sitemesh...  so the sitemesh stuff was bombing
//
//        //TenantUtils.setCurrentTenant 0
//      }
//    }
//  }
  }
}