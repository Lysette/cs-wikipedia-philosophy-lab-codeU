package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	final static WikiFetcher wf = new WikiFetcher();
	
	// gets first valid link on url
	public static String crawl(String currentUrl) { 

		String urlStart = "https://en.wikipedia.org";
		int brackets = 0; // counts nesting of bracket
		Elements paras;
		
		try {
			paras = wf.fetchWikipedia(currentUrl);
		
			for (Element para : paras) { 
				Iterable<Node> iter = new WikiNodeIterable(para);
		
				// go thru each node in each paragraph
				for (Node node : iter) {
					
					// count brackets
					if (node instanceof TextNode) {
						String text = ((TextNode) node).text();
						brackets += text.length() - text.replace("(", "").length();
						brackets -= text.length() - text.replace(")", "").length();
					}
					
					// check if node is link, not in italics (i or em), not in any brackets
					if (node instanceof Element && 
							((Element) node).tagName().equals("a") && 
							!((Element) node.parent()).tagName().equals("i") &&
							!((Element) node.parent()).tagName().equals("em") &&
							brackets == 0) {
						
						// get link
						String urlEnd = ((Element) node).attr("href");
	
						// no external links, or red links
						// only internal non-red links start with /wiki/
						if (urlEnd.startsWith("/wiki/")) { 
							String url = urlStart + urlEnd;
							// doesnt link back to current url
							if (!url.equals(currentUrl))
								return url;
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
        // some example code to get you started

		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		String urlEnd = "https://en.wikipedia.org/wiki/Philosophy";
		Elements paragraphs = wf.fetchWikipedia(url);

		/*
		Element firstPara = paragraphs.get(0);

		Iterable<Node> iter = new WikiNodeIterable(firstPara);
		for (Node node: iter) {
			if (node instanceof TextNode) {
				System.out.println(node);
			}
        }
		*/
		
		List<String> urls = new ArrayList<String>();
		
		// stop if reach dead end, get to philosophy, or loop
		while (url != null && !url.equals(urlEnd) && 
				!urls.subList(0, Math.max(0, urls.size() - 2)).contains(url)) {
			url = crawl(url);
			System.out.println(url);
			urls.add(url);
		}

        // the following throws an exception so the test fails
        // until you update the code
        //String msg = "Complete this lab by adding your code and removing this statement.";
        //throw new UnsupportedOperationException(msg);
	}
}
