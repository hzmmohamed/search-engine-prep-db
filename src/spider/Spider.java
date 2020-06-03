package spider;

import dbconnect.DBConnect;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

public class Spider {

        public static void run(boolean recrawl, int MaxCap, int threadsCount) throws InterruptedException, IOException {
        Set<String> visited = new HashSet<String>();
        CustomQueue<String> toVisit = new CustomQueue<String>();
        int c = DBConnect.getLastVisited(visited);
        if(recrawl)
        {
            toVisit.addAll(visited);
            LinksStock b = new LinksStock(MaxCap+toVisit.size(),toVisit,true);
            Thread[] threads = new Thread[threadsCount];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(new Supplier(b));
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }
            System.out.println("Finished crawling");
            b.page();
            b.printData();
        }
        else if(c>=MaxCap)
        {
            visited = new HashSet<>();
            System.out.println("DATA BASE IS ALREADY FULL FEHA: " + c +" Site\nThe Program will start recrawling Current links");
            int Max = 100;
            DBConnect.getNextCrawl(visited,new Timestamp(System.currentTimeMillis()),Max);
            if(visited.size()>0)
            {
                toVisit.addAll(visited);
                LinksStock b = new LinksStock(MaxCap+toVisit.size(),toVisit,true);
                Thread[] threads = new Thread[threadsCount];
                for (int i = 0; i < threads.length; i++) {
                    threads[i] = new Thread(new Supplier(b));
                    threads[i].start();
                }

                for (Thread thread : threads) {
                    thread.join();
                }
                System.out.println("Finished crawling");
                b.page();
                b.printData();
            }
            else
                {
                    System.out.println("nothing scheduled for recrawling at the mean time");
                }
        }
        else if(c!=0)
        {
            Set<String> auxilary = new HashSet<String>();
            DBConnect.getLatestFinalState(auxilary,toVisit);
            LinksStock b = new LinksStock(MaxCap,toVisit,auxilary,visited,false);
            b.printData();
            Thread[] threads = new Thread[threadsCount];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(new Supplier(b));
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }
            System.out.println("Finished crawling");
            b.page();
            b.printData();
        }
        else
            {
                toVisit.add("https://www.forbesafrica.com/"); //1
                toVisit.add("https://www.theverge.com/tech"); //2
                toVisit.add("https://techcrunch.com/"); //3
                toVisit.add("https://www.wired.com/category/culture/"); //4
                toVisit.add("https://www.wired.com/tag/open-source/"); //5
                toVisit.add("https://stackoverflow.com/questions?tab=Active"); //9
                toVisit.add("https://www.nationalgeographic.com/"); //10
                toVisit.add("https://vimeo.com/watch"); //11
                toVisit.add("https://www.brainpickings.org/"); //12
                toVisit.add("https://seths.blog/"); //13

                LinksStock b = new LinksStock(MaxCap,toVisit,false);
                Thread[] threads = new Thread[threadsCount];
                for (int i = 0; i < threads.length; i++) {
                    threads[i] = new Thread(new Supplier(b));
                    threads[i].start();
                }

                for (Thread thread : threads) {
                    thread.join();
                }
                System.out.println("Finished crawling");
                b.page();
                b.printData();
        }
    }
}