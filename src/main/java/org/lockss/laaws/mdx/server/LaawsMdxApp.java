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
import org.lockss.job.JobDbManager;
import org.lockss.job.JobManager;
import org.lockss.metadata.MetadataDbManager;
import org.lockss.metadata.MetadataManager;
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
      // start plugin manager after generic services
      new ManagerDesc(PLUGIN_MANAGER, "org.lockss.plugin.PluginManager"),
      // start database manager before any manager that uses it.
      new ManagerDesc(MetadataDbManager.getManagerKey(),
	  "org.lockss.metadata.MetadataDbManager"),
      // start metadata manager after pluggin manager and database manager.
      new ManagerDesc(MetadataManager.getManagerKey(),
	  "org.lockss.metadata.MetadataManager"),
      // Start the job manager.
      new ManagerDesc(JobDbManager.getManagerKey(),
	  "org.lockss.job.JobDbManager"),
      new ManagerDesc(JobManager.getManagerKey(), "org.lockss.job.JobManager")
  };

  public static void main( String[] args ) {
    if (log.isDebugEnabled()) log.debug("args = " + Arrays.toString(args));

    LaawsMdxApp laawsMdxApp;

    if (!SystemUtils.isJavaVersionAtLeast(MIN_JAVA_VERSION)) {
      System.err.println("LOCKSS requires at least Java " + MIN_JAVA_VERSION +
                         ", this is " + SystemUtils.JAVA_VERSION +
                         ", exiting.");
      System.exit(Constants.EXIT_CODE_JAVA_VERSION);
    }

    setSystemProperties();

    try {
      if (!USE_REST_WEB_SERVICE.equals(args[0])) {
	StartupOptions opts = getStartupOptions(args);
	if (log.isDebugEnabled()) {
	  log.debug("opts.getPropUrls() = " + opts.getPropUrls());
	  log.debug("opts.getGroupNames() = " + opts.getGroupNames());
	}

	laawsMdxApp = new LaawsMdxApp(opts.getPropUrls(), opts.getGroupNames());
      } else {
	String serviceLocation = args[1];
	if (log.isDebugEnabled())
	  log.debug("serviceLocation = " + serviceLocation);

	String serviceUser = args[2];
	if (log.isDebugEnabled()) log.debug("serviceUser = " + serviceUser);

	String servicePassword = args[3];
	if (log.isDebugEnabled())
	  log.debug("servicePassword = " + servicePassword);

	String serviceTimeout = args[4];
	if (log.isDebugEnabled())
	  log.debug("serviceTimeout = " + serviceTimeout);

	Integer timeoutInSeconds = null;

	if (serviceTimeout != null) {
	  try {
	    // Convert the passed timeout text value to its numeric value.
	    timeoutInSeconds = Integer.valueOf(serviceTimeout);
	    if (log.isDebugEnabled())
	      log.debug("timeoutInSeconds = " + timeoutInSeconds);
	  } catch (NumberFormatException nfe) {
	    log.warn("Invalid service timeout" + serviceTimeout + " ignored.");
	  }
	}

	laawsMdxApp = new LaawsMdxApp(serviceLocation, serviceUser,
	    servicePassword, timeoutInSeconds);
      }

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

    if (log.isDebugEnabled()) log.debug("Done.");
  }

  /**
   * Constructor used to access configuration files.
   * 
   * @param propUrls
   *          A List<String> with the configuration properties URLs.
   * @param groupNames
   *          A String with the group names.
   */
  public LaawsMdxApp(List<String> propUrls, String groupNames) {
    super(propUrls, groupNames);
  }

  /**
   * Constructor used to access the Configuration REST web service.
   *
   * @param serviceLocation
   *          A String with the configuration REST service location.
   * @param serviceUser
   *          A String with the configuration REST service user name.
   * @param servicePassword
   *          A String with the configuration REST service user password.
   * @param serviceTimeout
   *          An Integer with the configuration REST service connection timeout
   *          value.
   */
  public LaawsMdxApp(String serviceLocation, String serviceUser,
      String servicePassword, Integer serviceTimeout) {
    super(serviceLocation, serviceUser, servicePassword, serviceTimeout);
  }

  /**
   * Provides the manager's descriptors.
   * 
   * @return a ManagerDesc[] with the application manager's descriptors.
   */
  protected ManagerDesc[] getManagerDescs() {
    return managerDescs;
  }
}