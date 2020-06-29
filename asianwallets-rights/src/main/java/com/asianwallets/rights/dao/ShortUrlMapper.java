package com.asianwallets.rights.dao;

import com.asianwallets.common.base.BaseMapper;
import com.asianwallets.common.entity.ShortUrl;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortUrlMapper extends BaseMapper<ShortUrl> {

    String getUrl(@Param("shortUrl") String shortUrl);

}