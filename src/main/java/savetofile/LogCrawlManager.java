package savetofile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class LogCrawlManager {
    public static LogCrawlManager getInstance() {
        return new LogCrawlManager();
    }

    public void logDocumentCrawled(String type, int numberDocuments) {
        String today = LocalDate.now().toString();
        String logDirectoryPath = "src/main/resources/data/documents/log";
        String logFilePath = logDirectoryPath + "/" + today + ".txt";

        try {
            try (FileOutputStream fos = new FileOutputStream(logFilePath, true);
                 OutputStreamWriter osw = new OutputStreamWriter(fos);
                 PrintWriter out = new PrintWriter(osw)) {

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = LocalDateTime.now().format(dateTimeFormatter);

                out.printf("%s %d %s%n", currentDateTime, numberDocuments, type);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getLogDocumentCrawled(String fileName) {
        String logDirectoryPath = "src/main/resources/data/documents/log";
        String logFilePath = logDirectoryPath + "/" + fileName;

        ArrayList<String> logEntries = new ArrayList<>();

        if (!Files.exists(Paths.get(logFilePath))) {
            System.out.println("Log file not found: " + logFilePath);
            return logEntries;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                logEntries.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logEntries;
    }

    public void logQuestionsCrawled(String fileName, int numberQuestions, String field) {
        String today = LocalDate.now().toString();
        String logDirectoryPath = "src/main/resources/data/questions/log";
        String logFilePath = logDirectoryPath + "/" + today + ".txt";

        try {
            try (FileOutputStream fos = new FileOutputStream(logFilePath, true);
                 OutputStreamWriter osw = new OutputStreamWriter(fos);
                 PrintWriter out = new PrintWriter(osw)) {

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = LocalDateTime.now().format(dateTimeFormatter);

                out.printf("%s %s %d %s%n", currentDateTime, fileName, numberQuestions, field);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  ArrayList<String> getLogQuestionsCrawled(String fileName) {
        String logDirectoryPath = "src/main/resources/data/questions/log";
        String logFilePath = logDirectoryPath + "/" + fileName;

        ArrayList<String> logEntries = new ArrayList<>();

        if (!Files.exists(Paths.get(logFilePath))) {
            System.out.println("Log file not found: " + logFilePath);
            return logEntries;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(logFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                logEntries.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logEntries;
    }

    public String findNearestLogFile(String logDirectoryPath) {
        File logDirectory = new File(logDirectoryPath);

        if (!logDirectory.exists() || !logDirectory.isDirectory() || logDirectory.listFiles() == null) {
            System.out.println("Log directory not found or empty: " + logDirectoryPath);
            return null;
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        File logFilesToday = new File(logDirectoryPath + "/" + today.toString() + ".txt");
        if (logFilesToday.exists()) {
            return logFilesToday.getName();
        }

        File[] logFiles = logDirectory.listFiles();
        Optional<String> nearestLogFileName = Arrays.stream(logFiles)
                .max(Comparator.comparing(file -> {
                    LocalDate fileDate = LocalDate.parse(file.getName().substring(0, 10), dateFormatter);
                    return fileDate;
                }))
                .map(File::getName);

        return nearestLogFileName.orElse(null);
    }
}
