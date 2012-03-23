package org.karatachi.example.dao;

import java.util.List;

import org.karatachi.example.entity.Todo;
import org.seasar.dao.annotation.tiger.S2Dao;

@S2Dao(bean = Todo.class)
public interface TodoDao {
    public List<Todo> select();

    public void insert(Todo message);
}
