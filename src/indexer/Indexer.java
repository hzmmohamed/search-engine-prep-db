package indexer;

import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.Object;
import java.io.IOException;
import java.lang.*;


import dbconnect.DBConnect;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import org.tartarus.snowball.ext.porterStemmer;

import com.google.gson.Gson;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import ranker.Word;

import java.net.InetAddress;



public class Indexer {


    public static void main(String[] args) throws IOException, GeoIp2Exception, InterruptedException {

        final String stop = System.getProperty("user.dir") + "\\prepare\\src\\indexer\\english_stopwords.txt";
        final String geolite = System.getProperty("user.dir") + "\\prepare\\src\\indexer\\GeoLite2-City.mmdb";

        List<String> stopwords = Files.readAllLines(Paths.get(stop));
        int docId = 1;
        String docUrl;
        int totalCount = 0;
        Gson gson = new Gson();
        //Stemmer s = new Stemmer();
        porterStemmer stemmer = new porterStemmer();
        //long time = System.currentTimeMillis();
        Map<String, Word> countMap;
        Map<String, ArrayList<Word>> Words = new HashMap<String, ArrayList<Word>>();
        String countryName;

        //Connect to the database
        DBConnect.initDB();

        //Empty the old data 3ashan n3raf neshof eh eli bye7sal
//        DBConnect.deleteAllWords();

        //Get all crawler visited links
        Set<String> visitedUrls = new HashSet<String>();
        DBConnect.getVisited(visitedUrls);

        //To read the location out of the url
        //ClassLoader classLoader = Indexer.class.getClassLoader();
        //File database = new File(classLoader.getResource("GeoLite2-City.mmdb").getFile());
        File database = new File(geolite);
        DatabaseReader dbReader = new DatabaseReader.Builder(database).build();

        //Create BufferedReader so the words can be counted
        BufferedReader breader = null;
        BufferedReader h1reader = null;
        BufferedReader h2reader = null;
        BufferedReader h3reader = null;
        BufferedReader h4reader = null;
        BufferedReader h5reader = null;
        BufferedReader h6reader = null;
        BufferedReader treader = null;


        //for(String url : visitedUrls)
        Iterator<String> it = visitedUrls.iterator();
        while (it.hasNext() && docId <= 300) {
            countMap = new HashMap<String, Word>();
            totalCount = 0;
            String url = it.next();

            //Finding the location of the url
            try {
                InetAddress ipAddress = InetAddress.getByName(new URL(url).getHost());
                CityResponse response = dbReader.city(ipAddress);
                countryName = response.getCountry().getName();
            } catch (UnknownHostException e) {
                countryName = "Not Found";
            }

            //connect to url and get the HTML
            System.out.println("Downloading page number " + docId);
            Document doc = null;
            try {
                doc = Jsoup.connect(url).ignoreHttpErrors(true).timeout(0).get();
            } catch (UnknownHostException e) {
                continue;
            }
            docUrl = url;
            Elements headers = (Elements) doc.select("h1, h2, h3, h4, h5, h6");
            Elements title = (Elements) doc.select("title");
            Element meta = doc.select("meta[itemprop=dateModified]").first();
            Element time = doc.select("time[pubDate]").first();


            //Get the actual text from the page, excluding the HTML
            String date;
            //String pubdate = time.attr("datetime");
            //String datepublished = meta.attr("content");
            if (meta != null) date = meta.attr("content");
            else if (time != null) date = time.attr("datetime");
            else date = "not found";

            String bodyText = "";
            String titleText;
            String titletext = "";
            String h1Text;
            String h2Text;
            String h3Text;
            String h4Text;
            String h5Text;
            String h6Text;

            if (doc.body() != null)
                bodyText = doc.body().text().toLowerCase();
            else continue;
            titleText = title.text().toLowerCase();
            titletext = title.text().replace('\'', ' ').replace("'", "").replace('"', ' ').replace("\"", "");
            h1Text = headers.select("h1").text().toLowerCase();
            h2Text = headers.select("h2").text().toLowerCase();
            h3Text = headers.select("h3").text().toLowerCase();
            h4Text = headers.select("h4").text().toLowerCase();
            h5Text = headers.select("h5").text().toLowerCase();
            h6Text = headers.select("h6").text().toLowerCase();


            System.out.println("Analyzing text ");

            breader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bodyText.getBytes(StandardCharsets.UTF_8))));
            h1reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(h1Text.getBytes(StandardCharsets.UTF_8))));
            h2reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(h2Text.getBytes(StandardCharsets.UTF_8))));
            h3reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(h3Text.getBytes(StandardCharsets.UTF_8))));
            h4reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(h4Text.getBytes(StandardCharsets.UTF_8))));
            h5reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(h5Text.getBytes(StandardCharsets.UTF_8))));
            h6reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(h6Text.getBytes(StandardCharsets.UTF_8))));
            treader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(titleText.getBytes(StandardCharsets.UTF_8))));

            String line;
            String h1line;
            String h2line;
            String h3line;
            String h4line;
            String h5line;
            String h6line;
            String tline;

            //Since "line" is the longest string of them all it is the terminating condition of the while loop
            while ((line = breader.readLine()) != null) {
                String[] bwords = line.split("[^a-z0-9âäàåãáçõôöòóúûùüñšÿýž]+");
                //Removing stop words manually
                bwords = RemoveStopWords(stopwords, bwords);

                if ((h1line = h1reader.readLine()) != null) {
                    String[] h1words = h1line.split("[^a-z0-9âäàåãáçõôöòóúûùüñšÿýž]+");
                    h1words = RemoveStopWords(stopwords, h1words);
                    AddWords(h1words, line, titletext, stemmer, countMap, "h1", docUrl, countryName, date);

                }
                if ((h2line = h2reader.readLine()) != null) {
                    String[] h2words = h2line.split("[^a-z0-9âäàåãáçõôöòóúûùüñšÿýž]+");
                    h2words = RemoveStopWords(stopwords, h2words);
                    AddWords(h2words, line, titletext, stemmer, countMap, "h2", docUrl, countryName, date);

                }
                if ((h3line = h3reader.readLine()) != null) {
                    String[] h3words = h3line.split("[^a-z0-9âäàåãáçõôöòóúûùüñšÿýž]+");
                    h3words = RemoveStopWords(stopwords, h3words);
                    AddWords(h3words, line, titletext, stemmer, countMap, "h3", docUrl, countryName, date);

                }

                if ((h4line = h4reader.readLine()) != null) {
                    String[] h4words = h4line.split("[^a-z0-9âäàåãáçõôöòóúûùüñšÿýž]+");
                    h4words = RemoveStopWords(stopwords, h4words);
                    AddWords(h4words, line, titletext, stemmer, countMap, "h4", docUrl, countryName, date);

                }

                if ((h5line = h5reader.readLine()) != null) {
                    String[] h5words = h5line.split("[^a-z0-9âäàåãáçõôöòóúûùüñšÿýž]+");
                    h5words = RemoveStopWords(stopwords, h5words);
                    AddWords(h5words, line, titletext, stemmer, countMap, "h5", docUrl, countryName, date);

                }

                if ((h6line = h6reader.readLine()) != null) {
                    String[] h6words = h6line.split("[^a-z0-9âäàåãáçõôöòóúûùüñšÿýž]+");
                    h6words = RemoveStopWords(stopwords, h6words);
                    AddWords(h6words, line, titletext, stemmer, countMap, "h6", docUrl, countryName, date);

                }

                if ((tline = treader.readLine()) != null) {
                    String[] twords = tline.split("[^a-z0-9âäàåãáçõôöòóúûùüñšÿýž]+");
                    twords = RemoveStopWords(stopwords, twords);
                    AddWords(twords, line, titletext, stemmer, countMap, "title", docUrl, countryName, date);

                }


                totalCount = AddWords(bwords, line, titletext, stemmer, countMap, "body", docUrl, countryName, date);

            }
            //for (Word i : countMap.values()) {
            //    System.out.println( i.word + "\t" + i.bodyCount + "\t" + i.h1Count + "\t" + i.h2Count + "\t" + i.h3Count
            //            + "\t" + i.h4Count + "\t" + i.h5Count + "\t" + i.h6Count + "\t" + i.titleCount );
            //}

            for (Word i : countMap.values()) {
                i.termFrequency = (double) (i.bodyCount) / (totalCount);
                String json = gson.toJson(i);
                DBConnect.insertWord(i.word, json);
            /*ArrayList<Word> w = Words.get(i.word);
            if(w==null) {
               w = new ArrayList<>(  );
               w.add( i );
               Words.put( i.word , w );
                }
            else {
                w.add( i );
                Words.put( i.word, w );
                }*/
            }
            System.out.println("Document " + docId + " is done");
            docId++;
            DBConnect.update(url);

            //time = System.currentTimeMillis() - time;
            //System.out.println("Finished in " + time + " ms");
        }
