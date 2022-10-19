FROM openjdk:11

RUN wget https://github.com/mulesoft-labs/data-weave-cli/releases/download/v1.0.25/dw-1.0.25-Linux

RUN mv dw-1.0.25-Linux dw-1.0.25-Linux.zip

RUN unzip dw-1.0.25-Linux.zip

ENV PATH="${PATH}:/dw-1.0.25-Linux/bin"

RUN wget https://github.com/estebanwasinger/Mule-FaaS/releases/download/0.0.1/mule-faas-0.0.1-SNAPSHOT.jar

#ADD ./mule-faas2/target/mule-faas-0.0.1-SNAPSHOT.jar /

CMD ["java", "-jar", "mule-faas-0.0.1-SNAPSHOT.jar"]