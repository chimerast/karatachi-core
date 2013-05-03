package org.karatachi.jmx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.openmbean.CompositeData;
import javax.swing.tree.TreeNode;

import org.apache.commons.lang.ArrayUtils;

public class MBeanTree extends AbstractTreeNode {
    private static final long serialVersionUID = 1L;

    private final MBeanServerWrapper server;

    public MBeanTree(MBeanServerWrapper server) {
        super(null);

        this.server = server;

        String[] domains = server.get().getDomains();
        Arrays.sort(domains);
        for (String domain : domains) {
            addChild(new DomainNode(this, domain));
        }
    }

    @Override
    public String getName() {
        return "ROOT";
    }

    @Override
    public Object getValue() {
        return "";
    }

    public class DomainNode extends AbstractTreeNode {
        private static final long serialVersionUID = 1L;

        private final String domain;

        private DomainNode(TreeNode parent, String domain) {
            super(parent);
            this.domain = domain;
            for (MBeanWrapper child : MBeanWrapper.createDomainTree(server,
                    domain).values()) {
                addChild(new BeanNode(this, child));
            }
        }

        @Override
        public String getName() {
            return domain;
        }

        @Override
        public Object getValue() {
            return "";
        }
    }

    public class BeanNode extends AbstractTreeNode {
        private static final long serialVersionUID = 1L;

        private final MBeanWrapper bean;

        private BeanNode(TreeNode parent, MBeanWrapper bean) {
            super(parent);
            this.bean = bean;

            for (MBeanWrapper child : bean.getChildren()) {
                addChild(new BeanNode(this, child));
            }
            for (MBeanAttributeInfo attr : bean.getAttributes()) {
                addChild(new AttributeNode(this, bean, attr));
            }
            for (MBeanOperationInfo op : bean.getOperations()) {
                addChild(new OperationNode(this, bean, op));
            }
        }

        @Override
        public String getName() {
            return bean.getName();
        }

        @Override
        public Object getValue() {
            return "";
        }
    }

    public class AttributeNode extends AbstractTreeNode {
        private static final long serialVersionUID = 1L;

        private final MBeanWrapper bean;
        private final MBeanAttributeInfo info;

        private AttributeNode(TreeNode parent, MBeanWrapper bean,
                MBeanAttributeInfo info) {
            super(parent);
            this.bean = bean;
            this.info = info;
        }

        @Override
        public String getName() {
            return MBeanUtils.toSimpleClassName(info.getType()) + " "
                    + info.getName();
        }

        @Override
        public Object getValue() {
            try {
                return MBeanUtils.toSimpleObject(bean.get(info));
            } catch (Exception e) {
                return "error: " + e.getMessage();
            }
        }

        public void setValue(Object value) throws JMException {
            bean.set(info, value);
        }

        public boolean isEditable() {
            return info.isWritable();
        }

        public String getType() {
            return MBeanUtils.toSimpleClassName(info.getType());
        }
    }

    public class OperationNode extends AbstractTreeNode {
        private static final long serialVersionUID = 1L;

        private final MBeanWrapper bean;
        private final MBeanOperationInfo info;

        private OperationNode(TreeNode parent, MBeanWrapper bean,
                MBeanOperationInfo info) {
            super(parent);
            this.bean = bean;
            this.info = info;
        }

        @Override
        public String getName() {
            StringBuilder sb = new StringBuilder();
            sb.append(MBeanUtils.toSimpleClassName(info.getReturnType()));
            sb.append(" ");
            sb.append(info.getName());
            sb.append("(");

            boolean hasParam = false;
            for (MBeanParameterInfo param : info.getSignature()) {
                sb.append(MBeanUtils.toSimpleClassName(param.getType()));
                sb.append(" ");
                sb.append(param.getName());
                sb.append(", ");
                hasParam = true;
            }

            if (hasParam) {
                sb.delete(sb.length() - 2, sb.length());
            }

            sb.append(")");

            return sb.toString();
        }

        @Override
        public Object getValue() {
            return "";
        }

        public boolean isInvokable() {
            return info.getSignature().length == 0;
        }

        public Object invoke() throws JMException {
            return MBeanUtils.toSimpleObject(bean.invoke(info));
        }
    }

    private static class MBeanUtils {
        public static String toSimpleClassName(String type) {
            try {
                return Class.forName(type).getSimpleName();
            } catch (ClassNotFoundException e) {
                return type;
            }
        }

        public static Object toSimpleObject(Object value) {
            if (value == null) {
                return null;
            }

            if (value.getClass().isArray()) {
                return ArrayUtils.toString(value);
            }

            if (value instanceof CompositeData) {
                ArrayList<String> list = new ArrayList<String>();
                CompositeData data = (CompositeData) value;
                for (String key : data.getCompositeType().keySet()) {
                    list.add(key + "=" + data.get(key));
                }
                return ArrayUtils.toString(list.toArray());
            }

            return value.toString();
        }
    }
}

abstract class AbstractTreeNode implements TreeNode, MBeanNode, Serializable {
    private static final long serialVersionUID = 1L;

    private final TreeNode parent;
    private final Vector<TreeNode> children;

    public AbstractTreeNode(TreeNode parent) {
        this.parent = parent;
        this.children = new Vector<TreeNode>();
    }

    @Override
    public abstract String getName();

    @Override
    public abstract Object getValue() throws JMException;

    protected void addChild(TreeNode node) {
        children.add(node);
    }

    @Override
    public Enumeration<TreeNode> children() {
        return children.elements();
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public int getIndex(TreeNode node) {
        return children.indexOf(node);
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public boolean isLeaf() {
        return children.size() == 0;
    }
}
