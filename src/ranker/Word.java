package ranker;

public class Word implements Comparable<Word> {
    public String url;
    public String title;
    public String des;
    public String  word;
    public String country;
    public String pubDate;
    public double termFrequency;
    public int bodyCount;
    public int h1Count;
    public int h2Count;
    public int h3Count;
    public int h4Count;
    public int h5Count;
    public int h6Count;
    public int titleCount;


    @Override
    public  int hashCode() { return word.hashCode();
    }

    @Override
    public boolean equals(Object obj) { return word.equals(((Word)obj).word); }

    @Override
    public  int compareTo(Word b) { return b.bodyCount - bodyCount; }


}