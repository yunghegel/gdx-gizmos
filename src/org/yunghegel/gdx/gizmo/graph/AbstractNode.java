package org.yunghegel.gdx.gizmo.graph;

import com.badlogic.gdx.utils.Array;

public abstract class AbstractNode<T extends AbstractNode> implements Node<T>, Iterable<AbstractNode> {

    public final int id;

    protected Array<T> children;
    protected T parent;

    public AbstractNode(int id) {
        this.id = id;
    }

    @Override
    public void initChildrenArray() {
        this.children = new Array<T>();
    }

    @Override
    public void addChild(T child) {
        if (children == null) children = new Array<T>();
        children.add(child);
        child.setParent(this);
    }

    @Override
    public boolean isChildOf(Spatial other) {
        for (Spatial go : other) {
            if (go.id == this.id) return true;
        }

        return false;
    }

    @Override
    public Array<T> getChildren() {
        return this.children;
    }

    @Override
    public T getParent() {
        return this.parent;
    }

    @Override
    public void setParent(T parent) {
        this.parent = parent;
    }

    @Override
    public void remove() {
        if (parent != null) {
            parent.getChildren().removeValue(this, true);
            this.parent = null;
        }
    }


}


