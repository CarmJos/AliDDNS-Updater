package cc.carm.app.aliddns.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class FileUtils {

    /**
     * Rename the file.
     */
    public static void copy(final File file, final String newName) throws IOException {
        // file is null then return false
        if (file == null) return;
        // file doesn't exist then return false
        if (!file.exists()) return;
        // the new name equals old name then return true
        if (newName.equals(file.getName())) return;
        File newFile = new File(file.getParent(), newName);
        // the new name of file exists then return false
        if (newFile.exists()) newFile.delete();

        try (InputStream input = Files.newInputStream(file.toPath());
             OutputStream output = Files.newOutputStream(newFile.toPath())) {
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        }
    }
}
