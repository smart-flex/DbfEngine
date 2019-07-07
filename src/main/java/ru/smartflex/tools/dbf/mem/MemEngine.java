package ru.smartflex.tools.dbf.mem;

import java.io.File;
import java.io.InputStream;

/**
 * The <b>main class</b> that supplies .mem reader - start here. <br>
 * <br>
 * The <b>reader</b> may be used in that manner: <br>
 * First of all you have to get <code>MemBag</code> object (don't forget to
 * specify encoding):
 * <pre>
 * MemBag memBag = MemEngine.getMemBag(
 * 		Fp26MemReader.class.getResourceAsStream(&quot;TEST_MEM.MEM&quot;), &quot;Cp866&quot;);
 *
 * </pre>
 * Then you may get value from simple variables and arrays. For example:
 * <pre>
 *
 * memBag.getString(&quot;tstr_1&quot;); // get value from simple variable
 * memBag.getArrayElement(&quot;tarr_two&quot;, 3, 2); // get value from array element
 *
 * </pre>
 * Information about mem file structure was read from
 * http://www.clicketyclick.dk/databases/xbase/format/index.html
 *
 * @author galisha
 * @since 1.05
 */

public class MemEngine {

    private MemEngine() {
    }

    /**
     * Gets MemBag object from .mem file
     *
     * @param memFileName file name
     * @param enc         encoding
     * @return MemBag object
     * @since 1.05
     */
    public static MemBag getMemBag(String memFileName, String enc) {
        File memFile = new File(memFileName);
        return getMemBag(memFile, enc);
    }

    /**
     * Gets MemBag object from .mem file
     *
     * @param path        path to file
     * @param memFileName file name
     * @param enc         encoding
     * @return MemBag object
     * @since 1.05
     */
    public static MemBag getMemBag(String path, String memFileName, String enc) {
        File memFile = new File(path, memFileName);
        return getMemBag(memFile, enc);
    }

    /**
     * Gets MemBag object from .mem file
     *
     * @param memFile .mem file
     * @param enc     encoding
     * @return MemBag object
     * @since 1.05
     */
    public static MemBag getMemBag(File memFile, String enc) {
        return new MemBag(memFile, enc);
    }

    /**
     * Gets MemBag object from input stream
     *
     * @param memStream stream
     * @param enc       encoding
     * @return MemBag object
     * @since 1.05
     */
    public static MemBag getMemBag(InputStream memStream, String enc) {
        return new MemBag(memStream, enc);
    }

}
