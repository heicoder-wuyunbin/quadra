import { defaultAvatar, getSession } from '../../utils/api'

Page({
  data: {
    sessionReady: false,
    nickname: '今晚想遇见懂你的人吗？',
    subtitle: '先登录，再完善资料、管理黑名单和破冰问题，把用户域能力跑起来。',
    userId: 0,
    avatar: defaultAvatar,
    quickActions: [
      {
        title: '登录账号',
        desc: '用手机号密码接入网关登录接口',
        url: '/pages/login/index',
        type: 'navigate',
      },
      {
        title: '完善资料',
        desc: '同步用户资料与通知偏好',
        url: '/pages/profile/index',
        type: 'tab',
      },
      {
        title: '查看黑名单',
        desc: '分页查看我拉黑了谁',
        url: '/pages/blacklist/index',
        type: 'tab',
      },
      {
        title: '管理提问',
        desc: '维护陌生人破冰问题列表',
        url: '/pages/questions/index',
        type: 'tab',
      },
    ],
    planHighlights: [
      '温馨柔和的暖粉+杏橙色调，适合相亲交友场景',
      '优先接入用户强相关接口：登录、资料、黑名单、破冰问题',
      '围绕 openapi/api.json 映射真实后端能力，方便联调',
    ],
  },

  onShow() {
    this.syncSession()
  },

  syncSession() {
    const session = getSession()
    const app = getApp<IAppOption>()
    const profileDraft = app.globalData.profileDraft

    this.setData({
      sessionReady: !!session,
      userId: session?.userId || 0,
      nickname: profileDraft?.nickname || (session ? `ID ${session.userId} 的心动档案` : '今晚想遇见懂你的人吗？'),
      avatar: profileDraft?.avatar || defaultAvatar,
      subtitle: session
        ? '已经接入认证态，可以继续完善资料、设置通知和管理防打扰能力。'
        : '先完成登录，再把用户资料、黑名单和破冰问题联调起来。',
    })
  },

  handleActionTap(event: WechatMiniprogram.BaseEvent) {
    const { url, type } = event.currentTarget.dataset as { url: string; type: 'navigate' | 'tab' }
    if (type === 'tab') {
      wx.switchTab({ url })
      return
    }

    wx.navigateTo({ url })
  },
})
