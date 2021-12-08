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
				// 字典数据
				dictData(){
					return (dictType, dataType, ... ignore) =>{
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
						// 忽略某些值
						if(ignore && ignore.length > 0){
							data = data.filter( item => {
									return !ignore.some(i=> item.dictValue == i);
							})

						}
						return data;
					}
				},
				/**
					* 字典标签
					* @returns {any}
					*/
				dictLabel(){
					return (dictType, value, dataType, ... ignore) =>{
							let data = this.dictData(dictType, dataType, ignore);
							let ret = data.find(item => {
								return item.dictValue == value;
							});
							return ret ? ret.dictLabel : '';
					}
				}
			}
		});
	}
}
