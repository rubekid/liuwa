<template>

    <el-image
      class="image-view"
      :style="style"
      :src="fullSrc"
      :fit="fit"
      :lazy="lazy"
      :preview-src-list="srcList"
    ></el-image>


</template>

<script>
export default {
  props: {
    src: {
      type:String
    },
    // 是否显示提示
    preview: {
      type: Boolean,
      default: true
    },
    fit:{
      type: String,
      default: 'scale-down'
    },
    width:{
      type: Number,
      default: 100
    },
    height:{
      type: Number,
      default: 100
    },
    lazy: {
      type: Boolean,
      default: true
    }

  },
  data() {
    return {
      baseUrl: process.env.VUE_APP_BASE_API,
      dialogImageUrl: "",
      dialogVisible: false
    };
  },
  computed: {
    // 是否显示提示
    style() {
      return {
        width: this.width + 'px',
        height: this.height + 'px'
      }
    },
    fullSrc(){
      return this.baseUrl + this.src
    },
    srcList(){
      if(this.preview){
        return [this.fullSrc];
      }
      return [];
    }
  },
  methods: {

  }
};
</script>


