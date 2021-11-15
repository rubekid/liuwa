<template>
	<cascader v-if="mode == 'multiple'"
		v-model="regionValue"
		:placeholder="placeholder"
		:options="options" value-key="name" label-key="name" @change="handleChange"></cascader>
	<el-cascader v-else
		v-model="regionValue"
		:options="options"
		clearable
		:props="{value: 'name', label: 'name', expandTrigger: 'hover'}"
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
		province: { // 省份
			type: String,
			default: ''
		},
		city: {   // 城市
			type: String,
			default: ''
		},
		district: { // 区县
			type: String,
			default: ''
		},
		placeholder: {
			type: Array,
			default: () => ['请选择省份', '请选择城市', '请选择区县']
		},
		mode: { // 模式  single 单个 multiple 多个下拉框
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

	},
	watch: {

	},
	mounted() {

	},
	methods:{
		handleChange(e){
			this.$emit("update:province", e[0]);
			this.$emit("update:city", e[1]);
			this.$emit("update:district", e[2]);
			this.$emit("change", e);
		}
	}

};
</script>
