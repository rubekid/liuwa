import { Message, MessageBox, Notification, Loading } from 'element-ui'

let loadingInstance;

export default {
  // 消息提示
  info(content) {
    Message.info(content)
  },
  // 错误消息
  error(content) {
    Message.error(content)
  },
  // 成功消息
  success(content) {
    Message.success(content)
  },
  // 警告消息
  warning(content) {
    Message.warning(content)
  },
  // 弹出提示
  alert(content) {
    MessageBox.alert(content, "系统提示")
  },
  // 错误提示
  alertError(content) {
    MessageBox.alert(content, "系统提示", { type: 'error' })
  },
  // 成功提示
  alertSuccess(content) {
    MessageBox.alert(content, "系统提示", { type: 'success' })
  },
  // 警告提示
  alertWarning(content) {
    MessageBox.alert(content, "系统提示", { type: 'warning' })
  },
  // 通知提示
  notify(content) {
    Notification.info(content)
  },
  // 错误通知
  notifyError(content) {
    Notification.error(content);
  },
  // 成功通知
  notifySuccess(content) {
    Notification.success(content)
  },
  // 警告通知
  notifyWarning(content) {
    Notification.warning(content)
  },
  // 确认窗体
  confirm(content, title, options) {
				title = title || '系统提示';
    return MessageBox.confirm(content, title, Object.assign({
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: "warning",
    }, options || {}))
  },
  // 打开遮罩层
  loading(content) {
    loadingInstance = Loading.service({
      lock: true,
      text: content,
      spinner: "el-icon-loading",
      background: "rgba(0, 0, 0, 0.7)",
    })
  },
  // 关闭遮罩层
  closeLoading() {
    loadingInstance.close();
  }
}
