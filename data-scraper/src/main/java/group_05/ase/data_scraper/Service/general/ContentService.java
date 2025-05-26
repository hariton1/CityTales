package group_05.ase.data_scraper.Service.general;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ContentService {

    public String extractMainArticleText(Document doc) {

        StringBuilder textContent = new StringBuilder();

        Element contentDiv = doc.selectFirst("div.mw-parser-output");

        if (contentDiv != null) {
            Elements paragraphs = contentDiv.select("p");

            for (Element paragraph : paragraphs) {
                String text = paragraph.text().trim();
                if (!text.isEmpty()) {
                    textContent.append(text).append("\n\n");
                }
            }
        } else {
            System.err.println("Main content div not found.");
        }

        String[] parts = textContent.toString().split("\n", 7);

        if (parts.length < 7) {
            return "";
        } else {
            String rest = parts[6];
            rest = rest.replaceAll("\n", "");

            return rest;
        }
    }
}

