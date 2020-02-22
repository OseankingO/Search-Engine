# Search-Engine

## Introduction

This program is CS600 final project written by _**Xinghan Qin**_.

This Search Engine is designed for user to firstly grab title and main articles from the website. Then put all the articles into the Trie. And last, use search engine to search the most relative articles with user's input.

##

## Tools

Java, Jsoup

##

## To run

There are three main parts:

* Put all the URL into the **input.txt**. (Recommend BBC news, or during the second step, code needs to be changed.)

* Run **GetWebText.java**. (1. Change the path 2. If you used other website, make sure **doc.select("div.story-body__inner > p")** match the website design. Or you need to change the code.) You will get all the articles from your website with the file name is tiltle, and body is tiltle and article.

* Run **SearchEngine.java**. (Change the path) Type a string, then it will show:

  * If it is 0, then stop running.

  * If input does not contain any word, it will tell user the input is invalid.

  * If this word is in the article, it will show how many times and in which articles this word has showed. 

  * If this word is not in the article, then tell user this word is not in any article.

  * If all the words in input are not in the article, then tell user did't find any article related to all the words in the input.

** If some words in input are in the article, show which article are most relative to the input which based on total times of all the words in input appeared in this article. If more than one articles have same appeared times and the number is biggest, all of them will be display.

##

## Algorithms and Data Structure

The main algorithm this project applied is linked-list based Trie.

* Node

  * private String value: charactor or string
        
  * private List<int[]> articles: [[articleId1, times1], [articleId2, times2], ....]
        
  * private List<Node> children
  
* Trie

  * private Node root
        
  * private Map<Integer, String> map: {articleId : "articleTitle"}
        
  * private int size
  
When Add an article to the Tria, first, use split(), separate all the words. Add words into Tria one by one. When adding the word, from the root, search the is there prefix string of adding word in it's children. if no, get the first charactor of the adding word and create a new Node with the value equal the charactor, then add this Node to it's children list; if yes, remove the prefix string from adding word, and do same thing with this child and the substring. when the input word become empty, then add articleId to it's articles. If articleId is in articles, then add the times of this articleId. Hence, to add all N words in artcle, and average word length is L, then it costs O(NL) to run.

When searching the input, first use split(), separate all the words. Search words in Trie one by one. When searching the word, from the root, search the is there prefix string of adding word in it's children. if no, return false; if yes, do same thing with this child and the substring. If the input string becomes empty, return it's articles (articles can be null is this word didn' appear in any article). With input words number is N, and total Node number in Trie is M, the running time should be O(NlogM).

##

## Demo

The txt version of input example:

https://github.com/OseankingO/Search-Engine/blob/master/src/searchengine/input.txt

The example output of **GetWebText.java**:

https://github.com/OseankingO/Search-Engine/tree/master/src/searchengine/article

The testing and example output of **SearchEngine.java**:

https://github.com/OseankingO/Search-Engine/blob/master/src/searchengine/Output.txt

The video of demo: 

https://youtu.be/M_TN_GoKRcQ

##

## Testing

The the boundary conditions testing is included in txt version of output and video demo

##

## Author

* Xinghan Qin

##
