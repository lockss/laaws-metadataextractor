/*

 Copyright (c) 2016-2017 Board of Trustees of Leland Stanford Jr. University,
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

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.SystemUtils;
import org.lockss.app.LockssDaemon;
import org.lockss.config.CurrentConfig;
import org.lockss.daemon.ResourceUnavailableException;
import org.lockss.rs.status.ApiStatus;
import org.lockss.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Startup code.
 */
public class LaawsMdxApp extends LockssDaemon {
  public static final String USE_REST_WEB_SERVICE = "Use REST Web Service";
  private static final Logger log = LoggerFactory.getLogger(LaawsMdxApp.class);

  // Manager descriptors.  The order of this table determines the order in
  // which managers are initialized and started.
  protected final ManagerDesc[] managerDescs = {
      new ManagerDesc(RESOURCE_MANAGER, DEFAULT_RESOURCE_MANAGER),
      new ManagerDesc(ALERT_MANAGER, "org.lockss.alert.AlertManagerImpl"),
      new ManagerDesc(STATUS_SERVICE, DEFAULT_STATUS_SERVICE),
      // keystore manager must be started before any others that need to
      // access managed keystores
      new ManagerDesc(KEYSTORE_MANAGER,
	  "org.lockss.daemon.LockssKeyStoreManager"),
      new ManagerDesc(ACCOUNT_MANAGER, "org.lockss.account.AccountManager"),
      new ManagerDesc(CRAWL_MANAGER, "org.lockss.crawler.CrawlManagerImpl"),
      // start plugin manager after generic services
      new ManagerDesc(PLUGIN_MANAGER, "org.lockss.plugin.PluginManager"),
      // start database manager before any manager that uses it.
      new ManagerDesc(METADATA_DB_MANAGER,
	  "org.lockss.metadata.MetadataDbManager"),
      // start metadata manager after pluggin manager and database manager.
      new ManagerDesc(METADATA_MANAGER, "org.lockss.metadata.MetadataManager"),
      // Start the job manager.
      new ManagerDesc(JOB_DB_MANAGER,"org.lockss.job.JobDbManager"),
      new ManagerDesc(JOB_MANAGER, "org.lockss.job.JobManager"),
      // Start the COUNTER reports manager.
      new ManagerDesc(COUNTER_REPORTS_MANAGER,
	  "org.lockss.exporter.counter.CounterReportsManager"),
      // NOTE: Any managers that are needed to decide whether a servlet is to be
      // enabled or not (through ServletDescr.isEnabled()) need to appear before
      // the AdminServletManager on the next line.
      new ManagerDesc(SERVLET_MANAGER, "org.lockss.servlet.AdminServletManager")
  };

  private static final String API_VERSION = "1.0.0";

  // Representation of the status of the REST web service.
  private static ApiStatus apiStatus = new ApiStatus();

  public static void main( String[] args ) {
    if (log.isDebugEnabled()) log.debug("args = " + Arrays.toString(args));

    LaawsMdxApp laawsMdxApp;

    if (!SystemUtils.isJavaVersionAtLeast(MIN_JAVA_VERSION)) {
      System.err.println("LOCKSS requires at least Java " + MIN_JAVA_VERSION +
                         ", this is " + SystemUtils.JAVA_VERSION +
                         ", exiting.");
      System.exit(Constants.EXIT_CODE_JAVA_VERSION);
    }

    // Populate the API version for this REST web service.
    apiStatus.setVersion(API_VERSION);

    setSystemProperties();

    try {
      StartupOptions opts = getStartupOptions(args);
      if (log.isDebugEnabled()) {
	log.debug("opts.getBootstrapPropsUrl() = "
	    + opts.getBootstrapPropsUrl());
	log.debug("opts.getRestConfigServiceUrl() = "
	    + opts.getRestConfigServiceUrl());
	log.debug("opts.getPropUrls() = " + opts.getPropUrls());
	log.debug("opts.getGroupNames() = " + opts.getGroupNames());
      }

      laawsMdxApp = new LaawsMdxApp(opts.getBootstrapPropsUrl(),
	  opts.getRestConfigServiceUrl(), opts.getPropUrls(),
	  opts.getGroupNames());

      laawsMdxApp.startDaemon();

      // Install loadable plugin support
      laawsMdxApp.getPluginManager().startLoadablePlugins();

      // raise priority after starting other threads, so we won't get
      // locked out and fail to exit when told.
      Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 2);
    } catch (ResourceUnavailableException e) {
      log.error("Exiting because required resource is unavailable", e);
      System.exit(Constants.EXIT_CODE_RESOURCE_UNAVAILABLE);
      return;   // compiler doesn't know that System.exit() doesn't return.
    } catch (Throwable e) {
      log.error("Exception thrown in main loop", e);
      System.exit(Constants.EXIT_CODE_EXCEPTION_IN_MAIN);
      return;   // compiler doesn't know that System.exit() doesn't return.
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

    // The REST web service is ready at this point.
    apiStatus.setReady(Boolean.TRUE);

    if (log.isDebugEnabled()) log.debug("Done.");
  }

  /**
   * Constructor used to access configuration files.
   * 
   * @param bootstrapPropsUrl
   *          A String with the bootstrap configuration properties URL.
   * @param restConfigServiceUrl
   *          A String with the REST configuration service URL.
   * @param propUrls
   *          A List<String> with the configuration properties URLs.
   * @param groupNames
   *          A String with the group names.
   */
  public LaawsMdxApp(String bootstrapPropsUrl, String restConfigServiceUrl,
      List<String> propUrls, String groupNames) {
    super(bootstrapPropsUrl, restConfigServiceUrl, propUrls, groupNames);
  }

  /**
   * Provides the manager's descriptors.
   * 
   * @return a ManagerDesc[] with the application manager's descriptors.
   */
  protected ManagerDesc[] getManagerDescs() {
    return managerDescs;
  }

  /**
   * Provides the status of the REST web service.
   * 
   * @return an ApiStatus with the status of the REST web service.
   */
  public static ApiStatus getApiStatus() {
    return apiStatus;
  }
}