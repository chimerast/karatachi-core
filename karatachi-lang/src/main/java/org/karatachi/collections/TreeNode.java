package org.karatachi.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode<T extends Serializable & Comparable<? super T>>
        implements Serializable, Comparable<TreeNode<T>> {
    private static final long serialVersionUID = 1L;

    private final T value;
    private transient ArrayList<TreeNode<T>> parents;
    private ArrayList<TreeNode<T>> children;

    public TreeNode(T value) {
        this.value = value;
        this.parents = new ArrayList<TreeNode<T>>();
        this.children = new ArrayList<TreeNode<T>>();
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

    public void addChild(TreeNode<T> node) {
        checkLoop(node);
        children.add(node);
        node.parents.add(this);
    }

    private void checkLoop(TreeNode<T> node) {
        for (TreeNode<T> parent : node.parents) {
            if (parent == this) {
                throw new IllegalStateException("added node is looped.");
            } else {
                checkLoop(parent);
            }
        }
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

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException,
            ClassNotFoundException {
        stream.defaultReadObject();
        this.parents = new ArrayList<TreeNode<T>>();
        for (TreeNode<T> child : children) {
            child.parents.add(this);
        }
    }

    public static interface Visitor<S extends Serializable & Comparable<? super S>> {
        boolean visit(TreeNode<S> node);
    }
}
