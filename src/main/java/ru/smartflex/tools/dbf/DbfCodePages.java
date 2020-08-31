package ru.smartflex.tools.dbf;

/**
 * Enumeration of the supported code pages
 *
 * @author galisha
 * @since 1.00
 */
public enum DbfCodePages {

    Cp437(1, "Cp437"), Cp850(0x02, "Cp850"), Cp1252(0x03, "Cp1252"),
    Cp865(0x08, "Cp865"), Cp437_2(0x09, "Cp437"), Cp850_2(0x0A, "Cp850"),
    Cp437_3(0x0B, "Cp437"), Cp437_4(0x0D, "Cp437"), Cp850_3(0x0E, "Cp850"),
    Cp437_5(0x0F, "Cp437"), Cp850_4(0x10, "Cp850"), Cp437_6(0x11, "Cp437"),
    Cp850_5(0x12, "Cp850"), MS932(0x13, "MS932"), Cp850_6(0x14, "Cp850"),
    Cp437_7(0x15, "Cp437"), Cp850_7(0x16, "Cp850"), Cp865_1(0x17, "Cp865"),
    Cp437_8(0x18, "Cp437"), Cp437_9(0x19, "Cp437"), Cp850_8(0x1A, "Cp850"),
    Cp437_10(0x1B, "Cp437"), Cp863(0x1C, "Cp863"), Cp850_9(0x1D, "Cp850"),
    Cp852(0x1F, "Cp852"), Cp852_1(0x22, "Cp852"), Cp852_2(0x23, "Cp852"),
    Cp860(0x24, "Cp860"), Cp850_10(0x25, "Cp850"), Cp866(0x26, "Cp866"),
    Cp850_11(0x37, "Cp850"), Cp852_3(0x40, "Cp852"), MS936(0x4D, "MS936"),
    Cp949(0x4E, "Cp949"), Cp950(0x4F, "Cp950"), Cp874(0x50, "Cp874"),
    ANSI(0x57, "ANSI"), Cp1252_1(0x58, "Cp1252"), Cp1252_2(0x59, "Cp1252"),
    Cp852_4(0x64, "Cp852"), Cp866_1(0x65, "Cp866"), Cp865_2(0x66, "Cp865"),
    Cp861(0x67, "Cp861"), TIS620(0x69, "TIS620"),
    Cp737(0x6A, "Cp737"), Cp857(0x6B, "Cp857"), Cp863_1(0x6C, "Cp863"),
    Cp950_1(0x78, "Cp950"), Cp949_1(0x79, "Cp949"), MS936_1(0x7A, "MS936"),
    MS932_1(0x7B, "MS932"), Cp874_1(0x7C, "Cp874"), Cp737_1(0x86, "Cp737"),
    Cp852_5(0x87, "Cp852"), Cp857_1(0x88, "Cp857"), Cp1250(0xC8, "Cp1250"),
    Cp1251(0xC9, "Cp1251"), Cp1254(0xCA, "Cp1254"), Cp1253(0xCB, "Cp1253"),
    Cp1257(0xCC, "Cp1257");

    private int dbfCode;
    private String charsetName;

    DbfCodePages(int dbfCode, String charsetName) {
        this.dbfCode = dbfCode;
        this.charsetName = charsetName;
    }

    protected int getDbfCode() {
        return dbfCode;
    }

    protected String getCharsetName() {
        return charsetName;
    }

    protected static DbfCodePages getByDbfCode(int code) {
        DbfCodePages dcp = null;

        for (DbfCodePages dcps : DbfCodePages.values()) {
            if (dcps.getDbfCode() == code) {
                dcp = dcps;
                break;
            }
        }

        return dcp;
    }

    /**
     * Defines DbfCodePage by charset name
     * @param charsetName charset name
     * @return DbfCodePages
     * @since 1.11
     */
    protected static DbfCodePages getByCharsetName(String charsetName) {
        DbfCodePages dcp = null;

        for (DbfCodePages dcps : DbfCodePages.values()) {
            if (dcps.getCharsetName().equals((charsetName))) {
                dcp = dcps;
                break;
            }
        }

        return dcp;
    }
}
