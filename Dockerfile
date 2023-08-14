# used base image with pre-installed software
FROM maven.kriegerit.de:18444/krieger/infrastructure/docker-images/base/ubuntu2004

RUN  curl https://adoptopenjdk.jfrog.io/adoptopenjdk/api/gpg/key/public | apt-key add - && \
    add-apt-repository --yes https://adoptopenjdk.jfrog.io/adoptopenjdk/deb/ && \
    apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install --assume-yes --fix-broken --fix-missing --no-install-recommends adoptopenjdk-11-hotspot-jre fontconfig fonts-dejavu build-essential xorg libssl-dev libxrender-dev wget xfonts-75dpi && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN wget https://github.com/wkhtmltopdf/packaging/releases/download/0.12.6-1/wkhtmltox_0.12.6-1.focal_amd64.deb
RUN apt install ./wkhtmltox_0.12.6-1.focal_amd64.deb
# creates a non priveliged user "java"
RUN addgroup --system java && adduser --system java --ingroup java


# set container user to "java" instead of using "root"
USER java

# command line to execute the Spring Boot App at "docker run" 
CMD ["/usr/bin/java", "-jar", "/usr/share/java-app/java-app.jar"]

# build parameter to reference the created build artifact (jar) 
ARG JAR_FILE
# copies referenced build artifact (jar) into the container 
COPY ${JAR_FILE} /usr/share/java-app/java-app.jar
