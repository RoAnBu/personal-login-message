FROM adoptopenjdk:13-hotspot-bionic

RUN apt-get update && apt-get install -y git

WORKDIR /spigot-build/

RUN curl -o BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/112/artifact/target/BuildTools.jar && java -jar BuildTools.jar

FROM adoptopenjdk:13-jdk-hotspot

WORKDIR /spigot/

COPY --from=0 /spigot-build/spigot-*.jar ./
RUN mv $(find spigot-*.jar) spigot.jar

COPY server-config/eula.txt .
COPY start.sh .
COPY server-config/server.properties .
COPY server-config/spigot.yml .

RUN chown -R 1000:1000 /spigot

ENTRYPOINT ["/spigot/start.sh"]