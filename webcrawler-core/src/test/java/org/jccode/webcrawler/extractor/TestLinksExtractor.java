package org.jccode.webcrawler.extractor;

import org.junit.Test;

import java.io.*;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TestLinksExtractor
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/1 20:37
 * @Version 1.0
 **/
public class TestLinksExtractor {


    @Test
    public void testMatcherGroupCount() throws IOException {
        String path = "C:\\Users\\a1098\\Desktop\\test.html";
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(new File(path))));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        Pattern pattern = LinksExtractor.urlPattern;
        Matcher matcher = pattern.matcher(sb.toString());
        int count = 0;
        boolean found = matcher.find();
        while (found) {
            count++;
            found = matcher.find();
        }
        System.out.println("count of urls = " + count);
    }

    @Test
    public void testSelectList() throws IOException {
        String path = "C:\\Users\\a1098\\Desktop\\test.html";
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(new File(path))));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        LinksExtractor extractor = new LinksExtractor();
        List<String> list = extractor.extractList(sb.toString());
        System.out.println("list.size() = " + list.size());
        list.forEach(System.out::println);
    }

    @Test
    public void testNoDuplicatedList() throws IOException {
        String path = "C:\\Users\\a1098\\Desktop\\test.html";
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(new File(path))));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        LinksExtractor extractor = new LinksExtractor();
        List<String> duplicatedList = extractor.extractList(sb.toString());
        List<String> noDuplicateList = extractor.extractNoDuplicateList(sb.toString());
        System.out.println(duplicatedList.size());
        System.out.println(noDuplicateList.size());
    }
}
