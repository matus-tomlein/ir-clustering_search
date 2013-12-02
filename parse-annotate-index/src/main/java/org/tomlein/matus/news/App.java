package org.tomlein.matus.news;

import org.tomlein.matus.news.searchindex.Parser;

public class App
{
    public static void main( String[] args )
    {
        Parser parser = new Parser();
        parser.indexArticlesFromFile("/Volumes/Library HD/Data/news/all_links.txt");
    }
}
