FROM gcr.io/google_appengine/openjdk8
VOLUME /tmp
ADD build/libs/google_trace_example-1.0-SNAPSHOT-all.jar build/libs/google_trace_example-1.0-SNAPSHOT-all.jar
COPY config/dropWizardConfig-stage.yaml config/dropWizardConfig.yaml
CMD ["java","-jar","build/libs/google_trace_example-1.0-SNAPSHOT-all.jar", "server", "config/dropWizardConfig.yaml"]
