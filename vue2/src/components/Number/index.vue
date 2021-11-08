<template>
  <el-input type="text" v-model="text" class="number" :placeholder="placeholder" />
</template>

<script>

export default {
  name: "Number",
  props: {
    value: {
      type: [String,Number]
    },
    /* 高度 */
    height: {
      type: Number,
      default: null,
    },
    /* 最小高度 */
    minHeight: {
      type: Number,
      default: null,
    },
    /* 只读 */
    readOnly: {
      type: Boolean,
      default: false,
    },
    /* 类型（base64格式、url格式） */
    type: {
      type: String,
      default: "text",
    },
    placeholder:{
        type: String,
        default: ''
    },
    max:{ // 最大值
      type: Number
    },
    fixed:{ // 小数位数
      type: Number
    }
  },
  data() {
    return {
      text: ''
    };
  },
  created(){

  },
  computed: {

  },
  watch: {
    value:{
      handler(val){
							 this.text = (val || '') + '';
      },
					 immediate: true
    },
    text: {
      handler(val) {
        if(!val){
          val = '';
        }
        val = val.trim();
        if(this[this.type + 'Format']){
          val = this[this.type + 'Format'](val);
        }
        this.text = val;

        this.$emit('input', val);
      },
      immediate: true,
    },
  },
  mounted() {

  },
  methods:{
    /**
     * 整数
     */
    integerFormat(text){

      let number = parseInt(text);
      if(isNaN(number)){
        return '';
      }

      if(this.max && number > this.max){
        number = this.max;
      }

      return number + '';
    },
    /**
     * 小数
     */
    decimalFormat(text, max, fixed){
      max = max || this.max;
      fixed = fixed || this.fixed;
      let number = parseFloat(text);
      if(isNaN(number)){
        return '';
      }

      if(max && number > max){
        number = max;
      }

      // 最后一位是.暂时保留
      let last = text.substring(text.length - 1);
      text = number + '';

      if(last == '.'){
        text += '.';
      }

      let arr = text.split('.');
      if(fixed && arr.length == 2){
        arr[1] = arr[1].substr(0,  fixed);
        text = arr.join('.');
      }
      return text;
    },
    /**
     * 金额
     */
    moneyFormat(text){

      let fixed = this.fixed;
      if(!fixed){
        fixed = 2;
      }
      return this.decimalFormat(text, this.max, fixed);
    },
      /**
       * 百分率
       */
    percentFormat(text){
      return this.decimalFormat(text, 100, 2);

    },
    /**
       * 比率系数
       */
    ratioFormat(text){
      return this.decimalFormat(text);
    }
  }


};
</script>

<style>

</style>
