package javafest.dlpservice.service;

import javafest.dlpservice.utils.Kernel32;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class USBDetectorService {

    @Autowired
    private PolicyCheckService policyCheckService;

    private List<String> usbDrives;
    private final Map<String, Future<?>> runningServices = new HashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Logger logger = LoggerFactory.getLogger(USBDetectorService.class);

    private ThreadPoolTaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledTask;

    public USBDetectorService() {
        usbDrives = new ArrayList<>();
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();
    }

    public void detectAndPrintUSBDrives() {
        List<String> currentUsbDrives = detectUSBDrivesWindows();
        if (!currentUsbDrives.equals(usbDrives)) {
            updateServices(currentUsbDrives);
            usbDrives = new ArrayList<>(currentUsbDrives);
            logger.info("USB drives updated: " + usbDrives);
        }
    }

    public void start() {
        if (scheduledTask == null || scheduledTask.isCancelled()) {
            scheduledTask = taskScheduler.scheduleAtFixedRate(this::detectAndPrintUSBDrives, Duration.ofMillis(5000));
            logger.info("USB Detection Service started.");
        }
    }

    public void stop() {
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            stopAllServices();
            logger.info("USB Detection Service stopped.");
        }
    }

    private void stopAllServices() {
        for (Future<?> future : runningServices.values()) {
            if (future != null) {
                future.cancel(true);
            }
        }
        runningServices.clear();
    }

    public List<String> detectUSBDrivesWindows() {
        List<String> usbPaths = new ArrayList<>();
        File[] roots = File.listRoots();
        for (File root : roots) {
            if (root.getPath().matches("[A-Z]:\\\\") && isRemovableDrive(root.getPath().substring(0, 1))) {
                usbPaths.add(root.getPath());
            }
        }
        return usbPaths;
    }

    private boolean isRemovableDrive(String driveLetter) {
        HANDLE hDevice = Kernel32.INSTANCE.CreateFile("\\\\.\\" + driveLetter + ":",
                Kernel32.GENERIC_READ, Kernel32.FILE_SHARE_READ | Kernel32.FILE_SHARE_WRITE, null,
                Kernel32.OPEN_EXISTING, Kernel32.FILE_ATTRIBUTE_NORMAL, null);
        if (Kernel32.INVALID_HANDLE_VALUE.equals(hDevice)) {
            return false;
        }

        IntByReference lpBytesReturned = new IntByReference();
        Kernel32.DISK_GEOMETRY_EX dg = new Kernel32.DISK_GEOMETRY_EX();
        boolean result = Kernel32.INSTANCE.DeviceIoControl(hDevice, Kernel32.IOCTL_DISK_GET_DRIVE_GEOMETRY_EX,
                Pointer.NULL, 0, dg.getPointer(), dg.size(), lpBytesReturned, Pointer.NULL);
        dg.read();
        Kernel32.INSTANCE.CloseHandle(hDevice);

        return result && dg.Geometry.MediaType == 11; // 11 indicates removable media
    }

    private void updateServices(List<String> currentUsbDrives) {
        Set<String> newDrives = new HashSet<>(currentUsbDrives);
        Set<String> oldDrives = new HashSet<>(usbDrives);

        // Stop services for removed USB drives
        oldDrives.removeAll(newDrives);
        for (String drive : oldDrives) {
            Future<?> future = runningServices.remove(drive);
            if (future != null) {
                future.cancel(true);
                logger.info("Service stopped for usb drive: " + drive);
            }
        }

        // Start services for newly added USB drives
        newDrives.removeAll(usbDrives);
        for (String drive : newDrives) {
            try {
                Future<?> future = executorService.submit(new USBMonitorService(drive, policyCheckService));
                runningServices.put(drive, future);
                logger.info("Service started for usb drive: " + drive);
            } catch (IOException e) {
                logger.error("Failed to start service for usb drive: " + drive, e);
            }
        }
    }
}
 