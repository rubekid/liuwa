import auth from './auth'
import cache from './cache'
import modal from './modal'
import downloader from './downloader'

export default {
  install(Vue) {
    // 认证对象
    Vue.prototype.$auth = auth
    // 缓存对象
    Vue.prototype.$cache = cache
    // 模态框对象
    Vue.prototype.$modal = modal
    // 下载器
    Vue.prototype.$downloader = downloader
  }
}
