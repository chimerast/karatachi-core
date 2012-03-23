package org.karatachi.example.dao;

import java.util.List;

import org.karatachi.example.entity.Todo;
import org.seasar.dao.annotation.tiger.Query;
import org.seasar.dao.annotation.tiger.S2Dao;
import org.seasar.dao.annotation.tiger.Sql;
import org.seasar.dao.pager.PagerCondition;

@S2Dao(bean = Todo.class)
public interface TodoDao {
    @Query("ORDER BY timestamp DESC")
    public List<Todo> selectAll(PagerCondition condition);

    @Sql("SELECT count(*) FROM Todo")
    public int count();

    public void insert(Todo message);
}
