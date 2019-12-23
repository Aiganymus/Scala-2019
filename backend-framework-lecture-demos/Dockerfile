#
# Scala and sbt Dockerfile
#
#
# Pull base image OpenJDK
FROM openjdk:8u232

# copy configs
# this is run inside docker image
RUN mkdir -p /root/config/

COPY ./src/main/resources/*logback.xml /root/config/
COPY ./src/main/resources/*.conf /root/config/

# cd /root
WORKDIR /root
COPY ./target/universal/photo-service-0.1.zip /root/
RUN unzip -q photo-service-0.1.zip
WORKDIR /root/photo-service-0.1/bin

# clean zip
RUN rm /root/photo-service-0.1.zip

# make file executable
CMD chmod +x photo-service

CMD ["/bin/bash", "-c", "./photo-service -Dconfig.file=/root/config/application.conf -Dlogback.configurationFile=/root/config/logback.xml"]