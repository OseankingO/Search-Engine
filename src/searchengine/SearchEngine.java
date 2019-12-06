/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author sean
 */
public class SearchEngine {
    
    class Result {
            private Node n;
            private String s;

            public Result(Node n, String s) {
                this.n = n;
                this.s = s;
            }

            public Node getN() {
                return n;
            }

            public String getS() {
                return s;
            }
            
        }
    
    class Node {
        private String value;
        private List<int[]> articles;
        private List<Node> children;
        
        public Node() {
            children = new ArrayList<>();
        }

        public Node(String value) {
            this.value = value;
            children = new ArrayList<>();
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public List<int[]> getAricles() {
            return articles;
        }

        public void addAricles(int article, int times) {
            if(articles == null) 
                articles = new ArrayList<>();
            int[] tmp = new int[2];
            tmp[0] = article;
            tmp[1] = times;
            articles.add(tmp);
        }

        public List<Node> getChildren() {
            return children;
        }

        public void addChildren(Node child) {
            this.children.add(child);
        }
        
        public int compareWithString(String str) {
            // if word's length is smaller then this node is definitly not the prefix
            if(this.value.length() > str.length())
                return 0;
            String subString = str.substring(0, this.value.length());
            // if it's prefix then return the length of match
            if(subString.equals(this.value))
                return this.value.length();
            // not match return 0
            else
                return 0;
        }
        
        public Result findChild(String str) {
            for(Node child : this.children) {
                int res = child.compareWithString(str);
                // if there are node whose value is the prefix of the word, then return this child and the suffix
                if(res != 0){
                    str = str.substring(res);
                    return new Result(child, str);
                }
            }
            // null means no child match
            return null;
        }

        // add word into the trie
        private void addWord(String word, int articleId, int times) {
            // if input is "" means here is the end of the woed then we can add the articleId to this node
            if(word.equals("")) {
                addAricles(articleId, times);
            } else {
                Result res = findChild(word);
                // if the node is not exist, then we need to create an new one with next char of the word, and add word to next node 
                if(res == null) {
                    Node nextNode = new Node(word.substring(0, 1));
                    this.addChildren(nextNode);
                    nextNode.addWord(word.substring(1), articleId, times);
                } 
                //if the node exist, add substring to next node
                else {
                    Node nextNode = res.getN();
                    String nextString = res.getS();
                    nextNode.addWord(nextString, articleId, times);
                }
            }
        }

        private int[][] findWord(String inputWord) {
            if(inputWord.equals("")) {
                List<int[]> tmpArticles = this.getAricles();
                if(tmpArticles == null)
                    return null;
                int size = tmpArticles.size();
                int[][] res = new int[size][2];
                int i = 0;
                for(int[] article : tmpArticles) {
                    res[i][0] = article[0];
                    res[i][1] = article[1];
                    i ++;
                }
                return res;
            }
            Result res = this.findChild(inputWord);
            if(res == null) {
                return null;
            } else {
                Node nextNode = res.getN();
                String nextString = res.getS();
                return nextNode.findWord(nextString);
            }
        }

        private void rebuild() {
            if(this.getChildren().size() == 1 && this.getAricles() == null) {
                Node child = this.getChildren().get(0);
                List<Node> tmpChildren = child.getChildren();
                String tmpValue = this.getValue() + child.getValue();
                List<int[]> tmpArticles = child.getAricles();
                this.articles = tmpArticles;
                this.value = tmpValue;
                this.children = tmpChildren;
                this.rebuild();
            } else
            for(Node child : this.getChildren()) {
                child.rebuild();
            }
        }
    }
    
    class Trie {
        private Node root;
        private Map<Integer, String> map;
        private int size = 0;
        
        public Trie() {
            root = new Node();
            map = new HashMap<>();
        }

        public Map<Integer, String> getMap() {
            return map;
        }
        
        public int addArticle(String title, Map<String, Integer> article) {
            int id = this.size;
            this.size += 1;
            // put the article into map with its id
            map.put(id, title);
            
            // put article into trie
            for(String key : article.keySet()) {
                int times = article.get(key);
                this.root.addWord(key, id, times);
            }
            return id;
        }

        private int[][] findWord(String inputWord) {
            return this.root.findWord(inputWord);
        }

        private void rebuild() {
            this.root.rebuild();
        }
    }
    
    //read all the articles in article file
    public List<String[]> readArticlesFromDirectory(String path) throws FileNotFoundException, IOException {
        List<String[]> res = new ArrayList();
        final File directory = new File(path);
        for(final File article : directory.listFiles()) {
            BufferedReader br = new BufferedReader(new FileReader(article));
            String line;
            String[] str = new String[2];
            int label = 0;
            while((line = br.readLine()) != null) {
                if(label == 0) {
                    str[0] = line;
                    str[1] = "";
                    label ++; 
                }
                str[1] += line;
            }
            
            res.add(str);
        }
        return res;
    }
    
    public Map<String, Integer> putIntoDictinary(String body) {
        // use partten to sparate the body
        String[] words = body.split("[(0-9)@#£$%\\-\\d\\#\\t\\s\n\\,\\;\\:\\<\\>\\(\\)\\[\\]\\!\\?\"\\'\\{\\}\\_\\+\\=\\/\\*]+");
        Map<String, Integer> res = new HashMap<>();
        for(String word : words) {
            if(word.equals("") || word.length() == 1)
                continue;
            word = word.toLowerCase();
            if(res.get(word) == null) {
                res.put(word, 1);
            } else {
                res.put(word, res.get(word) + 1);
            }
        }
        return res;
    }
    
