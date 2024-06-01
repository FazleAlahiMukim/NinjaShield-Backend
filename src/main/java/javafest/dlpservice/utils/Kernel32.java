package javafest.dlpservice.utils;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.W32APIOptions;

import java.util.Arrays;
import java.util.List;

public interface Kernel32 extends com.sun.jna.platform.win32.Kernel32 {
    Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class, W32APIOptions.DEFAULT_OPTIONS);

    int IOCTL_DISK_GET_DRIVE_GEOMETRY_EX = 0x000700A0;

    class DISK_GEOMETRY extends Structure {
        public long Cylinders;
        public int MediaType;
        public int TracksPerCylinder;
        public int SectorsPerTrack;
        public int BytesPerSector;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("Cylinders", "MediaType", "TracksPerCylinder", "SectorsPerTrack", "BytesPerSector");
        }
    }

    class DISK_GEOMETRY_EX extends Structure {
        public DISK_GEOMETRY Geometry;
        public long DiskSize;
        public byte[] Data = new byte[1];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("Geometry", "DiskSize", "Data");
        }
    }
}
