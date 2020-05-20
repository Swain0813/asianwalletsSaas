package com.asianwallets.common.dto.th.tlv;

/**
 * @description:
 * @author: YangXu
 * @create: 2020-05-20 16:07
 **/
public class TLV{
    int tag;//tag定义成数字是为了方便遍历使用
    int len;
    byte[] value;

    public int getTag() {
        return tag;
    }
    public void setTag(int tag) {
        this.tag = tag;
    }
    public int getLen() {
        return len;
    }
    public void setLen(int len) {
        this.len = len;
    }
    public byte[] getValue() {
        return value;
    }
    public void setValue(byte[] value) {
        this.value = value;
    }
}
