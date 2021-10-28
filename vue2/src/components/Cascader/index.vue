<template>
  <div class="lw-cascader">
    <el-select class="lw-cascader-item" :placeholder="item.placeholder" v-model="item.value" v-for="(item, idx) in selects"
               clearable
               @change="changeOption($event, idx, item)">
      <el-option
       v-for="option in item.options"
       :key="option[valueKey]"
       :label="option[labelKey]"
       :value="option[valueKey]">
      </el-option>


    </el-select>

  </div>
</template>

<script>



export default {
  name: "Cascader",
  props: {
   value: {
     type: Array,
     default: []
   },
   placeholder: {
    type: Array,
    default: []
   },
   valueKey: {
    type: String,
    default: 'value'
   },
   labelKey: {
    type: String,
    default: 'label'
   },
   options:{
    type: Array,
    default: []
   }
  },
  data() {
    return {
      province: '',
      modelValue:[],
     selects:[]
    };
  },
  created(){

   this.modelValue = this.value;
   this.refreshSelects();
  },
  methods:{
   changeOption(e, index, item){

     let values = this.modelValue;
     values[index] = item.value;
     this.modelValue = values.slice(0, index +1);
     this.refreshSelects();
     this.$emit('input', this.modelValue);
     this.$emit('change', this.modelValue);
   },
   /**
    * 刷新选择器
    */
   refreshSelects(){
     let selects = [];
     let depth = 0;
     let select = {
      value: this.modelValue[depth],
      placeholder: this.placeholder[depth],
      options: this.options
     };
     selects.push(select);
     while(select){
      depth ++;
      select = this.setupChildren(depth, select);
      if(select){

       selects.push(select);

      }
     }

     this.selects = selects;

   },
   /**
    * 装配子级联
    * @param depth
    * @param options
    */
   setupChildren(depth, select){

     if(select.value){
      for(let i=0; i< select.options.length; i++){
       let option = select.options[i];
       if(option[this.valueKey] == select.value && option.children){

         return {
           value: this.modelValue[depth],
           placeholder: this.placeholder[depth],
           options: option.children
         };
       }
      }
     }
     return null;
   }

  }
};
</script>
<style>

 .lw-cascader-item:not(:first-child) {
   margin-left: 6px;
 }
</style>
