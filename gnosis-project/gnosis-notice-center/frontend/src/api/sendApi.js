import request from '../utils/request'

export const sendApi = {
  send: (data) => request.post('/send/send', data),
  batchSend: (data) => request.post('/send/batch', data),
  sendSms: (data) => request.post('/send/sms', data),
  sendEmail: (data) => request.post('/send/email', data),
  sendInbox: (data) => request.post('/send/inbox', data),
  sendWechat: (data) => request.post('/send/wechat', data),
}
