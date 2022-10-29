FROM openjdk:11

RUN wget https://github.com/mulesoft-labs/data-weave-cli/releases/download/v1.0.24/dw-1.0.24-Linux

RUN mv dw-1.0.24-Linux dw-1.0.24-Linux.zip

RUN unzip dw-1.0.24-Linux.zip

ADD ./mule-faas2/target/mule-faas-0.0.1-SNAPSHOT.jar /

ENV PORT=8080
#ADD ./mule-faas2/target/mule-faas-0.0.1-SNAPSHOT.jar /

CMD ["java", "-jar","-Dserver.port=${PORT}", "mule-faas-0.0.1-SNAPSHOT.jar"]