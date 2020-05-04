package com.example.google_trace_example.models;

import java.util.Optional;

import static java.lang.String.format;

public class Template {
  private final String content;
  private final String defaultName;
  private final String environment;

  public Template(String content, String defaultName, String environment) {
    this.content = content;
    this.defaultName = defaultName;
    this.environment = environment;
  }

  public String render(Optional<String> name) {
    return format(content, name.orElse(defaultName), environment);
  }
}
