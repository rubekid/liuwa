/**
	* 数据字典 mixin
	*/
export default {

		install(Vue){
				Vue.mixin({
						data(){
							if (!this.$options.dicts
								|| !(this.$options.dicts instanceof Array)
								|| this.$options.dicts.length  == 0) {
								return {}
							}

							return {
									sysDictMap: {}
							}
						},
					 created() {
								if(!this.sysDictMap){
									return ;
								}
								this.$options.dicts.forEach(item => {
									this.getDicts(item).then(res => {
											this.$set(this.sysDictMap, item, res.data);
									});
								})
						}
				});
		}
}
