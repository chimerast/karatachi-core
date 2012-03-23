package org.karatachi.example.dao;

import java.util.List;

import org.karatachi.example.entity.MBean;
import org.seasar.dao.annotation.tiger.S2Dao;
import org.seasar.dao.pager.PagerCondition;

@S2Dao(bean = MBean.class)
public interface MBeanDao {
    public List<MBean> selectAll(PagerCondition condition);
}
