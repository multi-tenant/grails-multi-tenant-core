package com.infusion.tenant

import javax.servlet.Filter
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.FilterChain
import org.springframework.web.context.support.WebApplicationContextUtils
import javax.servlet.http.HttpServletRequest
import org.springframework.web.context.WebApplicationContext
import javax.servlet.ServletContext
import org.springframework.context.ApplicationContext

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
      ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
      tenantResolver = ctx.tenantResolver
      currentTenant = ctx.currentTenant
    }
  }

  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
    if (servletRequest instanceof HttpServletRequest) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest
      checkInit(httpServletRequest)
      Integer tenantId = tenantResolver?.getTenantFromRequest(httpServletRequest)
      if (tenantId != null && tenantId > 0) {
        currentTenant?.set tenantId
      }
    }
    filterChain.doFilter(servletRequest, servletResponse)
    //Unset the currentTenant at the end of the request
    currentTenant?.set 0
  }

  public void destroy() {
  }
}