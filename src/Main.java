import com.maxmind.geoip2.exception.GeoIp2Exception;
import dbconnect.DBConnect;
import indexer.*;
import spider.*;
import ranker.*;

import java.io.IOException;

public class Main {


    public static void main(String[] args) throws GeoIp2Exception, InterruptedException, IOException {

        DBConnect.initDB();

        // Crawl
//        Spider.run(false, 100, 15);
//
//        // Index
        Indexer.main(args);
//
//        // Ranker
//        Ranker.pageRanking(10, 0.3);
    }

}
