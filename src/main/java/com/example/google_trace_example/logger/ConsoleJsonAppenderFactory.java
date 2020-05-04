package com.example.google_trace_example.logger;

import ch.qos.logback.classic.Level;
import com.google.cloud.logging.logback.LoggingAppender;
import io.dropwizard.logging.AbstractAppenderFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.async.AsyncAppenderFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.layout.LayoutFactory;

@JsonTypeName("cloud-stackdriver")
public class ConsoleJsonAppenderFactory extends AbstractAppenderFactory<ILoggingEvent> {

  private String appenderName = "cloud-stackdriver-appender";
  private boolean includeContextName = true;

  @JsonProperty
  public String getName() {
    return this.appenderName;
  }

  @JsonProperty
  public void setName(String name) {
    this.appenderName = name;
  }

  @Override
  public Appender<ILoggingEvent> build(LoggerContext context, String applicationName, LayoutFactory<ILoggingEvent> layoutFactory,
    LevelFilterFactory<ILoggingEvent> levelFilterFactory, AsyncAppenderFactory<ILoggingEvent> asyncAppenderFactory) {
    setNeverBlock(true);

    LoggingAppender cloudAppender = new LoggingAppender();
    cloudAppender.addEnhancer("com.google.cloud.logging.TraceLoggingEnhancer");
    cloudAppender.addFilter(levelFilterFactory.build(Level.toLevel(getThreshold())));
    cloudAppender.setFlushLevel(Level.ERROR);

    // for this to work properly, it needs to be configured to application.log
    cloudAppender.setLog("application.log");

    cloudAppender.setName(appenderName);
    cloudAppender.setContext(context);
    cloudAppender.start();

    return wrapAsync(cloudAppender, asyncAppenderFactory);
  }
}