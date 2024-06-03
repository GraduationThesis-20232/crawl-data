package savetofile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public interface DownloadFile {
    public default void download(String url, String fileName)
    {
        try {
            Path targetPath = null;
            if (fileName.contains("pdf")) {
                targetPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "data", "documents", "pdf", fileName);
            } else if (fileName.contains("docx")) {
                targetPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "data", "documents", "docx", fileName);
            } else {
                targetPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "data", "documents", "other", fileName);
            }

            if (Files.exists(targetPath)) {
                System.out.println("File already exists: " + targetPath);
                return;
            }

            try (InputStream in = new URL(url).openStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
