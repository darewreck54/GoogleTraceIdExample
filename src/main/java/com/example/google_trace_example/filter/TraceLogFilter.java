package com.example.google_trace_example.filter;

import com.google.cloud.logging.TraceLoggingEnhancer;
import java.io.IOException;
import java.util.UUID;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class TraceLogFilter implements Filter {
  // Taken from https://cloud.google.com/appengine/docs/flexible/python/reference/request-headers
  public static final String GOOGLE_PROJECT_ID_KEY = "GOOGLE_PROJECT_ID_KEY";

  // Taken from https://cloud.google.com/appengine/docs/flexible/python/reference/request-headers
  // StackDriver will utilize this header to group the logs together
  private static final String X_CLOUD_TRACE = "X-Cloud-Trace-Context";
  private FilterConfig filterConfig;

  @Override
  public void init(final FilterConfig filterConfig) {
    this.filterConfig = filterConfig;
  }

  /**
   * This method will append a trace id to the request. It will see if there is an existing trace id
   * and if not, it will generate one.
   *
   * @param request
   * @param response
   * @param chain
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public void doFilter(
    final ServletRequest request, final ServletResponse response, final FilterChain chain)
    throws IOException, ServletException {
    // Attempt to find a stack driver trace id to map to the Nginx log
    String traceId = (String) request.getAttribute(X_CLOUD_TRACE);
    if (traceId == null && request instanceof HttpServletRequest) {
      HttpServletRequest httpRequest = (HttpServletRequest) request;
      traceId = httpRequest.getHeader(X_CLOUD_TRACE);
      if (traceId != null) {
        // Get rid of the garbage after the first slash
        // EX: c3d69f17521a87095454e6e05bef5f29/f3d69f17521a87095454e6e05bef5f29 and
        //     we only want c3d69f17521a87095454e6e05bef5f29
        int slash = traceId.indexOf('/');
        if (slash >= 0) {
          traceId = traceId.substring(0, slash);
        }
      } else {
        traceId = UUID.randomUUID().toString();
      }
      request.setAttribute(X_CLOUD_TRACE, traceId);
    } else {
      // otherwise pick a random one for this request
      traceId = UUID.randomUUID().toString();
    }

    TraceLoggingEnhancer.setCurrentTraceId(
      "projects/" + filterConfig.getInitParameter(GOOGLE_PROJECT_ID_KEY) + "/traces/" + traceId);
    log.debug("projects/" + filterConfig.getInitParameter(GOOGLE_PROJECT_ID_KEY) + "/traces/" + traceId);
    chain.doFilter(request, response);
    TraceLoggingEnhancer.setCurrentTraceId(null);
  }

  @Override
  public void destroy() {}
}
