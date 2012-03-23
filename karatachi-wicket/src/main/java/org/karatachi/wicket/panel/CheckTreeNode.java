package org.karatachi.wicket.panel;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.karatachi.collections.TreeNode;

public class CheckTreeNode<T extends Serializable & Comparable<T>> extends
        TreeNode<T> {
    private static final long serialVersionUID = 1L;

    private boolean check = false;
    private boolean open = false;
    private boolean visible = true;
    private boolean leaf = false;

    public CheckTreeNode() {
        super(null);
    }

    public CheckTreeNode(T value) {
        super(value);
    }

    public boolean isCheck() {
        return check;
    }

    public CheckTreeNode<T> setCheck(boolean check) {
        this.check = check;
        return this;
    }

    public boolean isOpen() {
        return open;
    }

    public CheckTreeNode<T> setOpen(boolean open) {
        this.open = open;
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public CheckTreeNode<T> setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public CheckTreeNode<T> setLeaf(boolean leaf) {
        this.leaf = leaf;
        return this;
    }

    public void select(final Selector<T> selector) {
        if (selector == null) {
            accept(new Visitor<T>() {
                @Override
                public boolean visit(CheckTreeNode<T> node) {
                    node.setOpen(false);
                    node.setVisible(true);
                    return true;
                }
            });
        } else {
            accept(new Visitor<T>() {
                @Override
                public boolean visit(CheckTreeNode<T> node) {
                    node.setOpen(false);
                    node.setVisible(false);
                    return true;
                }
            });

            accept(new Visitor<T>() {
                @Override
                public boolean visit(CheckTreeNode<T> node) {
                    if (selector.select(node)) {
                        open(node);
                        node.accept(new Visitor<T>() {
                            @Override
                            public boolean visit(CheckTreeNode<T> node) {
                                node.setOpen(true);
                                node.setVisible(true);
                                return true;
                            }
                        });
                        return false;
                    } else {
                        return true;
                    }
                }

                @Override
                public void down() {
                    selector.down();
                }

                @Override
                public void up() {
                    selector.up();
                }

                private void open(CheckTreeNode<T> node) {
                    node.setOpen(true);
                    node.setVisible(true);
                    for (TreeNode<T> parent : node.getParents()) {
                        open((CheckTreeNode<T>) parent);
                    }
                }
            });
        }
    }

    public void accept(Visitor<T> visitor) {
        visitor.down();
        if (visitor.visit(this)) {
            for (TreeNode<T> child : getChildren()) {
                ((CheckTreeNode<T>) child).accept(visitor);
            }
        }
        visitor.up();
    }

    public static <S extends Serializable & Comparable<S>> CheckTreeNode<S> duplicateTree(
            TreeNode<S> node) {
        CheckTreeNode<S> newnode = new CheckTreeNode<S>(node.getValue());
        for (TreeNode<S> child : node.getChildren()) {
            newnode.addChild(duplicateTree(child));
        }
        return newnode;
    }

    public static abstract class Visitor<S extends Serializable & Comparable<S>> {
        public abstract boolean visit(CheckTreeNode<S> node);

        public void down() {
        }

        public void up() {
        }
    }

    public static abstract class Selector<S extends Serializable & Comparable<S>> {
        public abstract boolean select(CheckTreeNode<S> node);

        public void down() {
        }

        public void up() {
        }
    }

    public static abstract class Acceptor<S extends Serializable & Comparable<S>, R>
            extends Selector<S> {
        private R[] criterias;
        private Set<Integer> sets = new TreeSet<Integer>();
        private LinkedList<Integer> acceptor = new LinkedList<Integer>();

        public Acceptor(R[] criterias) {
            this.criterias = criterias;

            for (int i = 0; i < criterias.length; ++i) {
                sets.add(i);
            }
        }

        public abstract boolean accept(S value, R criteria);

        @Override
        public final boolean select(CheckTreeNode<S> node) {
            acceptor.set(0, -1);
            for (int i = 0; i < criterias.length; ++i) {
                if (accept(node.getValue(), criterias[i])) {
                    acceptor.set(0, i);
                    break;
                }
            }

            if (acceptor.containsAll(sets)) {
                return true;
            }

            return false;
        }

        @Override
        public final void down() {
            acceptor.push(-1);
        }

        @Override
        public final void up() {
            acceptor.pop();
        }
    }
}
