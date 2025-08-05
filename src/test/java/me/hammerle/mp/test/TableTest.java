package me.hammerle.mp.test;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;

import me.hammerle.mp.utils.Table;

public class TableTest {

    @Test
    public void testGetStartMiddleEnd() {
        Table table = new Table("§a", 4, 2);

        assertEquals("§a┌────┬──┐", table.getStart());
        assertEquals("§a├────┼──┤", table.getMiddle());
        assertEquals("§a└────┴──┘", table.getEnd());
    }

    @Test
    public void testGetWithLongInputAndColor() {
        Table table = new Table("§b", 2);
        String result = table.get("abcdefghij");
        String expected = "§b│ §rab§0" + Table.empty4 + Table.empty1 + "§b│";
        assertEquals(expected, result);
    }

    @Test
    public void testShortenWithColorCodes() throws Exception {
        Table table = new Table("§c", 1);
        Method shorten = Table.class.getDeclaredMethod("shorten", String.class, int.class);
        shorten.setAccessible(true);
        String shortened = (String) shorten.invoke(table, "§labcdef", 17);
        String expected = "§lab§0" + Table.empty2 + Table.empty1;
        assertEquals(expected, shortened);
    }
}

