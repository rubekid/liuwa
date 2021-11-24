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
			},
			computed:{
				// 字段数据
				dictData(){
					return (dictType, dataType) =>{
						let data = this.sysDictMap[dictType];
						if(!data){
							return [];
						}

						if(dataType){
							data = data.map( item => {
								let dictValue = item.dictValue;
								if(dataType == 'Number'){
									dictValue = + dictValue;
								}
								else if(dictValue == 'Boolean'){
									dictValue = dictValue ? true : false;
								}
								else{
									dictValue += '';
								}

								return {
									dictLabel: item.dictLabel,
									dictValue: dictValue
								}
							})
						}
						return data;
					}
				}
			}
		});
	}
}
