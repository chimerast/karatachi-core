package org.karatachi.wicket.system;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.LoadableDetachableModel;
import org.karatachi.classloader.PackageDir;

public class PackageLinkList extends WebComponent {
    private static final long serialVersionUID = 1L;

    public PackageLinkList(String id, String packageName) {
        super(id);
        setDefaultModel(new PackageListModel(packageName));

    }

    @Override
    protected void onComponentTagBody(final MarkupStream markupStream,
            final ComponentTag openTag) {
        StringBuilder sb = new StringBuilder();
        writeTree(sb, (ClassTreeNode) getDefaultModelObject());
        replaceComponentTagBody(markupStream, openTag, sb.toString());
    }

    private void writeTree(StringBuilder sb, ClassTreeNode node) {
        sb.append("<dl>");

        String packageName = node.pkg.getName();
        packageName = packageName.substring(packageName.lastIndexOf(".") + 1);
        if (node.pkg.isAnnotationPresent(PackageLinkName.class)) {
            packageName = node.pkg.getAnnotation(PackageLinkName.class).value();
        }

        sb.append("<dt>");
        sb.append(packageName);
        for (ClassTreeNode child : node.packages) {
            writeTree(sb, child);
        }
        sb.append("</dt>");

        for (Class<? extends Page> clazz : node.classes) {
            sb.append("<dd>");
            sb.append(String.format("<a href=\"%s\">", urlFor(clazz, null)));
            String className = clazz.getSimpleName();
            if (clazz.isAnnotationPresent(PackageLinkName.class)) {
                className = clazz.getAnnotation(PackageLinkName.class).value();
            }
            sb.append(className);
            sb.append("</a>");
            sb.append("</dd>");
        }

        sb.append("</dl>");
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        tag.setType(XmlTag.OPEN);
    }

    private class ClassTreeNode {
        public Package pkg;
        public List<ClassTreeNode> packages = new ArrayList<ClassTreeNode>();
        public List<Class<? extends Page>> classes =
                new ArrayList<Class<? extends Page>>();
    }

    private class PackageListModel extends
            LoadableDetachableModel<ClassTreeNode> {
        private static final long serialVersionUID = 1L;

        private final String packageName;

        public PackageListModel(String packageName) {
            this.packageName = packageName;
        }

        @Override
        protected ClassTreeNode load() {
            return loadClasses(packageName);
        }

        private ClassTreeNode loadClasses(String packageName) {
            ClassTreeNode ret = new ClassTreeNode();

            PackageDir packageDir = new PackageDir(packageName);

            for (Class<? extends Page> clazz : packageDir.getClasses(Page.class)) {
                if ((clazz.getModifiers() & Modifier.ABSTRACT) == 0) {
                    Constructor<? extends Page> c;
                    try {
                        c = clazz.getConstructor(PageParameters.class);
                    } catch (NoSuchMethodException ignore) {
                        try {
                            c = clazz.getConstructor();
                        } catch (NoSuchMethodException e) {
                            continue;
                        }
                    }
                    if ((c.getModifiers() & Modifier.PUBLIC) != 0) {
                        ret.classes.add(clazz);
                    }
                }
            }

            for (String child : packageDir.getPackageNames()) {
                ret.packages.add(loadClasses(packageName + "." + child));
            }

            ret.pkg = Package.getPackage(packageName);

            return ret;
        }
    }
}
