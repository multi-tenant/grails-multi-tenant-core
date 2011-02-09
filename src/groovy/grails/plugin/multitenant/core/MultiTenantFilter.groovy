package grails.plugin.multitenant.core

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletContext
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

import org.springframework.context.ApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils

/**
 * A filter that uses a tenant resolver to locate the correct tenant for the request.
 */

public class MultiTenantFilter implements Filter {

    private TenantResolver tenantResolver
    private CurrentTenant currentTenant

    public void init(FilterConfig filterConfig) {
    }

    private synchronized void checkInit(HttpServletRequest servletRequest) {
	
        if (tenantResolver == null || currentTenant == null) {
	
            ServletContext servletContext = servletRequest.getSession().getServletContext()
            ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext)
            tenantResolver = ctx.tenantResolver
            currentTenant = ctx.currentTenant
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {

        try {
	
            if (servletRequest instanceof HttpServletRequest) {

                HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest
                checkInit(httpServletRequest)

                Integer tenantId = tenantResolver?.getTenantFromRequest(httpServletRequest)

                // Set the tenant no matter what so we don't end with another user of this thread's tenant even if it is null or 0.
                currentTenant?.set tenantId
            }
            filterChain.doFilter(servletRequest, servletResponse)
            // Unset the currentTenant at the end of the request even if exception was thrown
            // This was not working in all cases and was causing the previous user of this threads tenant?
        } finally {
            currentTenant?.set 0
        }
    }

    public void destroy() {
    }
}