package com.liuwa.common.utils.bean;

import com.liuwa.common.bean.Node;
import com.liuwa.common.bean.TreeNode;
import com.liuwa.common.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树工具类
 * @author Rubekid
 *
 * 2017年5月23日 下午4:28:14
 */
public class TreeNodeUtils {

	private static Logger logger = LoggerFactory.getLogger(TreeNodeUtils.class);

	/**
	 * 组装树形数据
	 * @param list
	 * @return
	 */
	public static List<TreeNode> tree(List<?> list) {
		return tree(list, 0);
	}

	/**
	 * 组装树形数据
	 * @param list
	 * @param rootId
	 * @return
	 */
	public static List<TreeNode> tree(List<?> list, Object rootId) {
		return tree(list, rootId, "id", "name", "parentId");
	}

	/**
	 * 组装树形数据
	 * @param list
	 * @param rootId
	 * @param idFieldName
	 * @param labelFieldName
	 * @param parentIdFieldName
	 * @return
	 */
	public static List<TreeNode> tree(List<?> list, Object rootId, String idFieldName, String labelFieldName, String parentIdFieldName) {
		List<Node> nodes = new ArrayList<Node>();


		for (int i=0; i<list.size(); i++){
			Object obj = list.get(i);
			try{

				Field idField = ClassUtils.getField(obj, idFieldName);
				Field labelField = ClassUtils.getField(obj, labelFieldName);
				Field parentIdField = ClassUtils.getField(obj, parentIdFieldName);
				idField.setAccessible(true);
				labelField.setAccessible(true);
				parentIdField.setAccessible(true);


				Node<Object> node = new Node<Object>();
				node.setId(idField.get(obj));
				node.setParentId(parentIdField.get(obj));
				node.setLabel(String.valueOf(labelField.get(obj)));
				nodes.add(node);


			}
			catch (SecurityException | IllegalArgumentException | IllegalAccessException ex){
				logger.error(ex.getMessage(), ex);
			}



		}
		return TreeNodeUtils.generateTree(nodes, rootId);
	}

	/**
	 * 生成树
	 * @param nodes
	 * @return
	 */
	public static List<TreeNode> generateTree(List<Node> nodes, Object rootId){
		Map<Object, List<TreeNode>> map = new HashMap<Object, List<TreeNode>>();
		for(Node node : nodes){
			Object parentId = node.getParentId();
			List<TreeNode> treeNodes = new ArrayList<TreeNode>();
			if(map.containsKey(parentId)){
				treeNodes = map.get(parentId);
			}
			TreeNode treeNode = new TreeNode();
			treeNode.setId(node.getId());
			treeNode.setLabel(node.getLabel());
			treeNodes.add(treeNode);
			map.put(parentId, treeNodes);
		}
		
		return buildNode(map, rootId);
		
	}
	
	/**
	 * 组装节点
	 * @param map
	 * @param parentId
	 * @return
	 */
	public static List<TreeNode> buildNode(Map<Object, List<TreeNode>> map, Object parentId){
		List<TreeNode> nodes = map.get(parentId);
		if(nodes!=null){
			for(TreeNode treeNode : nodes){
				treeNode.setChildren(buildNode(map, treeNode.getId()));
			}
		}
		return nodes;
		
	}
	
	
}