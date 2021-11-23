package com.liuwa.common.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * 树节点
 * @author Rubekid
 *
 * 2017年5月23日 下午4:26:42
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TreeNode<T> {
	
	/**
	 * 节点ID
	 */
	private T id;
	
	/**
	 * 节点名称
	 */
	private String label;
	
	/**
	 * 子节点列表
	 */
	private List<TreeNode> children;

	public TreeNode(){}

	public TreeNode(T id, String label, List<TreeNode> children){
		this.id = id;
		this.label = label;
		this.children = children;
	}

	public TreeNode(T id, String label){
		this.id = id;
		this.label = label;
	}

	public T getId() {
		return id;
	}

	public void setId(T id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

}
