package org.karatachi.example.web.form;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.karatachi.example.dao.TodoDao;
import org.karatachi.example.entity.Todo;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.auto.AutoResolveForm;
import org.karatachi.wicket.auto.AutoResolveListView;
import org.seasar.framework.container.annotation.tiger.Binding;

public class TodoPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    @Binding
    private TodoDao todoDao;

    public TodoPage() {
        AutoResolveForm<Todo> form;
        add(form = new AutoResolveForm<Todo>("form", new Todo()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                todoDao.insert(getModelObject());
            }
        });
        form.setRequiredComponentBorder(new RequiredComponentBorder());

        IModel<List<Todo>> model = new LoadableDetachableModel<List<Todo>>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected List<Todo> load() {
                return todoDao.select();
            };
        };
        add(new AutoResolveListView<Todo>("list", model));
    }
}
