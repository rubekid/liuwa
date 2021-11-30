package com.liuwa.common.core.domain;

import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * 响应数据
 */
public class Result {

    public static ItemsVo items(List<?> items){
        return new ItemsVo(items);
    }

    public static class ItemsVo {

        private List<?> items;

        public ItemsVo(){}

        public ItemsVo(List<?> items){
            this.items = items;
        }

        public List<?> getItems() {
            return items;
        }

        public void setItems(List<?> items) {
            this.items = items;
        }
    }
}
