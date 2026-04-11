import { getErrorMessage, login, saveSession } from '../../utils/api'

Page({
  data: {
    mobile: '13800138000',
    password: '123456',
    loading: false,
  },

  handleMobileInput(event: WechatMiniprogram.Input) {
    this.setData({
      mobile: event.detail.value.trim(),
    })
  },

  handlePasswordInput(event: WechatMiniprogram.Input) {
    this.setData({
      password: event.detail.value.trim(),
    })
  },

  async submitLogin() {
    const { mobile, password, loading } = this.data
    if (loading) {
      return
    }

    if (!mobile || !password) {
      wx.showToast({
        title: '请输入手机号和密码',
        icon: 'none',
      })
      return
    }

    this.setData({ loading: true })

    try {
      const session = await login({ mobile, password })
      // 确保userId是string类型
      const sessionWithStringId = {
        ...session,
        userId: String(session.userId)
      }
      console.log('登录成功，用户ID:', sessionWithStringId.userId, typeof sessionWithStringId.userId)
      saveSession(sessionWithStringId)
      wx.showToast({
        title: '登录成功',
        icon: 'success',
      })
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/profile/index',
        })
      }, 300)
    } catch (error) {
      wx.showToast({
        title: getErrorMessage(error),
        icon: 'none',
      })
    } finally {
      this.setData({ loading: false })
    }
  },
})
