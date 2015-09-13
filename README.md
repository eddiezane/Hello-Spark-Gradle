# Hello-Spark-Gradle

See the [blog post](https://www.twilio.com/blog/2015/09/getting-started-with-gradle-and-the-spark-framework-3.html) for details!

## Usage

Clone the repo and `cd` into it.

```
git clone git@github.com:eddiezane/Hello-Spark-Gradle.git
cd Hello-Spark-Gradle
```

Update the lines marked with `// TODO` to use your credentials and [ngrok url](https://www.twilio.com/blog/2013/10/test-your-webhooks-locally-with-ngrok.html)

Build and start the JAR!

```
./gradlew shadowJar
java -jar build/libs/HelloSpark-all.jar
```