/*
        System.out.println("Insertions started");
        for(ArrayList<Word> i : Words.values()) {
            String json = gson.toJson( i );
            System.out.println(json);
           DBConnect.insertWord( i.get( 0 ).word, json );
        }
*/
        breader.close();
        h1reader.close();
        h2reader.close();
        h3reader.close();
        h4reader.close();
        h5reader.close();
        h6reader.close();
        treader.close();

    }


    public static int AddWords(String[] words, String Line, String t, porterStemmer stemmer, Map<String, Word> countMap, String x, String url, String c, String d) {
        int count = 0;
        for (String word : words) {
            if ("".equals(word)) {
                continue;
            }
            String description = " ";
            if (x.equals("body")) {
                int start = Line.indexOf(word);
                if (start != -1) {
                    start = start - 50;
                    if (start < 0) start = 0;
                    int end = start + word.length();
                    end = end + 100;
                    if (end > Line.length()) end = Line.length() - 1;
                    description = Line.substring(start, end);
                    description = description.replace('\'', ' ').replace("'", "").replace('"', ' ').replace("\"", "");
                    start = description.indexOf(' ');
                    end = description.lastIndexOf(' ');
                    if ((start != -1) && (end != -1)) {
                        description = description.substring(start, end);
                    }
                }
            }

            //Perform Stemming
            stemmer.setCurrent(word);
            stemmer.stem();
            word = stemmer.getCurrent();

            //Perform stemming using Stemmer Class(chang both declaration in/out func of stemmer object)
            //word = stemmer.stem(word)

            Word wordObj = countMap.get(word);
            if (wordObj == null) {
                wordObj = new Word();
                wordObj.word = word;
                wordObj.h1Count = 0;
                wordObj.h2Count = 0;
                wordObj.h3Count = 0;
                wordObj.h4Count = 0;
                wordObj.h5Count = 0;
                wordObj.h6Count = 0;
                wordObj.bodyCount = 0;
                wordObj.titleCount = 0;

                countMap.put(word, wordObj);
            }
            if (x.equals("h1"))
                wordObj.h1Count++;
            else if (x.equals("h2"))
                wordObj.h2Count++;
            else if (x.equals("h3"))
                wordObj.h3Count++;
            else if (x.equals("h4"))
                wordObj.h4Count++;
            else if (x.equals("h5"))
                wordObj.h5Count++;
            else if (x.equals("h6"))
                wordObj.h6Count++;
            else if (x.equals("title"))
                wordObj.titleCount++;
            else
                wordObj.bodyCount++;

            wordObj.url = url;
            wordObj.country = c;
            wordObj.pubDate = d;
            if(! d.equals("not found")){
                System.out.println(d);

            }
            wordObj.title = t;
            wordObj.des = description;


        }
        for (Word i : countMap.values()) {
            count = count + i.bodyCount;
        }
        return count;

    }


    public static String[] RemoveStopWords(List<String> stopwords, String[] words) {
        //Removing stop words manually
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (!stopwords.contains(word)) {
                builder.append(word);
                builder.append(' ');
            }
        }
        String result = builder.toString().trim();
        words = result.split(" ");
        return words;
    }

}

