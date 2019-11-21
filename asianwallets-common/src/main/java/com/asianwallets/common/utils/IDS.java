package com.asianwallets.common.utils;
import com.google.common.collect.ImmutableList;

import java.text.SimpleDateFormat;
import java.util.*;

public class IDS implements Constant {
    /**
     * 从输入list中随机返回一个对象.
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> T randomOne(List<T> list) {
        Collections.shuffle(list);
        return list.get(0);
    }


    /**
     * 从输入list中随机返回n个对象.
     *
     * @param list
     * @param n
     * @param <T>
     * @return
     */
    public static <T> List<T> randomSome(List<T> list, int n) {
        Collections.shuffle(list);
        return list.subList(0, n);
    }

    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间有-分割.
     *
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
     *
     * @return
     */
    public static String uuid2() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     *
     * @category：生成主键ID编号
     *@author RyanCai
     *@Time :2016年1月7日 下午5:06:02
     * @param guidType：2位前缀大写字母
     * @return
     */
    public static String getUUID(String guidType) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return guidType +dateFormat.format(new Date())+ getRandomInt(5);
    }
    /**
     *
     * @category：生成任意位数的随机数（其中只能包含：数子）
     *@author RyanCai
     *@Time :2015年9月6日 下午12:01:45
     * @param length
     * @return
     */
    public static String getRandomInt(int length){
        String str="0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number =random.nextInt(10);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * UUID TO HashCode
     *
     * @return
     */
    public static Integer uuidHashCode() {
        return System.identityHashCode(uuid2());
    }

    private static final IdWorker idWorker;

    static {
      /*  int workerId = Math.abs(NetUtil.getLocalMac().hashCode()) % 31;
        int dataCenterId = Math.abs(NetUtil.getLocalHost().hashCode()) % 31;
        idWorker = new IdWorker(workerId, dataCenterId, workerId + dataCenterId);*/
        idWorker =new IdWorker();
    }

    /**
     * @return 全局ID（Mac+IP+时间）
     */
    public static Long uniqueID() {
        return idWorker.getId();
    }

    /**
     * @param length
     * @return
     */
    public static String randomNumber(int length) {
        return randomLength(NUMBER_SEQUENCE, length);
    }

    /**
     * @param immu
     * @param length
     * @return
     */
    public static String randomLength(ImmutableList<Character> immu, int length) {
        StringBuffer sb = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            sb.append(immu.get(randomtInt(immu.size())));
        }
        return sb.toString();
    }

    /**
     * @param max
     * @return
     */
    public static Integer randomtInt(int max) {
        return random.nextInt(max);
    }

    /**
     * 生成6位随机字母数字组合
     * @param length
     * @return
     */
    public static String getStringRandom(int length) {

        String val = "";
        Random random = new Random();
        //length为几位密码
        for(int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + temp);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }


}
