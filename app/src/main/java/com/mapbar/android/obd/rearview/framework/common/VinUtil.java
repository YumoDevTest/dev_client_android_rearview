package com.mapbar.android.obd.rearview.framework.common;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by THINKPAD on 2016/2/25.
 */
public class VinUtil {
    public static Map<Character, Integer> kv = new HashMap<Character, Integer>();

    public static Map<Integer, Integer> wv = new HashMap<Integer, Integer>();

    static {

        for (int i = 0; i < 10; i++) {
            kv.put(String.valueOf(i).charAt(0), i);
        }

        kv.put('a', 1);
        kv.put('b', 2);
        kv.put('c', 3);
        kv.put('d', 4);
        kv.put('e', 5);
        kv.put('f', 6);
        kv.put('g', 7);
        kv.put('h', 8);
        kv.put('j', 1);
        kv.put('k', 2);
        kv.put('l', 3);
        kv.put('m', 4);
        kv.put('n', 5);
        kv.put('p', 7);
        kv.put('q', 8);
        kv.put('r', 9);
        kv.put('s', 2);
        kv.put('t', 3);
        kv.put('u', 4);
        kv.put('v', 5);
        kv.put('w', 6);
        kv.put('x', 7);
        kv.put('y', 8);
        kv.put('z', 9);

        wv.put(1, 8);
        wv.put(2, 7);
        wv.put(3, 6);
        wv.put(4, 5);
        wv.put(5, 4);
        wv.put(6, 3);
        wv.put(7, 2);
        wv.put(8, 10);
        wv.put(10, 9);
        wv.put(11, 8);
        wv.put(12, 7);
        wv.put(13, 6);
        wv.put(14, 5);
        wv.put(15, 4);
        wv.put(16, 3);
        wv.put(17, 2);

    }

    public static void main(String[] args) {
        System.out.println(vinVerifChar("ABDdeGD".trim().toUpperCase(), 8));
//    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    	System.out.println(sdf.format(Long.parseLong("1438159777025")));
//    	String files = "D:/60-OBD/统计数据/20150605/vin_car.csv";
//
//    	try {
//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(files)),"gb18030"));
//			while(br.ready()){
//				String line = br.readLine();
//				String[] lines = line.split(",");
//				String vin = lines[0];
//				if(!isLegal(vin)){
//					System.out.println(vin);
//				}
//			}
//			br.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//        System.out.println(isLegal("JM6LY103200200387"));
//        System.out.println(isLegal("LFV3A21K7D4262398"));
//        System.out.println(isLegal("LFV3A23C793062656"));
//        System.out.println(isLegal("LSGDC82C11S10203O"));
//        System.out.println(isLegal("LSGDC82C11S102030"));
//        System.out.println(isLegal("LSGUD84X2BE041557"));
//        System.out.println(isLegal("WDDBF4CB2EJ143048"));
//        System.out.println(isLegal("WDDBF4CB2EJ143048"));

    }

    /**
     * VIN验证输入字符是否正确
     * 要求传入的字母为大写
     *
     * @param vin vin码
     * @param len vin长度
     * @return
     */
    public static boolean vinVerifChar(String vin, int len) {
        return vin.matches("[A-Z0-9]{" + len + "}");
    }

    public final static boolean isLegal(String vin) {
        if (null == vin) {
            return false;
        } else if (vin.trim().length() == 17) {
            vin = vin.trim().toLowerCase();
            char[] codes = vin.toCharArray();

            /**
             * if the 9th is not number ,return false
             */
            int resultInCode = 0;
            if ("0123456789x".contains(vin.subSequence(8, 9))) {
                if (vin.subSequence(8, 9).toString().equals("x")) {
                    resultInCode = 10;
                } else {
                    resultInCode = Integer.valueOf(vin.subSequence(8, 9).toString());
                }
            } else {
                return false;
            }

            int total = 0;
            for (int i = 1; i < codes.length + 1; i++) {
                char code = codes[i - 1];

                if (kv.containsKey(code)) {
                    if (9 == i) {
                        continue;
                    } else {
                        total += kv.get(code) * wv.get(i);
                    }
                } else {
                    return false;
                }
            }
            return resultInCode == total % 11;
        } else {
            return false;
        }
    }
}
