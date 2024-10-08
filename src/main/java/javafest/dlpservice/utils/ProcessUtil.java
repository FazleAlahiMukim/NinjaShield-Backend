package javafest.dlpservice.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ProcessUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(ProcessUtil.class);

    public static void start(String name) {
        Map.Entry<String, String> paths = deducePaths(name);
        String exePath = paths.getKey();
        String file = paths.getValue();

        if (!isProcessRunning(file)) {
            logger.info("Starting process: " + file);

            String projectRoot = System.getProperty("user.dir");
            String exeFolderPath = null;

            if (name.equals("Screenshare")) {
                exeFolderPath = Paths.get(projectRoot, "programs", "ScreenShareMonitor").toString();
            } else if (name.equals("Screenshot")) {
                exeFolderPath = Paths.get(projectRoot, "programs", "ScreenshotMonitor").toString();
            } 
            
            startExe(exePath, exeFolderPath);
            
        }
    }

    public static void stop(String name) {
        Map.Entry<String, String> paths = deducePaths(name);
        String file = paths.getValue();

        if (isProcessRunning(file)) {
            logger.info("Stopping process: " + file);
            stopProcessByName(file);
        }
    }

    public static boolean isProcessRunning(String processName) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("tasklist");
            Process process = processBuilder.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(processName.toLowerCase())) {
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Process startExe(String exePath, String exeFolderPath) {
        ProcessBuilder processBuilder = new ProcessBuilder(exePath);
        if (exeFolderPath != null) {
            processBuilder.directory(new File(exeFolderPath));
        }
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void stopProcessByName(String processName) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("taskkill", "/F", "/IM", processName + ".exe");
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopProcess(Process process) {
        if (process != null) {
            process.destroy();
        }
    }

    private static Map.Entry<String, String> deducePaths(String name) {
        String exePath = null;
        String file = null;

        switch (name) {
            case "Email":
                exePath = "./programs/EmailMonitor/OutlookMonitor.exe";
                file = "OutlookMonitor";
                break;
            case "Printer":
                exePath = "./programs/PrintJobMonitor/PrintJobMonitor.exe";
                file = "PrintJobMonitor";
                break;
            case "Screenshare":
                exePath = "./programs/ScreenShareMonitor/ScreenShareMonitor.exe";
                file = "ScreenShareMonitor";
                break;
            case "Screenshot":
                exePath = "./programs/ScreenshotMonitor/ScreenshotMonitor.exe";
                file = "ScreenshotMonitor";
                break;
            case "Onedrive":
                exePath = "./programs/OnedriveMonitor/onedriveapp.exe";
                file = "onedriveapp";
                break;
            case "Clipboard":
                exePath = "./programs/ClipboardMonitor/clipboardapp.exe";
                file = "clipboardapp";
                break;
        }

        return new AbstractMap.SimpleEntry<>(exePath, file);
    }
    
}
