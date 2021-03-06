package org.jccode.webcrawler.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ContentExtractor
 * <p>
 * 自动抽取页面正文，代码来源；https://code.google.com/archive/p/cx-extractor/
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2020/2/1 20:00
 * @Version 1.0
 **/
public class ContentExtractor extends SinglePatternExtractor {

    private final static int blocksWidth = 3;
    private static int threshold = 86;
    private List<String> lines;
    private List<Integer> indexDistribution;
    private boolean flag;
    private int start;
    private int end;
    private StringBuilder text;

    public ContentExtractor() {
        this.text = new StringBuilder();
        this.start = -1;
        this.end = -1;
        flag = false;
        lines = new ArrayList<>();
        indexDistribution = new ArrayList<>();
    }

    /**
     * 返回抽取到的页面正文
     *
     * @param html
     * @return
     */
    @Override
    public String extract(String html) {
        // pre process
        html = html.replaceAll("(?is)<!DOCTYPE.*?>", "");
        html = html.replaceAll("(?is)<!--.*?-->", "");                // remove html
        // comment
        html = html.replaceAll("(?is)<script.*?>.*?</script>", ""); // remove javascript
        html = html.replaceAll("(?is)<style.*?>.*?</style>", "");   // remove css
        html = html.replaceAll("&.{2,5};|&#.{2,5};", " ");            // remove special
        // char
        html = html.replaceAll("(?is)<.*?>", "");

        lines = Arrays.asList(html.split("\n"));
        indexDistribution.clear();

        for (int i = 0; i < lines.size() - blocksWidth; i++) {
            int wordsNum = 0;
            for (int j = i; j < i + blocksWidth; j++) {
                lines.set(j, lines.get(j).replaceAll("\\s+", ""));
                wordsNum += lines.get(j).length();
            }
            indexDistribution.add(wordsNum);
        }

//        start = -1;
//        end = -1;
        boolean boolstart = false, boolend = false;
        text.setLength(0);

        for (int i = 0; i < indexDistribution.size() - 1; i++) {
            if (indexDistribution.get(i) > threshold && !boolstart) {
                if (indexDistribution.get(i + 1) != 0
                        || indexDistribution.get(i + 2) != 0
                        || indexDistribution.get(i + 3) != 0) {
                    boolstart = true;
                    start = i;
                    continue;
                }
            }
            if (boolstart) {
                if (indexDistribution.get(i) == 0
                        || indexDistribution.get(i + 1) == 0) {
                    end = i;
                    boolend = true;
                }
            }
            StringBuilder tmp = new StringBuilder();
            if (boolend) {
                //System.out.println(start+1 + "\t\t" + end+1);
                for (int ii = start; ii <= end; ii++) {
                    if (lines.get(ii).length() < 5) continue;
                    tmp.append(lines.get(ii)).append("\n");
                }
                String str = tmp.toString();
                if (str.contains("Copyright")) continue;
                text.append(str);
                boolstart = boolend = false;
            }
        }
        return text.toString();
    }


    @Override
    public List<String> extractList(String html) {
        throw new UnsupportedOperationException("ContentExtractor only extract page " +
                "content.");
    }
}
