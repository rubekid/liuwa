import { saveAs } from 'file-saver'
import axios from 'axios'
import { getToken } from '@/utils/auth'
import { Message } from 'element-ui'

const baseURL = process.env.VUE_APP_BASE_API

export default {
	/**
		* 通过服务的生成的文件名下载
		* @param fileData
		* @param isDelete
		*/
  download(fileData, isDelete = true) {
    let name = fileData.fileName;
				let url = baseURL + "/common/download?fileName=" + encodeURIComponent(name) + "&delete=" + isDelete
    axios({
      method: 'get',
      url: url,
      responseType: 'blob',
      headers: { 'Authorization': 'Bearer ' + getToken() }
    }).then(async (res) => {
      const isLogin = await this.blobValidate(res.data);
      if (isLogin) {
        const blob = new Blob([res.data])
        this.saveAs(blob, decodeURI(res.headers['download-filename']))
      } else {
        Message.error('无效的会话，或者会话已过期，请重新登录。');
      }
    })
  },
	/**
		* 本地资源下载
		* @param resource
		*/
  resource(resource) {
    let url = baseURL + "/common/download/resource?resource=" + encodeURIComponent(resource);
    axios({
      method: 'get',
      url: url,
      responseType: 'blob',
      headers: { 'Authorization': 'Bearer ' + getToken() }
    }).then(async (res) => {
      const isLogin = await this.blobValidate(res.data);
      if (isLogin) {
        const blob = new Blob([res.data])
        this.saveAs(blob, decodeURI(res.headers['download-filename']))
      } else {
        Message.error('无效的会话，或者会话已过期，请重新登录。');
      }
    })
  },
	/**
		* 打包zip包下载
		* @param url
		* @param name
		*/
  zip(url, name) {
    url = baseURL + url
    axios({
      method: 'get',
      url: url,
      responseType: 'blob',
      headers: { 'Authorization': 'Bearer ' + getToken() }
    }).then(async (res) => {
      const isLogin = await this.blobValidate(res.data);
      if (isLogin) {
        const blob = new Blob([res.data], { type: 'application/zip' })
        this.saveAs(blob, name)
      } else {
        Message.error('无效的会话，或者会话已过期，请重新登录。');
      }
    })
  },
	/**
		* 保存
		* @param text
		* @param name
		* @param opts
		*/
  saveAs(text, name, opts) {
    saveAs(text, name, opts);
  },
  async blobValidate(data) {
    try {
      const text = await data.text();
      JSON.parse(text);
      return false;
    } catch (error) {
      return true;
    }
  },
}

