/*
 * Copyright (c) 2010. All rights reserved.
 */
package ro.isdc.wro.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.isdc.wro.WroRuntimeException;
import ro.isdc.wro.config.jmx.WroConfiguration;
import ro.isdc.wro.http.WroFilter;
import ro.isdc.wro.http.support.FieldsSavingRequestWrapper;
import ro.isdc.wro.model.resource.ResourceType;


/**
 * Holds the properties related to a request cycle.
 *
 * @author Alex Objelean
 */
public class Context {
  private static final Logger LOG = LoggerFactory.getLogger(Context.class);
  /**
   * Maps correlationId with a Context.
   */
  private static final Map<String, Context> CONTEXT_MAP = Collections.synchronizedMap(new HashMap<String, Context>());
  /**
   * Holds a correlationId, created in {@link WroFilter}. A correlationId will be associated with a {@link Context}
   * object.
   */
  private static ThreadLocal<String> CORRELATION_ID = new ThreadLocal<String>();
  private WroConfiguration config;
  /**
   * Request.
   */
  private transient HttpServletRequest request;
  /**
   * Response.
   */
  private transient HttpServletResponse response;
  /**
   * ServletContext.
   */
  private transient ServletContext servletContext;
  /**
   * FilterConfig.
   */
  private transient FilterConfig filterConfig;
  /**
   * The path to the folder, relative to the root, used to compute rewritten image url.
   */
  private String aggregatedFolderPath;


  /**
   * @return {@link WroConfiguration} singleton instance.
   */
  public WroConfiguration getConfig() {
    return config;
  }


  /**
   * DO NOT CALL THIS METHOD UNLESS YOU ARE SURE WHAT YOU ARE DOING.
   * <p/>
   * sets the {@link WroConfiguration} singleton instance.
   */
  public void setConfig(final WroConfiguration config) {
    this.config = config;
  }


  /**
   * A context useful for running in web context (inside a servlet container).
   */
  public static Context webContext(final HttpServletRequest request, final HttpServletResponse response,
    final FilterConfig filterConfig) {
    return new Context(request, response, filterConfig);
  }


  /**
   * A context useful for running in non web context (standalone applications).
   */
  public static Context standaloneContext() {
    return new Context();
  }


  /**
   * @return {@link Context} associated with CURRENT request cycle.
   */
  public static Context get() {
    validateContext();
    final String correlationId = CORRELATION_ID.get();
    LOG.debug("get Context for correlationId: {}", correlationId);
    return CONTEXT_MAP.get(correlationId);
  }

  /**
   * @return true if the call is done during wro4j request cycle. In other words, if the context is set.
   */
  public static boolean isContextSet() {
    return CORRELATION_ID.get() != null && CONTEXT_MAP.get(CORRELATION_ID.get()) != null;
  }


  /**
   * Checks if the {@link Context} is accessible from current request cycle.
   */
  private static void validateContext() {
    if (!isContextSet()) {
      throw new WroRuntimeException("No context associated with CURRENT request cycle!");
    }
  }

  /**
   * Set a context with default configuration to current thread.
   */
  public static void set(final Context context) {
    set(context, new WroConfiguration());
  }


  /**
   * Associate a context with the CURRENT request cycle.
   *
   * @param context {@link Context} to set.
   */
  public static void set(final Context context, final WroConfiguration config) {
    Validate.notNull(context);
    Validate.notNull(config);
    context.setConfig(config);

    final String correlationId = generateCorrelationId();
    CORRELATION_ID.set(correlationId);
    CONTEXT_MAP.put(correlationId, context);
  }
  
  /**
   * @return a string representation of an unique id used to store Context in a map.
   */
  private static String generateCorrelationId() {
    return UUID.randomUUID().toString();
  }


  /**
   * Remove context from the local thread.
   */
  public static void unset() {
    final String correlationId = CORRELATION_ID.get();
    if (correlationId != null) {
      CONTEXT_MAP.remove(correlationId);
    }
    CORRELATION_ID.remove();
  }


  /**
   * Private constructor. Used to build {@link StandAloneContext}.
   */
  private Context() {}


  /**
   * Constructor.
   */
  private Context(final HttpServletRequest request, final HttpServletResponse response, final FilterConfig filterConfig) {
    //TODO check if decorating is necessary
    this.request = new FieldsSavingRequestWrapper(request);
    this.response = response;
    this.servletContext = filterConfig != null ? filterConfig.getServletContext() : null;
    this.filterConfig = filterConfig;
  }


  /**
   * @return the request
   */
  public HttpServletRequest getRequest() {
    return this.request;
  }


  /**
   * @return the response
   */
  public HttpServletResponse getResponse() {
    return this.response;
  }


  /**
   * @return the servletContext
   */
  public ServletContext getServletContext() {
    return this.servletContext;
  }


  /**
   * @return the filterConfig
   */
  public FilterConfig getFilterConfig() {
    return this.filterConfig;
  }


  /**
   * @return the aggregatedFolderPath
   */
  public String getAggregatedFolderPath() {
    return this.aggregatedFolderPath;
  }

  /**
   * This field is useful only for the aggregated resources of type {@link ResourceType#CSS}. </br>The
   * aggregatedFolderPath is used to compute the depth. For example, if aggregatedFolder is "wro" then the depth is 1
   * and the path used to prefix the image url is <code>".."</code>. If the aggregatedFolder is "css/aggregated", the
   * depth is 2 and the prefix is <code>"../.."</code>. The name of the aggregated folder is not important, it is used
   * only to compute the depth.
   *
   * @param aggregatedFolderPath the aggregatedFolderPath to set
   */
  public void setAggregatedFolderPath(final String aggregatedFolderPath) {
    this.aggregatedFolderPath = aggregatedFolderPath;
  }


  /**
   * Perform context clean-up.
   */
  public static void destroy() {
    unset();
    //remove all context objects stored in map
    CONTEXT_MAP.clear();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  /**
   * Set the correlationId to the current thread.
   */
  public static void setCorrelationId(final String correlationId) {
    Validate.notNull(correlationId);
    CORRELATION_ID.set(correlationId);
  }
  
  /**
   * Remove the correlationId from the current thread. This operation will not remove the {@link Context} associated
   * with the correlationId. In order to remove context, call {@link Context#unset()}.
   * <p/>
   * Unsetting correlationId is useful when you create child threads which needs to access the correlationId from the
   * parent thread. This simulates the {@link InheritableThreadLocal} functionality.
   */
  public static void unsetCorrelationId() {
    CORRELATION_ID.remove();
  }
  
  /**
   * @return the correlationId associated with this thread.
   */
  public static String getCorrelationId() {
    validateContext();
    return CORRELATION_ID.get();
  }
}
