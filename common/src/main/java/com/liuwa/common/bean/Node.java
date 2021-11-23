package com.liuwa.common.bean;

/**
 * 节点
 * @author Rubekid
 *
 * 2017年5月23日 下午4:25:34
 */
public class Node<T> {

	/**
	 * 节点ID
	 */
	private T id;
	
	/**
	 * 父节点ID
	 */
	private T parentId;
	
	/**
	 * 节点名称
	 */
	private String label;

	public Node(){}

	public Node(T id, T parentId, String label){
		this.id = id;
		this.parentId = parentId;
		this.label = label;
	}

	public T getId() {
		return id;
	}

	public void setId(T id) {
		this.id = id;
	}

	public T getParentId() {
		return parentId;
	}

	public void setParentId(T parentId) {
		this.parentId = parentId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}