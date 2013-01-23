package org.karatachi.text;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class StringHighlighter {
    private static Pattern NON_LETTER =
            Pattern.compile("(\\p{Punct}|\\p{Space}|　)+");

    private final Pattern pattern;

    private final String prefix;
    private final String suffix;

    private final int fragmentSize;
    private final int marginSize;

    public StringHighlighter(String keyword) {
        this(getHighlighterPattern(keyword), "<span class=\"highlight\">",
                "</span>");
    }

    public StringHighlighter(Pattern pattern, String prefix, String suffix) {
        this(pattern, prefix, suffix, 80, 20);
    }

    public StringHighlighter(Pattern pattern, String prefix, String suffix,
            int fragmentSize, int marginSize) {
        this.pattern = pattern;
        this.prefix = prefix;
        this.suffix = suffix;
        this.fragmentSize = fragmentSize;
        this.marginSize = marginSize;
    }

    public String getText(String content) {
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, prefix + matcher.group() + suffix);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public String getSummary(String content) {
        try {
            Matcher matcher = pattern.matcher(content);

            ArrayList<String> fragments = new ArrayList<String>();
            ArrayList<int[]> positions = new ArrayList<int[]>();

            while (matcher.find()) {
                int[] position = new int[] { matcher.start(), matcher.end() };
                if (positions.size() > 0
                        && matcher.start() > positions.get(0)[0]
                                + (fragmentSize - marginSize)) {
                    fragments.add(getFragment(content, positions));
                    positions.clear();
                    if (fragments.size() == 3) {
                        break;
                    }
                }
                positions.add(position);
            }
            if (positions.size() > 0) {
                fragments.add(getFragment(content, positions));
            }

            return StringUtils.join(fragments, " … ");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFragment(String content, ArrayList<int[]> positions) {
        int start = Math.max(positions.get(0)[0] - marginSize, 0);
        int end =
                Math.min(positions.get(0)[0] + (fragmentSize - marginSize),
                        content.length());

        StringBuilder ret = new StringBuilder();
        for (int[] p : positions) {
            ret.append(content.substring(start, p[0]));
            ret.append(prefix);
            ret.append(content.substring(p[0], p[1]));
            ret.append(suffix);
            start = p[1];
        }
        if (start < end) {
            ret.append(content.substring(start, end));
        }
        return ret.toString();
    }

    public static Pattern getHighlighterPattern(String str) {
        String search = str;
        search = NON_LETTER.matcher(search).replaceAll("|");
        if (search.startsWith("|")) {
            search = search.substring(1);
        }
        if (search.endsWith("|")) {
            search = search.substring(0, search.length() - 1);
        }
        return Pattern.compile("(" + search + ")");
    }
}
