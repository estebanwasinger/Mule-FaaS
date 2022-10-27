FROM openjdk:11

RUN wget https://github.com/mulesoft-labs/data-weave-cli/releases/download/v1.0.24/dw-1.0.24-Linux

RUN mv dw-1.0.24-Linux dw-1.0.24-Linux.zip

RUN unzip dw-1.0.24-Linux.zip

ENV PATH="${PATH}:/dw-1.0.25-Linux/bin"

RUN wget https://github.com/estebanwasinger/Mule-FaaS/releases/download/0.0.2/mule-faas-0.0.2-SNAPSHOT.jar

ENV PORT=8081
#ADD ./mule-faas2/target/mule-faas-0.0.1-SNAPSHOT.jar /

CMD ["java", "-jar","-Dserver.port=${PORT}", "mule-faas-0.0.2-SNAPSHOT.jar"]