package ru.smartflex.tools.dbf;

import org.junit.Test;
import ru.smartflex.tools.dbf.mem.MemBag;
import ru.smartflex.tools.dbf.mem.MemEngine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class Fp26MemReader {

    @Test
    public void testReadMemFile() {

        MemBag memBag = MemEngine.getMemBag(TestHelper.getFile("TEST_MEM.MEM"),
                "Cp866");

        // String (character)
        assertEquals("A", memBag.getString("tstr_1"));
        System.out.println("Read one symbol from variable: tstr_1: " + memBag.getString("tstr_1"));
        assertEquals("Hello mem", memBag.getString("tstr_eng"));
        System.out.println("Read english message from variable: tstr_eng: " + memBag.getString("tstr_eng"));
        assertEquals("Привет мем", memBag.getString("tstr_cp866"));
        System.out.println("Read russian message (Cp866) from variable: tstr_cp866: " + memBag.getString("tstr_cp866"));

        // Double (numeric)
        assertEquals(0.0d, memBag.getDouble("tnum_0"), 0.0d);
        System.out.println("\nRead 0 from variable: tnum_0: " + memBag.getDouble("tnum_0"));
        assertEquals(1.0d, memBag.getDouble("tnum_1"), 0.0d);
        System.out.println("Read 1 from variable: tnum_1: " + memBag.getDouble("tnum_1"));
        assertEquals(-1.0d, memBag.getDouble("tnum_1neg"), 0.0d);
        System.out.println("Read -1 from variable: tnum_1neg: " + memBag.getDouble("tnum_1neg"));
        assertEquals(3.1415926499999998d, memBag.getDouble("tnum_pi"), 0.0d);
        System.out.println("Read pi from variable: tnum_pi: " + memBag.getDouble("tnum_pi"));
        assertEquals(201401.18d, memBag.getDouble("tnum_rur"), 0.0d);
        System.out.println("Read 201401.18 from variable: tnum_rur: " + memBag.getDouble("tnum_rur"));
        assertEquals(-919.19d, memBag.getDouble("tnum_neg"), 0.0d);
        System.out.println("Read -919.19 from variable: tnum_neg: " + memBag.getDouble("tnum_neg"));

        // Boolean(logical)
        assertEquals(true, memBag.getBoolean("ttrue"));
        System.out.println("\nRead TRUE from variable: ttrue: " + memBag.getBoolean("ttrue"));
        assertEquals(false, memBag.getBoolean("tfalse"));
        System.out.println("Read FALSE from variable: tfalse: " + memBag.getBoolean("tfalse"));

        // Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = sdf.parse("2014.01.20");
            date2 = sdf.parse("1970.01.01");
        } catch (ParseException e) {
            assertEquals(true, false);
        }
        assertEquals(date1, memBag.getDate("tdt_140120"));
        System.out.println("\nRead 20.01.2014 from variable: tdt_140120: " + memBag.getDate("tdt_140120"));
        assertEquals(date2, memBag.getDate("tdt_700101"));
        System.out.println("Read 01.01.1970 from variable: tdt_700101: " + memBag.getDate("tdt_700101"));

        // Array
        assertEquals("A4", memBag.getArrayElement("tarr_one", 4));
        System.out.println("\nRead 'A4' from array el.: tarr_one(4): " + memBag.getArrayElement("tarr_one", 4));
        assertEquals(31.0d, (Double) memBag.getArrayElement("tarr_two", 3, 1), 0.0d);
        System.out.println("Read 31 from array el.: tarr_two(3,1): " + memBag.getArrayElement("tarr_two", 3, 1));
        assertEquals("A 3x2", memBag.getArrayElement("tarr_two", 3, 2));
        System.out.println("Read 'A 3x2' from array el.: tarr_two(3,2): " + memBag.getArrayElement("tarr_two", 3, 2));

    }

}

