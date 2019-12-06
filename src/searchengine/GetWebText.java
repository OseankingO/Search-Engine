/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author sean
 */
public class GetWebText {
    
    // read URL from resourse file  
    public List<String> readURL(String filePath) throws IOException {
        List<String> res = new ArrayList();
        File file =new File(filePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String URL;
        while((URL = reader.readLine()) != null) {
            res.add(URL);
        }
        return res;
    }
    
    // read the title and main body store from given URL and write it into txt file
    public void writeWebBody(String URL, String path) throws IOException {
        Document doc = Jsoup.connect(URL).get();
        String title = doc.title();
        String fileName = title + ".txt";
        // this part may need to be changed if we read from other news website, this one is just for BBC news website
        String body = doc.select("div.story-body__inner > p").text();
        PrintWriter writer = new PrintWriter(path + "article/" + fileName, "UTF-8");
        writer.println(title);
        writer.println(body);
        writer.close();
    }
    
    public static void main(String[] args) throws IOException {
        GetWebText rwt = new GetWebText();
        String path = "/Users/sean/Desktop/Stevens/600_Algorithm/project/SearchEngine/src/searchengine/";
        String filePath = path + "resource.txt";
        List<String> URLs = rwt.readURL(filePath);
        for(String URL : URLs) {
            rwt.writeWebBody(URL, path);
        }
//        Map<Integer, Integer> a = new HashMap<>();
//        a.put(1, 2);
//        System.out.print(a.get(2) == null);
    }
}
