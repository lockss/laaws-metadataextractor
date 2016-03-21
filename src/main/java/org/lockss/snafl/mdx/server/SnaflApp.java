package org.lockss.snafl.mdx.server;


import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import io.swagger.jaxrs.config.BeanConfig;
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
import org.lockss.snafl.mdx.api.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Main Class for starting the Entity Browser
 */
public class SnaflApp
{
  private static final Logger LOG = LoggerFactory.getLogger( SnaflApp.class );

  public static String SNAFL_SERVER_HOST ="SNAFL_SERVER_HOST";
  public static String SNAFL_SERVER_PORT = "SNAFL_SERVER_PORT";
  private static final int DEFAULT_SERVER_PORT = 8888;
  private static final String DEFAULT_SERVER_HOST = "http://localhost";
  int serverPort;
  String serverHost;

  public static void main( String[] args )
  {

    try {
      new SnaflApp();
    }
    catch (Exception e) {
      LOG.error("Server initialization failed!");
      e.printStackTrace();
    }
  }

  public SnaflApp() throws Exception {

    serverHost = System.getProperty(SNAFL_SERVER_HOST, DEFAULT_SERVER_HOST);
    serverPort =
      Integer.getInteger(System.getProperty(SNAFL_SERVER_PORT), DEFAULT_SERVER_PORT);

    // Build the Swagger data
    buildSwagger(serverHost+":"+serverPort);
    Server server = configureServer();
    server.start();
    server.join();
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

  protected static void buildSwagger(String host)
  {
    // This configures Swagger
    BeanConfig beanConfig = new BeanConfig();
    beanConfig.setVersion( "1.0.2" );
    beanConfig.setResourcePackage("org.lockss.snafl.mdx.api");

    beanConfig.setDescription( "Metadata Service API." );
    beanConfig.setTitle( "Metadata Service" );

    beanConfig.setSchemes(new String[]{"http"});
    beanConfig.setHost(host);
    beanConfig.setBasePath("/docs");
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

    resourceConfig.packages("org.lockss.snafl.mdx.model",
                            "org.lockss.snafl.mdx.api",
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
      SnaflApp.class.getClassLoader().getResource("swaggerui").toURI().toString());
    ContextHandler swaggerUIContextHandler = new ContextHandler();
    swaggerUIContextHandler.setContextPath("/docs/");
    swaggerUIContextHandler.setHandler(swaggerUIResourceHandler);

    return swaggerUIContextHandler;
  }

}