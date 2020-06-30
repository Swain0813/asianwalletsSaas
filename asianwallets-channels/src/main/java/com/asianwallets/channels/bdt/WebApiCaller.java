package com.asianwallets.channels.bdt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface WebApiCaller {
    InputStream call(URL var1, byte[] var2) throws IOException;
}
