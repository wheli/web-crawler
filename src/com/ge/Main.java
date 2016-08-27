package com.ge;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws IOException {
        InternetConsumer consumer = new InternetConsumer();
        String internet1JSONString = consumer.getJSONStringfromResource("resources/internet1.json");

        // Crawl Internet 1 and print the results of the crawl.
        if (internet1JSONString != null) {
            JSONObject internet1 = new JSONObject(internet1JSONString);
            WebCrawler webCrawler = new WebCrawler(internet1);
            webCrawler.crawl();
            System.out.println("Internet 1 results:");
            webCrawler.printResults();
        }

        // add space to make things pretty
        System.out.println("");

        // Crawl Internet 2 and print the results of the crawl.
        String internet2JSONString = consumer.getJSONStringfromResource("resources/internet2.json");
        if (internet2JSONString != null) {
            JSONObject internet2 = new JSONObject(internet2JSONString);
            WebCrawler webCrawler = new WebCrawler(internet2);
            webCrawler.crawl();
            System.out.println("Internet 2 results:");
            webCrawler.printResults();
        }
    }

    /**
     * Helper class/method to get "internet" resource files and their content as JSON strings.
     * (Can not use "getClass" in a "main");
     */
    private static class InternetConsumer {
        public String getJSONStringfromResource(String resource) throws IOException {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
            String jsonString = IOUtils.toString(inputStream, "UTF-8");
            return jsonString;
        }
    }
}
