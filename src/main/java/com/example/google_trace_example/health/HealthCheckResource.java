package com.example.google_trace_example.health;


import com.google.inject.Inject;

import io.swagger.annotations.*;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/appengine")
@Produces(MediaType.APPLICATION_JSON)
@Api(value="/appengine")
public class HealthCheckResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckResource.class);

  @Inject
  public HealthCheckResource() {
  }

  @GET
  @Path("readiness_check")
  public Response readinessCheck(
    @ApiParam(value="name of repo configuration", required=true) @QueryParam("name") String name)
  {
    // validate input
    LOGGER.info("Get Called");
    return Response.ok().build();
  }

  @GET
  @Path("liveness_check")
  public Response livenessCheck()
  {
    return Response.ok().build();
  }
}
