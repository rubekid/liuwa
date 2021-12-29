<template>
	<div>

		<template v-for="(item, index) in options">
			<template v-if="values.includes(item[valueKey] + '')">
				<span
					v-if="item.listClass == 'default' || !item.listClass"
					:key="item[valueKey] + ''"
					:index="index"
					:type="item.listClass == 'primary' ? '' : item.listClass"
					:class="item.cssClass"
					:title="item[labelKey]"
				>{{ item[labelKey] }}</span
				>
				<el-tag
					v-else
					:title="item[labelKey]"
					:disable-transitions="true"
					:key="item[valueKey] + ''"
					:index="index"
					:type="item.listClass == 'primary' ? '' : item.listClass"
					:class="item.cssClass"
				>
					{{ item[labelKey] }}
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
		value: [Number, String, Array, Boolean],
		// label 字段key
		labelKey: {
			type: String,
			default: 'dictLabel'
		},
		// value 字段key
		valueKey: {
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
