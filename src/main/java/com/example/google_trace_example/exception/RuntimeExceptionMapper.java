package com.example.google_trace_example.exception;

import io.dropwizard.jersey.errors.ErrorMessage;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeExceptionMapper.class);

  @Override
  public Response toResponse(RuntimeException exception) {
    if(exception instanceof WebApplicationException) {
      final Response response = ((WebApplicationException) exception).getResponse();
      if(response.getStatus() < HttpStatus.BAD_REQUEST_400) { // no error
        return Response.status(response.getStatusInfo())
          .type(MediaType.APPLICATION_JSON_TYPE)
          .entity(response)
          .build();
      } else if(response.getStatus() < HttpStatus.INTERNAL_SERVER_ERROR_500) {
        return Response.status(response.getStatusInfo())
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .entity(new ErrorMessage(response.getStatus(), exception.getMessage()))
                        .build();
      }
    }

    return Response.status(Status.INTERNAL_SERVER_ERROR)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .entity(new ErrorMessage(Status.INTERNAL_SERVER_ERROR.getStatusCode(), Status.INTERNAL_SERVER_ERROR.getReasonPhrase()))
          .build();
  }
}
