package priv.liuxy.utils;

import java.math.BigDecimal;

/**
 * Created by Liuxy on 2016/3/22.
 * 单位转换工具类
 */
public class UnitUtils {

    /**
     * 换算到指定的容量级别，保留小数点后两位
     *
     * @param bytes
     * @param memorySizeUnit
     * @return
     */
    public static String bytes2Specified(long bytes, MemorySizeUnit memorySizeUnit) {
        BigDecimal fileSize = new BigDecimal(bytes);

        BigDecimal specified = new BigDecimal(memorySizeUnit.getSize());
        float returnValue = fileSize.divide(specified, 2, BigDecimal.ROUND_UP).floatValue();
        return returnValue + memorySizeUnit.toString();
    }

    public enum MemorySizeUnit {
        GB(2 ^ 30), MB(2 ^ 20), KB(2 ^ 10);

        private int size;

        MemorySizeUnit(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }
}
