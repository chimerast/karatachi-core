package org.karatachi.example.web;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupException;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.collections.ReadOnlyIterator;
import org.karatachi.classloader.PackageDir;

public class LinkListView extends AbstractRepeater {
    private static final long serialVersionUID = 1L;

    public LinkListView(String id, String packageName) {
        super(id, new PackagePageModel(packageName));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPopulate() {
        List<Class<? extends WebPage>> list =
                (List<Class<? extends WebPage>>) getDefaultModelObject();
        for (int i = 0; i < list.size(); ++i) {
            if (get(Integer.toString(i)) == null) {
                add(new LinkListItem(i, list.get(i)));
            }
        }
    }

    @Override
    protected Iterator<Component> renderIterator() {
        final int size = size();
        return new ReadOnlyIterator<Component>() {
            private int index = 0;

            public boolean hasNext() {
                return index < size;
            }

            public Component next() {
                final String id = Integer.toString(index);
                index++;
                return get(id);
            }
        };
    }

    private static class PackagePageModel extends
            LoadableDetachableModel<List<Class<? extends WebPage>>> {
        private static final long serialVersionUID = 1L;

        private final String packageName;

        public PackagePageModel(String packageName) {
            this.packageName = packageName;
        }

        @Override
        protected List<Class<? extends WebPage>> load() {
            return new PackageDir(packageName).getClasses(WebPage.class);
        }
    }

    private static class LinkListItem extends WebMarkupContainer {
        private static final long serialVersionUID = 1L;

        private boolean wasOpenCloseTag = false;

        public LinkListItem(int id, Class<? extends WebPage> pageClass) {
            super(Integer.toString(id).intern(),
                    new Model<Class<? extends WebPage>>(pageClass));
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            if (tag.isOpenClose()) {
                wasOpenCloseTag = true;
                tag.setType(XmlTag.OPEN);
            }
            super.onComponentTag(tag);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onComponentTagBody(MarkupStream markupStream,
                ComponentTag openTag) {

            Class<? extends WebPage> pageClass =
                    (Class<? extends WebPage>) getDefaultModelObject();
            getResponse().write(
                    "<a href=\"" + urlFor(pageClass, null) + "\">"
                            + pageClass.getSimpleName() + "</a>");

            if (wasOpenCloseTag == false) {
                markupStream.skipRawMarkup();
                if (markupStream.get().closes(openTag) == false) {
                    throw new MarkupException("close tag not found for tag: "
                            + openTag.toString() + ". Component: "
                            + this.toString());
                }
            }
        }
    }
}
