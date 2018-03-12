FROM alpine
RUN apk --update add openjdk8-jre
EXPOSE 8080

ENV KUBE_LATEST_VERSION="v1.8.0"
ENV HELM_LATEST_VERSION="v2.6.2"
ENV DRAFT_LATEST_VERSION="v0.10.1"

WORKDIR "/opt/azure-vmtest/"
COPY target/vmcr8tester-1.0.jar /opt/azure-vmtest/
#RUN apk add maven
ENV JAVA_HOME=/usr/lib/jvm/default-jvm/jre/
ENTRYPOINT ["java","-jar","vmcr8tester-1.0.jar"]
