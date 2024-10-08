package javafest.dlpservice.utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.*;

public class SearchUtil {

    public static String extractText(File file) {
        String fileName = file.getName().toLowerCase();
        int attempts = 0;
        while (attempts < 10) {
            try {
                if (fileName.endsWith(".txt")) {
                    return new String(Files.readAllBytes(file.toPath()));
                } else if (fileName.endsWith(".docx")) {
                    return extractTextFromDocx(file);
                } else if (fileName.endsWith(".pdf")) {
                    return extractTextFromPdf(file);
                } else {
                    return null;
                }
            } catch (IOException e) {
                attempts++;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    break;
                }

            }
        }
        return null;
    }

    private static String extractTextFromDocx(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            XWPFDocument doc = new XWPFDocument(fis);
            StringBuilder text = new StringBuilder();
            for (XWPFParagraph para : doc.getParagraphs()) {
                text.append(para.getText()).append("\n");
            }
            doc.close();
            return text.toString();
        }
    }

    private static String extractTextFromPdf(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    public static String extractTextFromPdf(byte[] pdfBytes) {
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String extractTextFromDocx(byte[] docxBytes) {
        try (InputStream inputStream = new ByteArrayInputStream(docxBytes);
                XWPFDocument doc = new XWPFDocument(inputStream);
                XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, Integer> searchKeywordsAndRegex(String text, List<String> keywords,
            List<String> regexPatterns) {
        Map<String, Integer> matchCounts = new HashMap<>();

        for (String keyword : keywords) {
            int count = countOccurrences(text, keyword);
            matchCounts.put(keyword, count);
        }

        for (String regex : regexPatterns) {
            int count = countRegexMatches(text, regex);
            matchCounts.put(regex, count);
        }

        return matchCounts;
    }

    public static int searchKeywords(String text, List<String> keywords) {
        int totalCount = 0;
        for (String keyword : keywords) {
            int count = countOccurrences(text, keyword);
            totalCount += count;
        }
        return totalCount;
    }

    public static int searchRegex(String text, List<String> regexPatterns) {
        int totalCount = 0;
        for (String regex : regexPatterns) {
            int count = countRegexMatches(text, regex);
            totalCount += count;
        }
        return totalCount;
    }

    private static int countOccurrences(String text, String keyword) {
        text = text.toLowerCase();
        keyword = keyword.toLowerCase();

        int count = 0;
        int index = 0;
        while ((index = text.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }

    private static int countRegexMatches(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
