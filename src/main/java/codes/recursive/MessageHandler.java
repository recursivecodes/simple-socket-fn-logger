package codes.recursive;

import com.github.palindromicity.syslog.SyslogParser;
import com.github.palindromicity.syslog.SyslogParserBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

public class MessageHandler implements Runnable {
    private final Socket clientSocket;
    private final SyslogParser parser = new SyslogParserBuilder().build();
    private final Logger logger = LoggerFactory.getLogger(Main.class);

    public MessageHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String incomingMsg;
        try{
            while( (incomingMsg = reader.readLine()) != null ) {
                Map<String, Object> result = parser.parseLine(incomingMsg);
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
                logger.info( result.get("syslog.message").toString() );
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