    // use articles to build a trie
    public Trie createTrie(List<String[]> articles) {
        Trie trie = new Trie();
        for(String[] article : articles) {
            Map<String, Integer> res = putIntoDictinary(article[1]);
            trie.addArticle(article[0], res);
        }
        return trie;
    }
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        SearchEngine se = new SearchEngine();
        String path = "/Users/sean/Desktop/Stevens/600_Algorithm/project/SearchEngine/src/searchengine/article/";
        System.out.println("WELCOME TO SEAN'S SEARCHING ENGINE VERSION 1.0\n");
        System.out.println("Loading articles ...");
        List<String[]> articles = se.readArticlesFromDirectory(path);
        Trie trie = se.createTrie(articles);
        trie.rebuild();
        System.out.println("Loading completed!\n");
        Map<Integer, String> map = trie.getMap();
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter a string (0 to stop):"); 
        String input = sc.nextLine(); 
        // input your words and it will show how many times the words show in the articles
        // type 0 to stop
        while(!input.equals("0")) {
            // when your input is nothing then there is no input
            boolean noInput = false;
            System.out.println("\n*************************************************************************************************"); 
            if(input.equals("")) {
                noInput = true;
            }
            // split input sentense into words
            String[] inputArr = input.split("[\\.(1-9)@#£$%\\-\\d\\#\\t\\s\n\\,\\;\\:\\<\\>\\(\\)\\[\\]\\!\\?\"\\'\\{\\}\\_\\+\\=\\/\\*]+");
            List<Map<String, int[][]>> general = new ArrayList<>();
            List<int[]> times = new ArrayList<>();
            // if after split there is nothing then that means you input nothing
            if(inputArr.length == 0) {
                noInput = true;
            }
            else {
                for(String inputWord : inputArr) {
                    // skip the empty part
                    if(inputWord.equals((""))) 
                        continue;
                    inputWord = inputWord.toLowerCase();
                    // use trie to find the article and appeared times of this word
                    int[][] tmp = trie.findWord(inputWord);
                    // if the word cannot be found in trie then response user and skip
                    if(tmp == null) {
                        String output = inputWord + " is not in any articles.";
                        System.out.println(output);
                        System.out.println("-------------------------------------------------------------------------------------------------");
                        continue;
                    }
                    // store in an arraylist to compare the most relative article
                    for (int i = 0; i < tmp.length; i ++) {
                        if(times.isEmpty()) {
                            int[] tmpArr = new int[2];
                            tmpArr[0] = tmp[i][0];
                            tmpArr[1] = tmp[i][1];
                            times.add(tmpArr);
                        } else {
                            boolean find = false;
                            for(int[] arr : times) {
                                if(arr[0] == tmp[i][0]) {
                                    arr[1] = arr[1] + tmp[i][1];
                                    find = true;
                                    break;
                                }
                            }
                            if(! find) {
                                int[] tmpArr = new int[2];
                                tmpArr[0] = tmp[i][0];
                                tmpArr[1] = tmp[i][1];
                                times.add(tmpArr);
                            }
                        }
                    }
                    // remember the word and the article and appeared times of this word
                    Map<String, int[][]> tmpMap = new HashMap<>();
                    tmpMap.put(inputWord, tmp);
                    general.add(tmpMap);
                }
            }
            // show the word and how many times it appeared in which article
            if(!general.isEmpty()) {
                for(Map<String, int[][]> res : general) {
                    for(String word : res.keySet()) {
                        System.out.println(word + " has appeared:");
                        int[][] relateArticle = res.get(word);
                        int size = relateArticle.length;
                        for(int i = 0; i < size; i ++) {
                            String articleName = map.get(relateArticle[i][0]);
                            String out = relateArticle[i][1] + " times in \"" + articleName + "\"";
                            System.out.println(out);
                        }
                    }
                    System.out.println("-------------------------------------------------------------------------------------------------");
                }
            } 
            // show which article are most relative to the input
            // rand base total times of all the words in input appeared in this article. 
            // if more than one articles have same appeared times and the number is biggest, I will show all of them
            if(! times.isEmpty()) {
                List<Integer> maxTimeArticle = new ArrayList<>();
                int maxTime = 0;
                System.out.println("Test total times of all the words in input appeared in article:");
                for(int[] arr : times) {
                    System.out.println(arr[1] + ": \"" + map.get(arr[0]) + "\"");
                    if(arr[1] > maxTime) {
                        maxTimeArticle = new ArrayList<>();
                        maxTime = arr[1];
                        maxTimeArticle.add(arr[0]);
                    } else if(arr[1] == maxTime) {
                        maxTimeArticle.add(arr[0]);
                    }
                }
                System.out.println("-------------------------------------------------------------------------------------------------");
                if(maxTimeArticle.size() == 1) {
                    System.out.println("The most relative article is:");
                } else {
                    System.out.println("The most relative articles are:");
                }
                for(int articleId : maxTimeArticle) {
                    String articleName = "\"" + map.get(articleId) + "\"";
                    System.out.println(articleName);
                }
                System.out.println("*************************************************************************************************");
            }
            // if no input, tell user input is not invalid
            else if(noInput) {
                System.out.println("Your input is not invalid!");
                System.out.println("*************************************************************************************************");
            }
            // if in the trie cannot find any words in input, tell user didn' find.
            else {
                System.out.println("Did't find any article related to all the words you type!");
                System.out.println("*************************************************************************************************");
            }
            System.out.println(); 
            System.out.println("Please enter a string (0 to stop):"); 
            sc = new Scanner(System.in);
            input = sc.nextLine();  
        }
    System.out.println("\nThanks for using, bye!");    
    }
    
}
