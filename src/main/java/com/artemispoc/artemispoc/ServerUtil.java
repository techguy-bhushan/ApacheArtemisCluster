package com.artemispoc.artemispoc;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.jms.client.ActiveMQConnection;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.Connection;
import java.io.*;

public class ServerUtil {
    public ServerUtil() {
    }

    public static Process startServer(String artemisInstance, String serverName) throws Exception {
        return startServer(artemisInstance, serverName, 0, 0);
    }

    public static Process startServer(String artemisInstance, String serverName, int id, int timeout) throws Exception {
        boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().trim().startsWith("win");
        ProcessBuilder builder = null;
        if (IS_WINDOWS) {
            builder = new ProcessBuilder(new String[]{"cmd", "/c", "artemis.cmd", "run"});
        } else {
            builder = new ProcessBuilder(new String[]{"./artemis", "run"});
        }

        builder.directory(new File(artemisInstance + "/bin"));
        final Process process = builder.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                process.destroy();
            }
        });
        ServerUtil.ProcessLogger outputLogger = new ServerUtil.ProcessLogger(true, process.getInputStream(), serverName, false);
        outputLogger.start();
        ServerUtil.ProcessLogger errorLogger = new ServerUtil.ProcessLogger(true, process.getErrorStream(), serverName, true);
        errorLogger.start();
        if (timeout != 0) {
            waitForServerToStart(id, timeout);
        }

        return process;
    }

    public static void waitForServerToStart(int id, int timeout) throws InterruptedException {
        waitForServerToStart("tcp://localhost:" + ('\uf0b0' + id), (long)timeout);
    }

    public static void waitForServerToStart(String uri, long timeout) throws InterruptedException {
        long realTimeout = System.currentTimeMillis() + timeout;

        while(System.currentTimeMillis() < realTimeout) {
            try {
                ActiveMQConnectionFactory cf = ActiveMQJMSClient.createConnectionFactory(uri, (String)null);
                Throwable var6 = null;

                try {
                    cf.createConnection().close();
                    System.out.println("server " + uri + " started");
                    break;
                } catch (Throwable var16) {
                    var6 = var16;
                    throw var16;
                } finally {
                    if (cf != null) {
                        if (var6 != null) {
                            try {
                                cf.close();
                            } catch (Throwable var15) {
                                var6.addSuppressed(var15);
                            }
                        } else {
                            cf.close();
                        }
                    }

                }
            } catch (Exception var18) {
                System.out.println("awaiting server " + uri + " start at ");
                Thread.sleep(500L);
            }
        }

    }

    public static void killServer(Process server) throws Exception {
        if (server != null) {
            System.out.println("**********************************");
            System.out.println("Killing server " + server);
            System.out.println("**********************************");
            server.destroy();
            server.waitFor();
            Thread.sleep(1000L);
        }

    }

    public static int getServer(Connection connection) {
        ClientSession session = ((ActiveMQConnection)connection).getInitialSession();
        TransportConfiguration transportConfiguration = session.getSessionFactory().getConnectorConfiguration();
        String port = (String)transportConfiguration.getParams().get("port");
        return Integer.valueOf(port).intValue() - '\uf0b0';
    }

    public static Connection getServerConnection(int server, Connection... connections) {
        Connection[] var2 = connections;
        int var3 = connections.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Connection connection = var2[var4];
            ClientSession session = ((ActiveMQConnection)connection).getInitialSession();
            TransportConfiguration transportConfiguration = session.getSessionFactory().getConnectorConfiguration();
            String port = (String)transportConfiguration.getParams().get("port");
            if (Integer.valueOf(port).intValue() == server + '\uf0b0') {
                return connection;
            }
        }

        return null;
    }

    static class ProcessLogger extends Thread {
        private final InputStream is;
        private final String logName;
        private final boolean print;
        private final boolean sendToErr;

        ProcessLogger(boolean print, InputStream is, String logName, boolean sendToErr) throws ClassNotFoundException {
            this.is = is;
            this.print = print;
            this.logName = logName;
            this.sendToErr = sendToErr;
            this.setDaemon(false);
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(this.is);
                BufferedReader br = new BufferedReader(isr);

                String line;
                while((line = br.readLine()) != null) {
                    if (this.print) {
                        if (this.sendToErr) {
                            System.err.println(this.logName + "-err:" + line);
                        } else {
                            System.out.println(this.logName + "-out:" + line);
                        }
                    }
                }
            } catch (IOException var4) {
                ;
            }

        }
    }
}
