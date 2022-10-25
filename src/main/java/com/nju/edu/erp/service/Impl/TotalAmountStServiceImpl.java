package com.nju.edu.erp.service.Impl;

import com.nju.edu.erp.dao.TotalAmountStDao;
import com.nju.edu.erp.dao.TotalAmountStGiftDao;
import com.nju.edu.erp.enums.promotion.TotalAmountStState;
import com.nju.edu.erp.exception.MyServiceException;
import com.nju.edu.erp.model.po.promotion.TotalAmountStGiftPO;
import com.nju.edu.erp.model.po.promotion.TotalAmountStPO;
import com.nju.edu.erp.model.vo.promotion.TotalAmountStVO;
import com.nju.edu.erp.service.TotalAmountStService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TotalAmountStServiceImpl implements TotalAmountStService {

    @Autowired
    private TotalAmountStDao totalAmountStDao;

    @Autowired
    private TotalAmountStGiftDao totalAmountStGiftDao;

    @Override
    @Transactional
    public void updateStateById(Integer id, TotalAmountStState state) {
        int effectLines = totalAmountStDao.updateStateById(id, state);
        if (effectLines == 0) {
            throw new MyServiceException("00001", "状态更新错误");
        }
    }

    @Override
    @Transactional
    public void add(TotalAmountStVO totalAmountStVO) {
        if (totalAmountStDao.findByAmount(totalAmountStVO.getAmount()) != null) {
            throw new MyServiceException("00001", "已存在重复条目");
        }
        totalAmountStDao.add(totalAmountStVO.getAmount(), totalAmountStVO.getVoucher());
        TotalAmountStPO latest = totalAmountStDao.getLatest();
        Map<String, Integer> gifts = totalAmountStVO.getGifts();
        gifts.forEach((pid, quantity) -> {
            totalAmountStGiftDao.add(pid, quantity, latest.getId());
        });

    }

    @Override
    public List<TotalAmountStVO> getAll() {
        List<TotalAmountStPO> all = totalAmountStDao.findAll();
        List<TotalAmountStVO> ret = new ArrayList<>();
        for (TotalAmountStPO po : all) {
            TotalAmountStVO vo = new TotalAmountStVO();
            BeanUtils.copyProperties(po, vo);
            List<TotalAmountStGiftPO> giftPOS = totalAmountStGiftDao.selectByStId(vo.getId());
            Map<String, Integer> giftVOs = giftPOS.stream()
                    .collect(HashMap::new, (m, g)->m.put(g.getPid(), g.getQuantity()), HashMap::putAll);
            vo.setGifts(giftVOs);
            ret.add(vo);
        }
        return ret;
    }
}
