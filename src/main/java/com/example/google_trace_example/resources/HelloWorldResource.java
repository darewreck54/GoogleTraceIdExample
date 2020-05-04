package com.example.google_trace_example.resources;

import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.example.google_trace_example.api.Saying;
import com.example.google_trace_example.models.Template;
import com.google.cloud.logging.TraceLoggingEnhancer;
import io.dropwizard.jersey.caching.CacheControl;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.dropwizard.jersey.params.DateTimeParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
@Api(value="/hello-world")
public class HelloWorldResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldResource.class);

  private final Template template;
  private final AtomicLong counter;

  public HelloWorldResource(Template template) {
    this.template = template;
    this.counter = new AtomicLong();
  }

  @GET
  @Timed(name = "get-requests-timed")
  @Metered(name = "get-requests-metered")
  @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
  @Path("{name}")
  @ApiOperation(
    value="This method will retrieve the repository configuration by ID",
    notes="Returns a single repository configuration.  Query params should be unique identifiers.")
  @ApiResponses(value = {
    @ApiResponse(code = 400, message = "Invalid ID supplied", response = ErrorMessage.class),
    @ApiResponse(code = 404, message = "Repo Configuration cannot be found with the specified parameter", response = ErrorMessage.class)
  })
  @Produces(MediaType.APPLICATION_JSON)
  public Saying sayHello(@QueryParam("name") Optional<String> name) {
    return new Saying(counter.incrementAndGet(), template.render(name));
  }

  @POST
  public void receiveHello(Saying saying) {
    LOGGER.info("Received a saying: {}", saying);
  }

  @GET
  @Path("/date")
  @Produces(MediaType.TEXT_PLAIN)
  public String receiveDate(@QueryParam("date") Optional<DateTimeParam> dateTimeParam) {
    if (dateTimeParam.isPresent()) {
      final DateTimeParam actualDateTimeParam = dateTimeParam.get();
      LOGGER.info("Received a date: {}", actualDateTimeParam);
      return actualDateTimeParam.get().toString();
    } else {
      LOGGER.info("TraceID: {}", TraceLoggingEnhancer.getCurrentTraceId());
      LOGGER.warn("No received date");
      return null;
    }
  }
}
