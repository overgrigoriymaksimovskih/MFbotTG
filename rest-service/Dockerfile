FROM maven:3.8-openjdk-17-slim

ENV HOME=/home/app
RUN mkdir -p $HOME

ADD ./settings.xml /usr/share/maven/ref/
ENV MVN_SETTINGS=/usr/share/maven/ref/settings.xml

RUN mkdir -p $HOME/shared-db-migrations
ADD ./shared-db-migrations $HOME/shared-db-migrations
RUN mvn -f $HOME/shared-db-migrations/pom.xml -s $MVN_SETTINGS clean package
RUN mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=$HOME/shared-db-migrations/target/shared-db-migrations-0.0.1.jar \
    -DgroupId=pro.masterfood \
    -DartifactId=shared-db-migrations \
    -Dversion=0.0.1 \
    -Dpackaging=jar

RUN mkdir -p $HOME/common
ADD ./common $HOME/common
RUN mvn -f $HOME/common/pom.xml -s $MVN_SETTINGS clean package
RUN mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=$HOME/common/target/common-0.0.1.jar --debug

RUN mkdir -p $HOME/rest-service
ADD ./rest-service/pom.xml $HOME/rest-service
ADD ./rest-service/src $HOME/rest-service/src
#-----------------------------------------------------------------------------------------------------------------------

# Установите зависимости для Chrome и ChromeDriver
RUN apt-get update && \
    apt-get install -y wget unzip apt-transport-https ca-certificates && \
    apt-get clean

# Определите версию Chrome и ChromeDriver
ENV CHROME_VERSION=135.0.7049.42
ENV CHROMEDRIVER_VERSION=135.0.7049.42

# Сформируйте URL-адреса для загрузки
ENV CHROME_HEADLESS_SHELL_URL=https://storage.googleapis.com/chrome-for-testing-public/${CHROME_VERSION}/linux64/chrome-headless-shell-linux64.zip
ENV CHROMEDRIVER_URL=https://storage.googleapis.com/chrome-for-testing-public/${CHROMEDRIVER_VERSION}/linux64/chromedriver-linux64.zip

# Загрузите, распакуйте и установите chrome-headless-shell
RUN wget -q -O /tmp/chrome-headless-shell-linux64.zip ${CHROME_HEADLESS_SHELL_URL} && \
    unzip /tmp/chrome-headless-shell-linux64.zip -d /opt/chrome-headless-shell && \
    mv /opt/chrome-headless-shell/chrome-headless-shell-linux64/chrome-headless-shell /usr/bin/chrome-headless-shell

# Загрузите, распакуйте и установите ChromeDriver
RUN wget -q -O /tmp/chromedriver_linux64.zip ${CHROMEDRIVER_URL} && \
    unzip /tmp/chromedriver_linux64.zip -d /opt/chromedriver && \
    chmod +x /opt/chromedriver/chromedriver && \
    ln -s /opt/chromedriver/chromedriver /usr/local/bin/chromedriver

# Установите переменную окружения для пути к ChromeDriver
ENV CHROMEDRIVER_PATH=/usr/local/bin/chromedriver

#-----------------------------------------------------------------------------------------------------------------------
RUN mvn -f $HOME/rest-service/pom.xml -s $MVN_SETTINGS clean package --debug

CMD java -jar $HOME/rest-service/target/rest-service-0.0.1.jar