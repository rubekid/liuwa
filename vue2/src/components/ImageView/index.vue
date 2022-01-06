<template>

    <el-image
      class="image-view"
      :style="style"
      :src="fullSrc"
      :fit="fit"
      :lazy="lazy"
      :preview-src-list="previewSrcList"
    ></el-image>


</template>

<script>
export default {
  props: {
    src: {
      type:String
    },
			 srcList:{
						type: Array,
						default: ()=>{
							return []
						}
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
						let width = this.width > 0 ? (this.width + 'px') : 'auto';
      let height = this.height > 0 ? (this.height + 'px') : 'auto';
						return {
        width: width,
        height: height
      }
    },
    fullSrc(){
      return this.buildFullSrc(this.src)
    },
				previewSrcList(){
      if(this.preview){
							 if(this.srcList && this.srcList.length > 0){
										return this.srcList.map(item=>{
												return this.buildFullSrc(item);
										})
								}
        return [this.fullSrc];
      }
      return [];
    }
  },
  methods: {
			/**
				* 组建完整路径
				* @param src
				* @returns {string|*}
				*/
				buildFullSrc(src){
					console.log(src);
					if(src.indexOf("http://") == 0 ||src.indexOf("https://") == 0){
						return src;
					}
					return this.baseUrl + src
				}
  }
};
</script>


