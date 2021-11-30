<template>
	<div>

		<template v-for="(item, index) in options">
			<template v-if="values.includes(item[dictValue] + '')">
				<span
					v-if="item.listClass == 'default' || !item.listClass"
					:key="item[dictValue] + ''"
					:index="index"
					:class="item.cssClass"
					:title="item[dictLabel]"
				>{{ item[dictLabel] }}</span
				>
				<el-tag
					v-else
					:title="item[dictLabel]"
					:disable-transitions="true"
					:key="item[dictValue] + ''"
					:index="index"
					:type="item.listClass == 'primary' ? '' : item.listClass"
					:class="item.cssClass"
				>
					{{ item[dictLabel] }}
				</el-tag>
			</template>
		</template>
	</div>
</template>

<script>
export default {
	name: "DictTag",
	props: {
		options: {
			type: Array,
			default: null,
		},
		value: [Number, String, Boolean, Array],
		dictLabel: {
			type: String,
			default: 'dictLabel'
		},
		dictValue: {
			type: String,
			default: 'dictValue'
		}
	},
	computed: {
		values() {

			if (this.value !== null && typeof this.value !== 'undefined') {
				let value = this.value;
				if(typeof value == 'string'){
					value = value.replace(/^,|,$/, '').split(',');
				}
				return Array.isArray(value) ? value : [String(value)];
			} else {
				return [];
			}
		}
	}
};
</script>
<style scoped>
.el-tag{
	margin-bottom:10px;
}
.el-tag + .el-tag {
	margin-left: 10px;
}
</style>
