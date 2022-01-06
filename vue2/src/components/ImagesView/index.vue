<template>

				<div class="images-view">
						<image-view v-for="(image,idx) in images" :key="idx" :src="image" :preview="preview" :fit="fit" :width="width" :height="height" :lazy="lazy" :src-list="images" />
				</div>

</template>

<script>
export default {
  props: {
    items: {
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

    };
  },
  computed: {
				images(){

					 if(this.items){
							 return this.items.split(',');
						}
						return [];
				},

  },
  methods: {

  }
};
</script>


