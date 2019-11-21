package com.asianwallets.common.enums;
import com.asianwallets.common.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public enum Status implements Serializable {
    _0, _1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21, _22, _23, _24, _25, _26, _27, _28, _29, _30, _31, _32, _33, _34, _35, _36, _37, _38, _39, _40, _41, _42, _43, _44, _45, _46, _47, _48, _49, _50, _51, _52, _53, _54, _55, _56, _57, _58, _59, _60, _61, _62, _63, _64, _65, _66, _67, _68, _69, _70, _71, _72, _73, _74, _75, _76, _77, _78, _79, _80, _81, _82, _83, _84, _85, _86, _87, _88, _89, _90, _91, _92, _93, _94, _95, _96, _97, _98, _99, _100;

    public static int size = Status.values().length;

    Status() {
    }

    public static Status valueOf(int index) {

        if (index >= 0 && index < size) {
            return Status.values()[index];
        } else {
            throw new BusinessException("不存在的枚举坐标:｛" + index + "｝");
        }
    }

    public static ArrayList<String> frontCodes(int index, String prefix) {
        ArrayList<String> codes = Lists.newArrayList();
        for (int j = 0; j < index; j++) {
            codes.add(valueOf(j).getCode(prefix));
        }
        return codes;
    }

    public static ArrayList<String> laterCodes(int index, String prefix) {
        ArrayList<String> codes = Lists.newArrayList();
        for (int j = index; j < size; j++) {
            codes.add(valueOf(j).getCode(prefix));
        }
        return codes;
    }

    public String getCode(@NonNull String prefix) {
        return prefix.toUpperCase().concat(this.name());
    }


}
