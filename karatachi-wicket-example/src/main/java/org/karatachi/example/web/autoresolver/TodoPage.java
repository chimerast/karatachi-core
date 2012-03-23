package org.karatachi.example.web.autoresolver;

import java.util.Date;
import java.util.List;

import org.karatachi.example.component.S2PagerListView;
import org.karatachi.example.dao.TodoDao;
import org.karatachi.example.entity.Todo;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.auto.AutoResolveForm;
import org.karatachi.wicket.form.behavior.RequiredComponent;
import org.karatachi.wicket.listview.SimplePageableListViewNavigator;
import org.seasar.dao.pager.PagerCondition;
import org.seasar.framework.container.annotation.tiger.Binding;

public class TodoPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    @Binding
    private TodoDao todoDao;

    public TodoPage() {
        Todo todo = new Todo();
        todo.date = new Date();

        AutoResolveForm<Todo> form;
        add(form = new AutoResolveForm<Todo>("form", todo) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                todoDao.insert(getModelObject());
            }
        });
        form.setRequiredComponentBorder(new RequiredComponent("※必須"));

        S2PagerListView<Todo> list;
        add(list = new S2PagerListView<Todo>("list", 20) {
            private static final long serialVersionUID = 1L;

            @Override
            protected List<Todo> select(PagerCondition condition) {
                return todoDao.selectAll(condition);
            }

            @Override
            protected int count() {
                return todoDao.count();
            }
        });
        add(new SimplePageableListViewNavigator("navigator", list));
    }
}
