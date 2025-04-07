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
# Установите зависимости для Chrome и ChromeDriver + необходимые библиотеки!
RUN apt-get update && \
    apt-get install -y wget unzip apt-transport-https ca-certificates libglib2.0-0 libnss3 libgconf-2-4 && \
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
    chmod +x /opt/chrome-headless-shell/chrome-headless-shell-linux64/chrome-headless-shell && \
    ln -s /opt/chrome-headless-shell/chrome-headless-shell-linux64/chrome-headless-shell /usr/local/bin/chrome-headless-shell # Создайте символическую ссылку!

# Загрузите, распакуйте и установите ChromeDriver
RUN wget -q -O /tmp/chromedriver_linux64.zip ${CHROMEDRIVER_URL} && \
    unzip /tmp/chromedriver_linux64.zip -d /opt/chromedriver && \
    chmod +x /opt/chromedriver/chromedriver-linux64/chromedriver && \
    ln -s /opt/chromedriver/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver

# Установите переменную окружения для пути к ChromeDriver (не обязательно, но рекомендуется)
ENV CHROMEDRIVER_PATH=/usr/local/bin/chromedriver

#-----------------------------------------------------------------------------------------------------------------------
RUN mvn -f $HOME/rest-service/pom.xml -s $MVN_SETTINGS clean package --debug

CMD java -jar $HOME/rest-service/target/rest-service-0.0.1.jar