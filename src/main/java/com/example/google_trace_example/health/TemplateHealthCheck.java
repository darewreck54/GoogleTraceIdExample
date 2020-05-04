package com.example.google_trace_example.health;

import com.codahale.metrics.health.HealthCheck;

import com.example.google_trace_example.models.Template;
import java.util.Optional;

public class TemplateHealthCheck extends HealthCheck {
  private final Template template;

  public TemplateHealthCheck(Template template) {
    this.template = template;
  }

  @Override
  protected Result check() throws Exception {
    template.render(Optional.of("woo"));
    template.render(Optional.empty());
    return Result.healthy();
  }
}
