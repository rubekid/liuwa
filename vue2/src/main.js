import Vue from 'vue'

import Cookies from 'js-cookie'

import Element from 'element-ui'
import './assets/styles/element-variables.scss'

import '@/assets/styles/index.scss' // global css
import '@/assets/styles/common.scss' // common css
import App from './App'
import store from './store'
import router from './router'
import directive from './directive' //directive
import plugins from './plugins' // plugins
import DictMixin from "@/mixins/DictMixin";
import PermissionMixin from "@/mixins/PermissionMixin";


import './assets/icons' // icon
import './permission' // permission control
import { getDicts } from "@/api/system/dict/data";
import { getConfigKey } from "@/api/system/config";
import { parseTime, resetForm, addDateRange, selectDictLabel, selectDictLabels, handleTree } from "@/utils/common";
import Pagination from "@/components/Pagination";
// 自定义表格工具组件
import RightToolbar from "@/components/RightToolbar"
// 富文本组件
import Editor from "@/components/Editor"
// 区域组件
import Region from "@/components/Region"
// 数值组件
import Number from "@/components/Number"
// 文件上传组件
import FileUpload from "@/components/FileUpload"
// 单图上传组件
import SingleImageUpload from "@/components/SingleImageUpload"
// 多图上传组件
import MultiImageUpload from "@/components/MultiImageUpload"
// 图片展示
import ImageView from "@/components/ImageView"
// 多图片展示
import ImagesView from "@/components/ImagesView"
// 字典标签组件
import DictTag from '@/components/DictTag'
// 头部标签组件
import VueMeta from 'vue-meta'

// 全局方法挂载
Vue.prototype.getDicts = getDicts
Vue.prototype.getConfigKey = getConfigKey
Vue.prototype.parseTime = parseTime
Vue.prototype.resetForm = resetForm
Vue.prototype.addDateRange = addDateRange
Vue.prototype.selectDictLabel = selectDictLabel
Vue.prototype.selectDictLabels = selectDictLabels
Vue.prototype.handleTree = handleTree




Vue.prototype.msgError = function(msg) {
    this.$message({ showClose: true, message: msg, type: "error" });
}


// 全局组件挂载
Vue.component('DictTag', DictTag)
Vue.component('Pagination', Pagination)
Vue.component('RightToolbar', RightToolbar)
Vue.component('Editor', Editor)
Vue.component('Number', Number)
Vue.component("Region", Region);
Vue.component('FileUpload', FileUpload)
Vue.component('SingleImageUpload', SingleImageUpload)
Vue.component('MultiImageUpload', MultiImageUpload)
Vue.component('ImageView', ImageView)
Vue.component('ImagesView', ImagesView)

Vue.use(directive)
Vue.use(plugins)
Vue.use(VueMeta)
Vue.use(DictMixin)
Vue.use(PermissionMixin)

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */

Vue.use(Element, {
    size: Cookies.get('size') || 'medium' // set element-ui default size
})

Vue.config.productionTip = false

let app = null;
let resizeTimeout = null;
window && window.addEventListener("resize", () => {
    if (resizeTimeout) {
        clearTimeout(resizeTimeout);
    }
    resizeTimeout = setTimeout(() => {
        let width = document.documentElement.clientWidth;
        let height = document.documentElement.clientHeight;
        let appSize = {
                width: width,
                height: height
            }
            //console.log(appSize, new Date().getTime())
        app && app.$store.dispatch("app/setAppSize", appSize)
    }, 200);

});

app = new Vue({
    el: '#app',
    router,
    store,
    render: h => h(App)
})
