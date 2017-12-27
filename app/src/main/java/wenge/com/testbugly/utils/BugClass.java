package wenge.com.testbugly.utils;

/**
 * 测试bug类.
 *
 * @author devilwwj
 * @since 2016/10/25
 */
public class BugClass {

    public String bug() {
        // 这段代码会报空指针异常
//        String str = null;
//        Log.e("BugClass", "get string length:" + str.length());
        return "在更新包的基础上再进行更新";
    }
}
