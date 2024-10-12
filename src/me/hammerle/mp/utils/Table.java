package me.hammerle.mp.utils;

import java.util.HashMap;

public class Table {
    private final static HashMap<Character, Integer> SIZE = new HashMap<>();
    public static String empty4 = " ";
    public static String empty2 = "˼";
    public static String empty1 = "ˈ";

    public static void addSizeMapping(char c, int size) {
        SIZE.put(c, size);
    }

    private final String color;
    private final int[] widths;

    public Table(String color, int... widths) {
        this.color = color;
        this.widths = widths;
    }

    public String getStart() {
        return getLine('┌', '┬', '┐');
    }

    public String getMiddle() {
        return getLine('├', '┼', '┤');
    }

    public String getEnd() {
        return getLine('└', '┴', '┘');
    }

    private String getLine(char start, char middle, char end) {
        StringBuilder sb = new StringBuilder(color).append(start);
        for(int width : widths) {
            for(int j = 0; j < width; j++) {
                sb.append('─');
            }
            sb.append(middle);
        }
        sb.setCharAt(sb.length() - 1, end);
        return sb.toString();
    }

    public String get(String... args) {
        StringBuilder sb = new StringBuilder(color).append("│ ");
        for(int i = 0; i < widths.length; i++) {
            sb.append("§r");
            sb.append(shorten(args[i], 9 * widths[i] - 1));
            sb.append(color).append("│ ");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private String shorten(String s, int max) {
        int sum = 0;
        int fat = 0;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(c == '§') {
                sb.append('§');
                i++;
                if(i < s.length()) {
                    char cc = Character.toLowerCase(s.charAt(i));
                    if(cc == 'l') {
                        fat = 1;
                    } else if(cc != 'm' && cc != 'n' && cc != 'o' && cc != 'k') {
                        fat = 0;
                    }
                    sb.append(cc);
                }
                continue;
            }
            int width = SIZE.getOrDefault(c, 6) + fat;
            if(sum + width > max) {
                break;
            }
            sum += width;
            sb.append(c);
        }
        sb.append("§0");
        while(sum + 4 <= max) {
            sb.append(empty4);
            sum += 4;
        }
        while(sum + 2 <= max) {
            sb.append(empty2);
            sum += 2;
        }
        while(sum + 1 <= max) {
            sb.append(empty1);
            sum++;
        }
        return sb.toString();
    }
}
