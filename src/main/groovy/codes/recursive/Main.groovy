package codes.recursive

import com.github.palindromicity.syslog.SyslogParser
import com.github.palindromicity.syslog.SyslogParserBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Main {

    static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class)
        SyslogParser parser = new SyslogParserBuilder().build()

        int port = System.getProperty("port", 30000 as String).toInteger()
        def socketServer = new ServerSocket(port)

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            void run() {
                logger.info("Server shutting down. Goodbye...")
                socketServer.close()
            }
        })

        logger.info("Listening on localhost:${port}...")

        while(true) {
            socketServer.accept { socket ->
                socket.withStreams { input, output ->
                    // want to send something back? do this...
                    // output << "[${new Date()}] MSG RECEIVED\n"
                    def reader = input.newReader()
                    def incomingMsg
                    while( (incomingMsg = reader.readLine()) != null ) {
                        Map<String, Object> result = parser.parseLine(incomingMsg)
                        /*
                            gives us a nicely formatted Map
                            containing lots of information.
                            for example:
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
                         */
                        logger.info( result.get("syslog.message").toString() )
                    }
                }
            }
        }

    }

}