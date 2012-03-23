package org.karatachi.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class TreeNode<T extends Serializable & Comparable<? super T>>
        implements Serializable, Comparable<TreeNode<T>> {
    private static final long serialVersionUID = 1L;

    private final T value;
    private List<TreeNode<T>> parents;
    private List<TreeNode<T>> children;
    private boolean unmodifiable;

    public TreeNode(T value) {
        if (value == null) {
            throw new IllegalStateException(
                    "TreeNode doesn't accept null value.");
        }
        this.value = value;
        this.parents = new ArrayList<TreeNode<T>>();
        this.children = new ArrayList<TreeNode<T>>();
    }

    @Override
    public String toString() {
        return "TreeNode(value='" + value + "')";
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TreeNode) {
            return getValue().equals(((TreeNode<T>) obj).getValue());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public int compareTo(TreeNode<T> o) {
        return getValue().compareTo(o.getValue());
    }

    public T getValue() {
        return value;
    }

    public List<TreeNode<T>> getParents() {
        return parents;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void setUnmodifiable() {
        if (unmodifiable) {
            return;
        }
        this.unmodifiable = true;
        this.parents = Collections.unmodifiableList(parents);
        this.children = Collections.unmodifiableList(children);
        for (TreeNode<T> child : children) {
            child.setUnmodifiable();
        }
    }

    protected void checkModifiable() {
        if (unmodifiable) {
            throw new IllegalStateException("TreeNode is unmodifiable.");
        }
    }

    public void addChild(TreeNode<T> node) {
        checkModifiable();
        checkLoop(this, node, new LinkedList<T>());
        children.add(node);
        node.parents.add(this);
    }

    public void removeChild(TreeNode<T> node) {
        checkModifiable();
        children.remove(node);
        node.parents.remove(this);
    }

    public void removeChildren() {
        for (TreeNode<T> child : new ArrayList<TreeNode<T>>(children)) {
            removeChild(child);
        }
    }

    private void checkLoop(TreeNode<T> node, TreeNode<T> newnode,
            LinkedList<T> stack) {
        stack.addFirst(node.value);
        for (TreeNode<T> parent : node.parents) {
            if (parent == newnode) {
                while (parent != null) {
                    stack.addFirst(parent.value);
                    if (parent.parents.size() > 0) {
                        parent = parent.parents.get(0);
                    } else {
                        parent = null;
                    }
                }

                StringBuilder sb = new StringBuilder();
                for (T value : stack) {
                    sb.append("<-" + value);
                }
                throw new IllegalStateException(
                        "Added node is looped: adding='" + newnode.getValue()
                                + "', to='" + sb.toString() + "'");
            } else {
                checkLoop(parent, newnode, stack);
            }
        }
        stack.removeFirst();
    }

    public TreeNode<T> getChild(int index) {
        return children.get(index);
    }

    public boolean hasChild() {
        return children.size() != 0;
    }

    public void accept(Visitor<T> visitor) {
        if (visitor.visit(this)) {
            for (TreeNode<T> child : children) {
                child.accept(visitor);
            }
        }
    }

    public static interface Visitor<S extends Serializable & Comparable<? super S>> {
        boolean visit(TreeNode<S> node);
    }

    /**
     * ツリーのshallow copyを生成する
     */
    public static <S extends Serializable & Comparable<? super S>> TreeNode<S> duplicateTree(
            TreeNode<S> node) {
        return duplicateTree(node, new NodeFactory<S, TreeNode<S>>() {
            public org.karatachi.collections.TreeNode<S> newInstance(S value) {
                return new TreeNode<S>(value);
            };
        });
    }

    /**
     * ツリーのshallow copyを生成する（ノードの重複を許す）
     */
    public static <S extends Serializable & Comparable<? super S>> TreeNode<S> duplicateTreeFlat(
            TreeNode<S> node) {
        return duplicateTreeFlat(node, new NodeFactory<S, TreeNode<S>>() {
            public org.karatachi.collections.TreeNode<S> newInstance(S value) {
                return new TreeNode<S>(value);
            };
        });
    }

    protected interface NodeFactory<S extends Serializable & Comparable<? super S>, P extends TreeNode<S>> {
        public P newInstance(S value);
    }

    protected static <S extends Serializable & Comparable<? super S>, P extends TreeNode<S>> P duplicateTree(
            TreeNode<S> node, NodeFactory<S, P> creator) {
        return duplicateTree(node, creator, new TreeMap<S, P>());
    }

    private static <S extends Serializable & Comparable<? super S>, P extends TreeNode<S>> P duplicateTree(
            TreeNode<S> origin, NodeFactory<S, P> creator, TreeMap<S, P> store) {
        if (store.containsKey(origin.getValue())) {
            return store.get(origin.getValue());
        } else {
            P dest = creator.newInstance(origin.getValue());
            store.put(dest.getValue(), dest);
            for (TreeNode<S> child : origin.getChildren()) {
                dest.addChild(duplicateTree(child, creator, store));
            }
            return dest;
        }
    }

    protected static <S extends Serializable & Comparable<? super S>, P extends TreeNode<S>> P duplicateTreeFlat(
            TreeNode<S> origin, NodeFactory<S, P> creator) {
        P dest = creator.newInstance(origin.getValue());
        for (TreeNode<S> child : origin.getChildren()) {
            dest.addChild(duplicateTreeFlat(child, creator));
        }
        return dest;
    }
}
