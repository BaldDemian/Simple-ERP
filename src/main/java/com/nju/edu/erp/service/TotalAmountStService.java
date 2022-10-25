package com.nju.edu.erp.service;

import com.nju.edu.erp.enums.promotion.TotalAmountStState;
import com.nju.edu.erp.model.vo.promotion.TotalAmountStVO;

import java.util.List;

public interface TotalAmountStService {

    void updateStateById(Integer id, TotalAmountStState state);

    void add(TotalAmountStVO totalAmountStVO);

    List<TotalAmountStVO> getAll();
}
