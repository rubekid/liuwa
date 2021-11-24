<template>
	<cascader v-if="mode == 'select'"
											v-model="regionValue"
											:placeholder="placeholder"
											:options="options" value-key="name" label-key="name" @change="handleChange"></cascader>
	<el-cascader v-else
														v-model="regionValue"
														:options="options"
														clearable
														:props="{value: 'name', label: 'name', emitPath: emitPath,  expandTrigger: expandTrigger, checkStrictly:checkStrictly, multiple: multiple}"
														@change="handleChange"></el-cascader>
</template>

<script>

import regionData from "./region"
import Cascader from "@/components/Cascader"

export default {
	name: "Region",
	components:{
		Cascader
	},
	props: {
		value:{
			type: String| Array
		},
		province: { // 省份
			type: String|Array,
			default: ''
		},
		city: {   // 城市
			type: String|Array,
			default: ''
		},
		district: { // 区县
			type: String|Array,
			default: ''
		},
		placeholder: {
			type: Array,
			default: () => ['请选择省份', '请选择城市', '请选择区县']
		},
		expandTrigger:{ // 次级菜单的展开方式	 click / hover
			type: String,
			default: 'click'
		},
		checkStrictly:{ // 是否严格的遵守父子节点不互相关联
			type: Boolean,
			default: false
		},
		emitPath:{ // 在选中节点改变时，是否返回由该节点所在的各级菜单的值所组成的数组，若设置 false，则只返回该节点的值
			type: Boolean,
			default: true
		},
		mode: { // 模式  single 单个 multiple 多选  select下拉
			type: String,
			default: 'single'
		},
		depth: { // 级数  省市区 3级 省市 2级
			type: Number,
			default: 3
		}
	},
	data() {
		return {
			regionValue:[],
			options: []
		};
	},
	created(){
		// 拷贝副本，避免数据污染
		let options = JSON.parse(JSON.stringify(regionData));

		if(this.depth == 1){
			options.forEach(item => {
				delete item.children;
			});
		}
		else if(this.depth == 2){
			options.forEach(level1 => {
				level1.children && level1.children.forEach(item => {
					delete item.children;

				})
			});
		}

		let value = [this.province, this.city, this.district];
		if(this.depth < 3){
			value = value.slice(0, this.depth);
		}

		this.options = options;
		this.regionValue = value;
	},
	computed: {
		multiple(){
			return this.mode == 'multiple';
		}
	},
	watch: {
		province(val){
			if(!val){
				this.regionValue = [];
				return;
			}
			let value = [this.province, this.city, this.district];
			if(this.depth < 3){
				value = value.slice(0, this.depth);
			}
			this.regionValue = value;
		},
		value(val){
			if(typeof val == 'string'){
				this.regionValue = val.split(',');
			}
			else{
				this.regionValue = val;
			}

		}
	},
	mounted() {

	},
	methods:{
		handleChange(e){
			this.$emit("update:province", e[0]);
			this.$emit("update:city", e[1]);
			this.$emit("update:district", e[2]);
			this.$emit("input", e);
			this.$emit("change", e);
		}
	}

};
</script>
