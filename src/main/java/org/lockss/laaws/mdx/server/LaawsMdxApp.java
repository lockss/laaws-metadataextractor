/*

 Copyright (c) 2016 Board of Trustees of Leland Stanford Jr. University,
 all rights reserved.

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 STANFORD UNIVERSITY BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 Except as contained in this notice, the name of Stanford University shall not
 be used in advertising or otherwise to promote the sale, use or other dealings
 in this Software without prior written authorization from Stanford University.

 */
package org.lockss.laaws.mdx.server;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import io.swagger.jaxrs.config.BeanConfig;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.lockss.app.LockssDaemon;
import org.lockss.config.CurrentConfig;
import org.lockss.daemon.ResourceUnavailableException;
import org.lockss.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Class for starting the Entity Browser
 */
public class LaawsMdxApp extends LockssDaemon {
  private static final Logger LOG = LoggerFactory.getLogger( LaawsMdxApp.class );

  public static String LAAWS_MDX_SERVER_HOST ="LAAWS_MDX_SERVER_HOST";
  public static String LAAWS_MDX_SERVER_PORT = "LAAWS_MDX_SERVER_PORT";
  private static final int DEFAULT_SERVER_PORT = 8888;
  private static final String DEFAULT_SERVER_HOST = "http://localhost";
  int serverPort;
  String serverHost;

  public static void main( String[] args ) {
	LaawsMdxApp laawsMdxApp;
    if (!SystemUtils.isJavaVersionAtLeast(MIN_JAVA_VERSION)) {
      System.err.println("LOCKSS requires at least Java " + MIN_JAVA_VERSION +
                         ", this is " + SystemUtils.JAVA_VERSION +
                         ", exiting.");
      System.exit(Constants.EXIT_CODE_JAVA_VERSION);
    }

    StartupOptions opts = getStartupOptions(args);
    setSystemProperties();

    try {
      laawsMdxApp = new LaawsMdxApp(opts.getPropUrls(), opts.getGroupNames());
      laawsMdxApp.startDaemon();
      // raise priority after starting other threads, so we won't get
      // locked out and fail to exit when told.
      Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 2);

    } catch (ResourceUnavailableException e) {
      LOG.error("Exiting because required resource is unavailable", e);
      System.exit(Constants.EXIT_CODE_RESOURCE_UNAVAILABLE);
      return;                           // compiler doesn't know that
                                        // System.exit() doesn't return
    } catch (Throwable e) {
      LOG.error("Exception thrown in main loop", e);
      System.exit(Constants.EXIT_CODE_EXCEPTION_IN_MAIN);
      return;                           // compiler doesn't know that
                                        // System.exit() doesn't return
    }
    if (CurrentConfig.getBooleanParam(PARAM_APP_EXIT_IMM,
                                      DEFAULT_APP_EXIT_IMM)) {
      try {
	laawsMdxApp.stop();
      } catch (RuntimeException e) {
        // ignore errors stopping daemon
      }
      System.exit(Constants.EXIT_CODE_NORMAL);
    }
    laawsMdxApp.keepRunning();
    LOG.info("Exiting because time to die");
    System.exit(Constants.EXIT_CODE_NORMAL);
  }

  public LaawsMdxApp(List<String> propUrls, String groupNames)
		  throws Exception {
    super(propUrls, groupNames);

    serverHost = System.getProperty(LAAWS_MDX_SERVER_HOST, DEFAULT_SERVER_HOST);
    serverPort = Integer.getInteger(System.getProperty(LAAWS_MDX_SERVER_PORT),
	DEFAULT_SERVER_PORT);

    // Build the Swagger data
    buildSwagger(serverHost+":"+serverPort);
    Server server = configureServer();
    server.start();
  }

  protected Server configureServer() throws Exception {
    Resource.setDefaultUseCaches( false );

    URI baseUri = UriBuilder.fromUri(serverHost).port(serverPort).build();

    HandlerList handlers = initHandlers(baseUri);

    // configure server
    Server server = new Server(serverPort);
    server.setHandler( handlers );
    return server;
  }

  protected static void buildSwagger(String host) {
    // This configures Swagger
    BeanConfig beanConfig = new BeanConfig();
    beanConfig.setVersion( "1.0.2" );
    beanConfig.setResourcePackage("org.lockss.laaws.mdx.api");

    beanConfig.setDescription( "Metadata Service API." );
    beanConfig.setTitle( "Metadata Service" );

    beanConfig.setSchemes(new String[]{"http"});
    //beanConfig.setHost(host);
//    beanConfig.setBasePath("/docs");
    beanConfig.setScan(true);
  }

  protected static HandlerList initHandlers(URI baseUri) throws Exception {
    // init handlers
    final HandlerList handlers = new HandlerList();

    // Handler for swagger UI, static handler.
    ContextHandler swaggerUIContextHandler = buildSwaggerUIContextHandler();
    handlers.addHandler(swaggerUIContextHandler);

    // Handler for load jersey resources.
    ContextHandler resourceContextHandler = buildResourceContextHandler();
    handlers.addHandler(resourceContextHandler);
    return handlers;
  }

  private static ContextHandler buildResourceContextHandler() {
    ResourceConfig resourceConfig = new ResourceConfig();

    resourceConfig.packages("org.lockss.laaws.mdx.model",
                            "org.lockss.laaws.mdx.api",
                            "io.swagger.jaxrs.listing");
    resourceConfig.register(JacksonFeature.class);
    //resourceConfig.register(NotFoundException.class);

    ServletContainer servletContainer = new ServletContainer(resourceConfig);
    ServletHolder servletHolder = new ServletHolder(servletContainer);

    servletHolder.setInitOrder(1);
    servletHolder.setInitParameter("jersey.config.server.tracing", "ALL");

    ServletContextHandler servletContextHandler =
      new ServletContextHandler(ServletContextHandler.SESSIONS);
    servletContextHandler.setSessionHandler(new SessionHandler());
    servletContextHandler.setContextPath("/");

    servletContextHandler.addServlet(servletHolder, "/*");

    return servletContextHandler;
  }

  private static ContextHandler buildSwaggerUIContextHandler() throws Exception {
    final ResourceHandler swaggerUIResourceHandler = new ResourceHandler();
    swaggerUIResourceHandler.setResourceBase(
      LaawsMdxApp.class.getClassLoader().getResource("swaggerui").toURI()
      .toString());
    ContextHandler swaggerUIContextHandler = new ContextHandler();
    swaggerUIContextHandler.setContextPath("/docs/");
    swaggerUIContextHandler.setHandler(swaggerUIResourceHandler);

    return swaggerUIContextHandler;
  }
}