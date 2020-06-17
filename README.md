# simple-socket-fn-logger

![simple-socket-fn-logger](https://github.com/recursivecodes/simple-socket-fn-logger/workflows/simple-socket-fn-logger/badge.svg)

## About

A simple socket server that can be used as a logging endpoint for your Oracle Functions! This can be run on your local machine, or wherever you want to run it (a cloud VM, etc). At bare minimum, it's a great tool to get near realtime logging for your Oracle Functions that are deployed in the Oracle Cloud. But it can be more than that! If you want, you can modify `MessageHandler.java` to persist your log data (maybe to a [free Autonomous DB instance](https://oracle.com/cloud/free))! The syslog format contains a log of data and this logger just outputs the message contents. You have access to a `Map` of data that looks like so:


```json
{
    "syslog.header.appName": "app_id=ocid1.fnapp.oc1.phx...,fn_id=ocid1.fnfunc.oc1.phx...",
    "syslog.header.version": "1",
    "syslog.header.hostName": "runner-00001700e5f9",
    "syslog.header.facility": "1",
    "syslog.header.msgId": "app_id=ocid1.fnapp.oc1.phx...,fn_id=ocid1.fnfunc.oc1.phx...",
    "syslog.header.timestamp": "2020-06-15T14:46:35Z",
    "syslog.message": "Error in function: ReferenceError: foo is not defined",
    "syslog.header.pri": "11",
    "syslog.header.procId": "8",
    "syslog.header.severity": "3"
}
```

Feel free to extend this as needed!

## Usage

You can use a pre-compiled version of this server, or compile it yourself.  

### Pre-compiled

Download the [latest release](https://github.com/recursivecodes/simple-socket-fn-logger/releases), place it in a directory and run the JAR (requires Java 11 JDK installed). See [Running The Server](#running-the-server).

### Compiling

To compile, use Gradle:

```shell script
$ ./gradlew shadowJar
```

This will create a runnable JAR in the `build/libs` directory.  See [Running The Server](#running-the-server).

### Running The Server

To run the server via the latest released JAR file:

```shell script
java -jar simple-socket-fn-logger-[version]-all.jar
```

You may also use one of the provided native images. For example, if you downloaded the Mac OS native executable called `simple-socket-fn-logger-0.46-macos`, you would run it like so:

| NOTE: Windows EXE native image is untested. If you would like to help out with Windows support, file a PR! |
| --- |

```shell script
$ chmod +x simple-socket-fn-logger-0.46-macos
$ ./simple-socket-fn-logger-0.46-macos
```

This will start up a socket server on the default port of 30000. If you want to use a different port, pass it in:

```shell script
java -jar -Dport=32000 simple-socket-fn-logger-[version]-all.jar
```

Native images also support passing in the port:

```shell script
$ ./simple-socket-fn-logger-0.46-macos -Dport=32000
```

| WARNING: Check firewall ports, routers, security lists, etc to make sure the port is open! |
| --- |

This **can** be run on `localhost`, but your syslog URL must be your public IP and your router/firewall should forward the port as necessary!

### Config Oracle Function Application

You'll need to set the syslog URL to point at your new socket server. You can do this via the CLI:

```shell script
$ fn update app syslog-demo-app --syslog-url tcp://[your public IP]:[socket server port]
```

Or via the console:

![set syslog url via console](https://objectstorage.us-phoenix-1.oraclecloud.com/n/toddrsharp/b/readme-assets/o/2020-06-15_10-58-38.png)

It's worth repeating that this **can** be run on `localhost`, but your syslog URL must be your public IP and your router/firewall should forward the port as necessary!

### Sample Output

Here's what you'll see in your console when a function produces output. You'll find this **much more helpful** than the standard response of `Error invoking function. status: 502 message: function failed`.

#### Node.JS 

A `console.log()` and an exception:

```shell script
Jun 16 12:18:46.060 [main] INFO  codes.recursive.Main - Listening on localhost:30000...
Jun 16 12:19:07.154 [Thread-1] INFO  codes.recursive.Main - this is a console.log()
Jun 16 12:19:07.154 [Thread-2] INFO  codes.recursive.Main - Error in function: ReferenceError: foo is not defined
Jun 16 12:19:07.159 [Thread-2] INFO  codes.recursive.Main - at /function/func.js:11:3
Jun 16 12:19:07.164 [Thread-2] INFO  codes.recursive.Main - at /function/node_modules/@fnproject/fdk/fn-fdk.js:299:26
Jun 16 12:19:07.167 [Thread-2] INFO  codes.recursive.Main - at new Promise (<anonymous>)
Jun 16 12:19:07.169 [Thread-2] INFO  codes.recursive.Main - at IncomingMessage.req.on.on (/function/node_modules/@fnproject/fdk/fn-fdk.js:297:7)
Jun 16 12:19:07.172 [Thread-2] INFO  codes.recursive.Main - at IncomingMessage.emit (events.js:193:13)
Jun 16 12:19:07.173 [Thread-2] INFO  codes.recursive.Main - at endReadableNT (_stream_readable.js:1139:12)
Jun 16 12:19:07.175 [Thread-2] INFO  codes.recursive.Main - at processTicksAndRejections (internal/process/task_queues.js:81:17)
Jun 16 12:19:07.177 [Thread-2] INFO  codes.recursive.Main - Error 502 : {"message":"Exception in function, consult logs for details","detail":"ReferenceError: foo is not defined"}
```

#### Java

A call to `System.out.println()` and an exception:

```shell script
Jun 16 12:19:37.682 [main] INFO  codes.recursive.Main - Listening on localhost:30000...
Jun 16 12:19:51.922 [Thread-1] INFO  codes.recursive.Main - This is System.out.println()
Jun 16 12:19:51.930 [Thread-1] INFO  codes.recursive.Main - An error occurred in function: foo
Jun 16 12:19:51.935 [Thread-1] INFO  codes.recursive.Main - Caused by: java.lang.Exception: foo
Jun 16 12:19:51.941 [Thread-1] INFO  codes.recursive.Main - at com.example.fn.HelloFunction.handleRequest(HelloFunction.java:9)
Jun 16 12:19:51.944 [Thread-1] INFO  codes.recursive.Main - at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
Jun 16 12:19:51.948 [Thread-1] INFO  codes.recursive.Main - at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(Unknown Source)
Jun 16 12:19:51.951 [Thread-1] INFO  codes.recursive.Main - at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)
Jun 16 12:19:51.956 [Thread-1] INFO  codes.recursive.Main - at java.base/java.lang.reflect.Method.invoke(Unknown Source)
```