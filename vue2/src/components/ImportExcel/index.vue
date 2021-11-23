<template>
	<div class="import-excel">
		<el-button
			type="info"
			plain
			icon="el-icon-upload2"
			size="mini"
			@click="open = true"

		>导入</el-button>
		<el-dialog :title="dialogTitle" :visible.sync="open" width="400px" append-to-body>
			<el-upload
				ref="upload"
				:limit="1"
				accept=".xlsx, .xls"
				:headers="headers"
				:action="importUrl + '?overwrite=' + overwrite"
				:disabled="disabled"
				:on-progress="handleFileUploadProgress"
				:on-success="handleFileSuccess"
				:auto-upload="false"
				drag
			>
				<i class="el-icon-upload"></i>
				<div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
				<div class="el-upload__tip text-center" slot="tip">
					<div class="el-upload__tip" slot="tip">
						<el-checkbox v-model="overwrite" /> 是否更新已经存在的{{title}}}数据
					</div>
					<span>仅允许导入xls、xlsx格式文件。</span>
					<el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
				</div>
			</el-upload>
			<div slot="footer" class="dialog-footer">
				<el-button type="primary" @click="submitFileForm">确 定</el-button>
				<el-button @click="open = false">取 消</el-button>
			</div>
		</el-dialog>
	</div>



</template>

<script>
// 通过excel 导入数据
import {getToken} from "@/utils/auth";
import {importTemplate} from "@/api/system/user";
import request from "@/utils/request";

export default {
	name: "ImportExcel",
	props: {
		title: {
			type:String
		},
		url:{
			type: String
		},
		tplUrl: {
			type: String
		},
		// 显示
		visible: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
			// 是否显示弹出层
			open: false,
			// 是否覆盖已存在的数据
			overwrite: false,
			// 是否禁用上传
			disabled: false,
			// 设置上传的请求头部
			headers: { Authorization: "Bearer " + getToken() },
			baseUrl: process.env.VUE_APP_BASE_API
		};
	},
	created() {
		this.open = this.visible;
	},
	computed: {
		// 弹出层标题
		dialogTitle(){
			return this.title || '数据'  + '导入';
		},
		// 上传的地址
		importUrl(){
			return this.baseUrl + '/' + this.url.replace(/^\//, '');
		},
		templateUrl(){
			return '/' + (this.tplUrl ? this.tplUrl : (this.url.replace(/^\//, '') + '/template'))
		}
	},
	watch:{
		visible(val){
			this.open = val;
		},
		open(val){
			this.$emit("update:visible", val);
		}
	},
	methods: {
		// 文件上传中处理
		handleFileUploadProgress(event, file, fileList) {
			this.disabled = true;
		},
		// 文件上传成功处理
		handleFileSuccess(response, file, fileList) {
			this.open = false;
			this.disabled = false;
			this.$refs.upload.clearFiles();
			this.$alert(response.msg, "导入结果", { dangerouslyUseHTMLString: true });
			this.$emit("complete")
		},
		// 提交上传文件
		submitFileForm() {
			this.$refs.upload.submit();
		},
		// 下载模板
		importTemplate(){
			console.log(this.templateUrl);
			request({
				url: this.templateUrl,
				method: 'get'
			}).then(response => {
				this.$downloader.download(response.data);
			});
		}
	}
};
</script>


