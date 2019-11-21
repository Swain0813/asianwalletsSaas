package com.asianwallets.common.utils.validation.impl;

import java.text.DecimalFormat;

public class DecimalFormatImpl extends DecimalFormat {

    private static final long serialVersionUID = -1224133129562317059L;

    public DecimalFormatImpl(String format) {
        super(format);
    }

    public synchronized float formatFloat(Object data) {

        try {
            return Float.parseFloat(super.format(data).trim());
        } catch (NumberFormatException e) {
            return 0;
        }

    }
}
