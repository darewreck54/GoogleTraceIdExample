package com.example.google_trace_example;

import com.example.google_trace_example.models.Template;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import javax.validation.constraints.NotEmpty;

public class ApplicationConfiguration extends io.dropwizard.Configuration {
  @JsonProperty("swagger")
  public SwaggerBundleConfiguration swaggerBundleConfiguration;

  @NotEmpty
  private String template;

  @NotEmpty
  private String defaultName = "Stranger";

  @NotEmpty
  private String environment;

  public String getGoogleProjectId() {
    return googleProjectId;
  }

  public void setGoogleProjectId(String googleProjectId) {
    this.googleProjectId = googleProjectId;
  }

  @NotEmpty
  private String googleProjectId;

  @JsonProperty
  public String getTemplate() {
    return template;
  }

  @JsonProperty
  public void setTemplate(String template) {
    this.template = template;
  }

  @JsonProperty
  public String getDefaultName() {
    return defaultName;
  }

  @JsonProperty
  public void setDefaultName(String defaultName) {
    this.defaultName = defaultName;
  }

  public Template buildTemplate() {
    return new Template(template, defaultName, environment);
  }

  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }
}
