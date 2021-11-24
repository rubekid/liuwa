/**
	* 权限 mixin
	*/
export default {

		install(Vue){
				Vue.mixin({

					computed:{
						/** 是否有权限 */
						hasRole(){
							return (... roleFlag) => {
								let roles = this.$store.state.user.roles;
								const hasRole = roles.some(role => {
									return roleFlag.includes(role)
								})
								return hasRole;
							}
						}
					}
				});
		}
}
