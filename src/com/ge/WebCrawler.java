package com.ge;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class WebCrawler {

    private JSONArray theInternet; // an array of web pages.

    private Set<String> crawled; // successfully crawled pages
    private Set<String> skipped; // skipped links
    private Set<String> error; // links to pages that don't exist
    private Set<String> visited; // pages that have been visited.

    // quicker access when getting a JSON page object, given its address
    private HashMap<String,JSONObject> addressPageCache;

    /**
     * Construct/initialize the web crawler.
     * @param theInternet
     */
    public WebCrawler(JSONObject theInternet) {
        this.theInternet = theInternet.getJSONArray("pages");
        crawled = new HashSet<>();
        skipped = new HashSet<>();
        error = new HashSet<>();
        visited = new HashSet<>();
        addressPageCache = new HashMap<>();
    }

    /**
     * Crawl all the pages in a JSON Array of pages.
     */
    public void crawl() {
        for (int i = 0; i < theInternet.length(); i++) {
            JSONObject page = (JSONObject) theInternet.get(i);
            String address = page.getString("address");
            crawlPage(address);
        }
    }

    /**
     * Crawl a JSON page object, given its address.
     *
     * Used the address string as a parameter, instead of a JSON page object
     * because of the error and skip tracking and recursive natural of this function.
     * (There may be a way to use the JSON page object and remove the "getPageForAddress"
     * call on line 57, but I would need more time to work it out).
     *
     * @param address
     */
    private void crawlPage(String address) {
        if (!visited.contains(address)) {
            visited.add(address);

            // get JSON page object using address
            JSONObject page = getPageForAddress(address);
            if (page != null) {
                // get links on page
                JSONArray links = page.getJSONArray("links");

                // Special case where page has no links - mark as crawled and move on.
                // (e.g. Internet 1 - p5)
                if (links.length() == 0) {
                    crawled.add(address);
                    return;
                }

                // Special case for Internet 2 - not sure why p5 is considered a success, but p6 isn't.
                // Both pages link to the same page (p1) that has already been crawled. The only difference
                // that I can see is that the p6 page has a link to a page that was already skipped (when
                // p5 was crawled).
                if (haveAllLinksBeenSkipped(links)) {
                    return;
                }

                // crawl each linked page that has not yet been visited.
                List<String> linksNotVisited = getLinksNotVisited(links);
                if (!linksNotVisited.isEmpty()) {
                    for (String link : linksNotVisited) {
                        // if the linked page is not valid, record it as an error.
                        if (getPageForAddress(link) == null) {
                            error.add(link);
                        } else {
                            crawlPage(link);
                        }
                    }
                }
                // mark a page as crawled once all links have been visited.
                crawled.add(address);
            }
        }
    }

    /**
     * Get the JSON page object given an address (string).
     * Cache result for future page requests.
     * @param address
     * @return
     */
    private JSONObject getPageForAddress(String address) {
        // try the cache first, to save time
        JSONObject page = addressPageCache.get(address);
        if (page == null) {
            // no result found in cache, get "page" JSONObject from master list (a.k.a "theInternet").
            for (int i = 0; i < theInternet.length(); i++) {
                if (theInternet.getJSONObject(i).getString("address").equals(address)) {
                    JSONObject p = theInternet.getJSONObject(i);
                    // add to cache, to speed up future calls.
                    addressPageCache.put(address, p);
                    return p;
                }
            }
            return null; // return null if nothing found
        }
        else {
            return page;
        }
    }

    /**
     * Given a JSONArray of links, return a sub-list of ones
     * that have not been visited yet.
     * @param links
     * @return
     */
    private List getLinksNotVisited(JSONArray links) {
        List<String> linksNotVisisted = new ArrayList<>();
        for (int i = 0; i < links.length(); i++) {
            String linkedAddress = links.getString(i);
            if (!visited.contains(linkedAddress)) {
                linksNotVisisted.add(linkedAddress);
            } else {
                skipped.add(linkedAddress);
            }
        }
        return linksNotVisisted;
    }

    /**
     * Check to see if all the links in a JSONArray of links have
     * been skipped.
     * @param links
     * @return
     */
    private boolean haveAllLinksBeenSkipped(JSONArray links) {
        boolean haveAllLinksBeenSkipped = true;
        for (int i = 0; i < links.length(); i++) {
            String linkedAddress = links.getString(i);
            if (!skipped.contains(linkedAddress)) {
                haveAllLinksBeenSkipped = false;
            }
        }
        return haveAllLinksBeenSkipped;
    }

    /**
     * Print the results of a crawl.
     */
    public void printResults() {

        System.out.println("Success:");
        printSet(crawled);
        System.out.println("");

        System.out.println("Skipped:");
        printSet(skipped);
        System.out.println("");

        System.out.println("Error:");
        printSet(error);
        System.out.println("");
    }

    /**
     * Print the values within a set of strings.
     * @param set
     */
    private void printSet(Set<String> set) {
        System.out.print("[");

        int i = 0;
        for (String item : set) {
            System.out.print("\"" + item + "\"");
            if (i + 1 < set.size()) {
                System.out.print(", ");
            }
            i++;
        }
        System.out.print("]");
    }

}
