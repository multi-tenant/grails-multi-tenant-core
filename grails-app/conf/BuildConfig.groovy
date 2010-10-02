/**
 * This module defines the build dependencies for the plugin to support such items as the OSCache functionality, etc.
 */
grails.project.dependency.resolution =
  {
    inherits "global" // inherit Grails' default dependencies
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories
    {
      grailsPlugins()
      grailsHome()
      grailsCentral()
      mavenCentral()
      mavenRepo "http://download.java.net/maven/2/"
      mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies
    {
      // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
      // This allows the plugin to compile.  If the user wants to use the OSCache multi tenant capability they will
      // need to add the jars to their runtime configuration for the application.
    }
  }
