package icu.funkye.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import icu.funkye.entity.Product;
import icu.funkye.mapper.ProductMapper;
import icu.funkye.service.IProductService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {


    /**
     * 减库存
     *
     * @param productId
     * @param count
     */
    @Transactional(rollbackFor = Exception.class)
    public void deduct(Integer productId, int count) {
        if (productId == 222) {
            throw new RuntimeException("异常:模拟业务异常:product branch exception");
        }

        QueryWrapper<Product> wrapper = new QueryWrapper<>();
        wrapper.setEntity(new Product().setId(productId));
        Product product = baseMapper.selectOne(wrapper);
        product.setStock(product.getStock() - count);

        baseMapper.updateById(product);
    }
}
