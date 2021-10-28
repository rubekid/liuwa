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
   value: {
     type: Array,
     default: []
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


    this.options = options;
    this.regionValue = this.value;
  },
  computed: {

  },
  watch: {

  },
  mounted() {

  },
  methods:{
   handleChange(e){
    console.log(e);
    this.$emit("input", e);
    this.$emit("change", e);
   }
  }

};
</script>
