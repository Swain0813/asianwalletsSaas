package com.asianwallets.base.service.impl;
import com.alibaba.fastjson.JSON;
import com.asianwallets.base.dao.ProductMapper;
import com.asianwallets.base.service.ProductService;
import com.asianwallets.common.base.BaseServiceImpl;
import com.asianwallets.common.constant.AsianWalletConstant;
import com.asianwallets.common.dto.ProductDTO;
import com.asianwallets.common.entity.Product;
import com.asianwallets.common.exception.BusinessException;
import com.asianwallets.common.redis.RedisService;
import com.asianwallets.common.response.EResultEnum;
import com.asianwallets.common.utils.IDS;
import com.asianwallets.common.vo.ProductVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import java.util.Date;
import java.util.List;

/**
 * 产品管理模块的实现
 */
@Slf4j
@Service
@Transactional
public class ProductServiceImpl extends BaseServiceImpl<Product> implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisService redisService;


    /**
     * @Author YangXu
     * @Date 2019/12/5
     * @Descripate 添加产品
     * @return
     **/
    @Override
    public int addProduct(String name, ProductDTO productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO,product);
        Example example = new Example(Product.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("payType", product.getPayType());
        criteria.andEqualTo("currency", product.getCurrency());
        List<Product> list = productMapper.selectByExample(example);
        if (list.size() > 0) {
            throw new BusinessException(EResultEnum.REPEATED_ADDITION.getCode());
        }
        String id = IDS.uuid2();
        product.setId(id);
        product.setEnabled(true);
        product.setCreator(name);
        product.setCreateTime(new Date());
        int num = productMapper.insert(product);

        Product p = productMapper.selectByPrimaryKey(id);
        try {
            //审核通过后将新增和修改的机构信息添加的redis里
            redisService.set(AsianWalletConstant.PRODUCT_CACHE_CODE_KEY.concat("_").concat(p.getProductCode().toString()), JSON.toJSONString(p));
            redisService.set(AsianWalletConstant.PRODUCT_CACHE_TYPE_KEY.concat("_").concat(p.getPayType()).concat("_").concat(p.getCurrency()).concat("_").concat(p.getTradeDirection().toString()), JSON.toJSONString(p));
        } catch (Exception e) {
            log.error("将产品信息同步到redis里发生错误：" + e.getMessage());
            throw new BusinessException(EResultEnum.ERROR_REDIS_UPDATE.getCode());
        }
        return num;
    }

    /**
     * @Author YangXu
     * @Date 2019/12/5
     * @Descripate 更新产品
     * @return
     **/
    @Override
    public int updateProduct(String username, ProductDTO productDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(productDTO,product);
        product.setId(productDTO.getProductId());
        product.setUpdateTime(new Date());
        product.setModifier(username);
        int num = productMapper.updateByPrimaryKeySelective(product);

        Product p = productMapper.selectByPrimaryKey(productDTO.getProductId());
        try {
            //审核通过后将新增和修改的机构信息添加的redis里
            redisService.set(AsianWalletConstant.PRODUCT_CACHE_CODE_KEY.concat("_").concat(p.getProductCode().toString()), JSON.toJSONString(p));
            redisService.set(AsianWalletConstant.PRODUCT_CACHE_TYPE_KEY.concat("_").concat(p.getPayType()).concat("_").concat(p.getCurrency()).concat("_").concat(p.getTradeDirection().toString()), JSON.toJSONString(p));
        } catch (Exception e) {
            log.error("将产品信息同步到redis里发生错误：" + e.getMessage());
            throw new BusinessException(EResultEnum.ERROR_REDIS_UPDATE.getCode());
        }

        return num;
    }

    /**
     * @Author YangXu
     * @Date 2019/12/5
     * @Descripate 分页查询产品
     * @return
     **/
    @Override
    public PageInfo<ProductVO> pageProduct(ProductDTO productDTO) {
        return new PageInfo<ProductVO>(productMapper.pageProduct(productDTO));
    }

    /**
     * @Author YangXu
     * @Date 2019/12/6
     * @Descripate 查询产品
     * @return
     **/
    @Override
    public List<ProductVO> selectProduct(ProductDTO productDTO) {
        return productMapper.selectProduct(productDTO);
    }

    /**
     * @return
     * @Author YangXu
     * @Date 2019/1/29
     * @Descripate 根据支付方式查询所有产品
     **/
    @Override
    public List<Product> selectProductByPayType(String payType, String language) {
        return productMapper.selectProductByPayType(payType, language);
    }
}
