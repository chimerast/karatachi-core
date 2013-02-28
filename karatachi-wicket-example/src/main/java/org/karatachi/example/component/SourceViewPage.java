package org.karatachi.example.component;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.karatachi.classloader.PackageDir;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.script.SyntaxHilighterLabel;

public class SourceViewPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    private Class<?> clazz;

    public SourceViewPage(PageParameters parameters) {
        remove("packages");
        remove("source");

        if (parameters.getNamedKeys().contains("class")) {
            try {
                clazz = Class.forName(parameters.get("class").toString());
            } catch (ClassNotFoundException ignore) {
            }
        }

        if (clazz == null) {
            setResponsePage(Application.get().getHomePage());
            return;
        }

        add(new Label("pageTitle", new LoadableDetachableModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected String load() {
                return clazz.getName();
            }
        }));

        add(new ListView<Class<?>>("classes", new PackageDir(
                clazz.getPackage().getName()).getClasses()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Class<?>> item) {
                final Class<?> clazz = item.getModelObject();

                Link<Void> link;
                item.add(link = new Link<Void>("link") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick() {
                        SourceViewPage.this.clazz = clazz;
                    }
                });
                link.add(new Label("name", clazz.getSimpleName()));
            }
        });

        add(new SyntaxHilighterLabel("java", "Java", "Eclipse",
                new SourceModel("java")));
        add(new SyntaxHilighterLabel("html", "Xml", "Eclipse", new SourceModel(
                "html")));
    }

    public class SourceModel extends LoadableDetachableModel<String> {
        private static final long serialVersionUID = 1L;

        private final String type;

        public SourceModel(String type) {
            this.type = type;
        }

        @Override
        protected String load() {
            String ret = null;
            try {
                ret =
                        IOUtils.toString(
                                clazz.getResourceAsStream(clazz.getSimpleName()
                                        + "." + type), "UTF-8");
            } catch (Exception e) {
                ret = "NO DATA";
            }
            return ret;
        }
    }
}
