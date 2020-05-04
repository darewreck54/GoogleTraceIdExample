package com.example.google_trace_example;


import com.example.google_trace_example.resources.HelloWorldResource;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;

public class ServerModule extends DropwizardAwareModule<ApplicationConfiguration> {
  @Override
  protected void configure() {
    binder().requireExplicitBindings();
    for (Class<?> clazz : Main.RESOURCES) {
      bind(clazz);
    }

    bind(HelloWorldResource.class).toInstance(new HelloWorldResource(this.configuration().buildTemplate()));
  }
}
