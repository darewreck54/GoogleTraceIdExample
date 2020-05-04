package com.example.google_trace_example;

import com.example.google_trace_example.exception.RuntimeExceptionMapper;
import com.example.google_trace_example.filter.TraceLogFilter;
import com.example.google_trace_example.health.TemplateHealthCheck;
import com.example.google_trace_example.models.Template;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import java.util.EnumSet;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import ru.vyarus.dropwizard.guice.GuiceBundle;

public class Main extends Application<ApplicationConfiguration> {
  public static final Set<Class<?>> RESOURCES =
    ImmutableSet.<Class<?>>builder().build();
    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    @Override
    public String getName() {
        return "TemporalCtrlApplication";
    }

    @Override
    public void initialize(Bootstrap<ApplicationConfiguration> bootstrap) {

        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(
                        bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

        bootstrap
          .getObjectMapper()
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);


        bootstrap.addBundle(new SwaggerBundle<ApplicationConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(ApplicationConfiguration configuration) {
                configuration.swaggerBundleConfiguration.setVersion("0.1");
                configuration.swaggerBundleConfiguration.setContact("dhang@snapchat.com");
                configuration.swaggerBundleConfiguration.setDescription("Api used to manage Cool Tool");
                configuration.swaggerBundleConfiguration.setIsPrettyPrint(true);
                // configuration.swaggerBundleConfiguration.setSchemes(new String[] {SwaggerDefinition.Scheme.HTTPS.name(), SwaggerDefinition.Scheme.HTTP.name()});
                return configuration.swaggerBundleConfiguration;
            }
        });


      GuiceBundle guiceBundle = GuiceBundle.builder()
        // scan will find everything
        .enableAutoConfig(this.getClass().getPackage().getName())
        // all three directly registered
        .extensions(RESOURCES.toArray(new Class[0]))
        .modules(new ServerModule())
        .printDiagnosticInfo()
        .build();

        bootstrap.addBundle(guiceBundle);
    }

    @Override
    public void run(ApplicationConfiguration configuration, Environment environment) throws Exception {
        final Template template = configuration.buildTemplate();

      // apply filter to all servlet request.  This will add trace id
       Dynamic filter = environment.servlets().addFilter("TraceLogFilter", TraceLogFilter.class);
       filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
       filter.setInitParameter(TraceLogFilter.GOOGLE_PROJECT_ID_KEY, configuration.getGoogleProjectId());

       environment.jersey().register(new RuntimeExceptionMapper());
        environment.jersey().register(new JsonProcessingExceptionMapper(true));

        environment.healthChecks().register("template", new TemplateHealthCheck(template));
        environment.jersey().register(RolesAllowedDynamicFeature.class);

    }
}
