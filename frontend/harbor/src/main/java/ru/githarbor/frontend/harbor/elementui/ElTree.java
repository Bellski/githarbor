package ru.githarbor.frontend.harbor.elementui;

import com.axellience.vuegwt.core.client.component.IsVueComponent;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import ru.githarbor.frontend.harbor.vue.harbor.repository.data.RepositoryTreeNode;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ElTree implements IsVueComponent {

    @JsProperty
    public IsVueComponent currentNode;

    public TreeNode<RepositoryTreeNode> root;

    public native TreeNode<RepositoryTreeNode> getNode(String key);
    public native RepositoryTreeNode getCurrentNode();
    public native void setCurrentKey(String key);
    public native String  getCurrentKey();
    public native void setCurrentNode(TreeNode<RepositoryTreeNode> node);
}
