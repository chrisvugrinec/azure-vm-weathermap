FROM alpine
RUN apk --update add openjdk8-jre
EXPOSE 8080
WORKDIR "/opt/azure-vmtest/"
COPY target/vmcr8tester-1.0.jar /opt/azure-vmtest/
#RUN apk add maven
ENV JAVA_HOME=/usr/lib/jvm/default-jvm/jre/
ENTRYPOINT ["java","-jar","vmcr8tester-1.0.jar"]
