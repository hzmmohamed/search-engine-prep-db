package ranker;

public class Url implements Comparable<Url>{
    String url;
    Double rank;

    Url()
    {
        setRank(0.0);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRank(Double rank) {
        this.rank = rank;
    }

    public Double getRank() {
        return rank;
    }

    @Override
    public int compareTo(Url o) {
        return o.getRank().compareTo(this.getRank());
    }
}
