FROM adoptopenjdk/openjdk11:jdk-11.0.5_10-alpine
ADD . /src
WORKDIR /src
RUN ./gradlew build
EXPOSE 8080
ENTRYPOINT ["java","-jar","build/libs/currency-exchange-rate-1.0.jar"]