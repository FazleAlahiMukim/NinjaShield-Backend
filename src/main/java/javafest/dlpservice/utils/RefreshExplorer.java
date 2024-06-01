package javafest.dlpservice.utils;


import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefreshExplorer {
    private static final Logger logger = LoggerFactory.getLogger(RefreshExplorer.class);
    public static void execute() {
        try {
            Robot robot = new Robot();

            // Optional: Give the user time to focus on the File Explorer window
            // robot.delay(2000);

            // Simulate pressing F5
            robot.keyPress(KeyEvent.VK_F5);
            robot.keyRelease(KeyEvent.VK_F5);

            logger.info("F5 key was pressed to refresh the active window.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
