package cc.wybxc;

import cc.wybxc.common.ApplicationProperties;
import cc.wybxc.frontend.DanmakuClient;
import cc.wybxc.frontend.DanmakuServer;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ApplicationProperties.init();

        var options = new Options();
        options.addOption("h", "help", false, "Print this message");
        options.addOption("c", "client", false, "Run as client instead of server");
        options.addOption("p", "port", true, "Port to listen on (default: 2333)");
        options.addOption("b", "backend", true, "Backend class (default: cc.wybxc.backend.WebSocketBackend)");
        options.addOption("d", "debug", false, "Enable debug mode");

        var parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Failed to parse command line arguments: " + e);
            System.exit(1);
            return;
        }

        if (cmd.hasOption("h")) {
            var formatter = new HelpFormatter();
            formatter.printHelp("DanmakuFX", options);
            System.exit(0);
        }

        if (cmd.hasOption("d")) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        }

        if (cmd.hasOption("p")) {
            System.setProperty("backend.port", cmd.getOptionValue("p"));
            logger.debug("Set backend.port to {}", cmd.getOptionValue("p"));
        }

        if (cmd.hasOption("b")) {
            System.setProperty("backend.class", cmd.getOptionValue("b"));
            logger.debug("Set backend.class to {}", cmd.getOptionValue("b"));
        }

        ApplicationProperties.load();

        if (cmd.hasOption("c")) {
            DanmakuClient.launch();
        } else {
            DanmakuServer.launch();
        }
    }
}
