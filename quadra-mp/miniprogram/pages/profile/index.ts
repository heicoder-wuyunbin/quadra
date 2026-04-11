import {
  defaultAvatar,
  defaultNotificationSetting,
  getErrorMessage,
  getNotificationSettingDraft,
  getSession,
  getUserProfile,
  saveNotificationSettingDraft,
  updateUserProfile,
  updateUserSetting,
} from '../../utils/api'

const genderOptions = [
  { label: '未知', value: 0 },
  { label: '男', value: 1 },
  { label: '女', value: 2 },
] as const

const marriageOptions = [
  { label: '单身', value: 0 },
  { label: '已婚', value: 1 },
  { label: '离异', value: 2 },
] as const

function findOptionIndex<T extends ReadonlyArray<{ value: number }>>(options: T, value?: number): number {
  const index = options.findIndex((item) => item.value === value)
  return index >= 0 ? index : 0
}

Page({
  data: {
    loggedIn: false,
    loading: false,
    saving: false,
    userId: '',
    avatar: defaultAvatar,
    nickname: '',
    city: '',
    profession: '',
    income: '',
    birthday: '',
    tagText: '',
    genderOptions,
    marriageOptions,
    genderIndex: 0,
    marriageIndex: 0,
    likeNotification: true,
    commentNotification: true,
    systemNotification: true,
  },

  onShow() {
    this.bootstrap()
  },

  async bootstrap() {
    const session = getSession()
    if (!session) {
      this.setData({ loggedIn: false, userId: '' })
      return
    }

    const settingDraft = getNotificationSettingDraft(session.userId)

    this.setData({
      loggedIn: true,
      userId: session.userId,
      likeNotification: settingDraft.likeNotification,
      commentNotification: settingDraft.commentNotification,
      systemNotification: settingDraft.systemNotification,
    })

    await this.loadProfile(session.userId)
  },

  async loadProfile(userId: string) {
    console.log('加载个人资料，用户ID:', userId, typeof userId)
    this.setData({ loading: true })
    try {
      const profile = await getUserProfile(userId)
      console.log('获取个人资料成功:', profile)
      const app = getApp<IAppOption>()
      app.globalData.profileDraft = {
        nickname: profile.nickname || '',
        avatar: profile.avatar || defaultAvatar,
      }

      // 处理tags字段，将JSON字符串转换为数组
      let tagText = ''
      if (profile.tags) {
        try {
          const tagsObj = typeof profile.tags === 'string' ? JSON.parse(profile.tags) : profile.tags
          if (tagsObj && typeof tagsObj === 'object') {
            tagText = Object.keys(tagsObj).join('、')
          }
        } catch (e) {
          console.error('解析tags失败:', e)
        }
      }

      this.setData({
        avatar: profile.avatar || defaultAvatar,
        nickname: profile.nickname || '',
        city: profile.city || '',
        profession: profile.profession || '',
        income: profile.income || '',
        birthday: profile.birthday || '',
        tagText: tagText,
        genderIndex: findOptionIndex(genderOptions, profile.gender),
        marriageIndex: findOptionIndex(marriageOptions, profile.marriage),
      })
    } catch (error) {
      console.error('加载个人资料失败:', error)
      wx.showToast({
        title: getErrorMessage(error),
        icon: 'none',
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  goLogin() {
    wx.navigateTo({
      url: '/pages/login/index',
    })
  },

  handleAvatarChoose(event: WechatMiniprogram.CustomEvent<{ avatarUrl: string }>) {
    this.setData({
      avatar: event.detail.avatarUrl,
    })
  },

  handleInput(event: WechatMiniprogram.Input) {
    const field = event.currentTarget.dataset.field as string
    this.setData({
      [field]: event.detail.value.trim(),
    })
  },

  handleGenderChange(event: WechatMiniprogram.PickerChange) {
    this.setData({
      genderIndex: Number(event.detail.value),
    })
  },

  handleMarriageChange(event: WechatMiniprogram.PickerChange) {
    this.setData({
      marriageIndex: Number(event.detail.value),
    })
  },

  handleSwitchChange(event: WechatMiniprogram.SwitchChange) {
    const field = event.currentTarget.dataset.field as string
    const checked = event.detail.value
    this.setData({
      [field]: checked,
    })

    if (this.data.userId) {
      saveNotificationSettingDraft(this.data.userId, {
        likeNotification: field === 'likeNotification' ? checked : this.data.likeNotification,
        commentNotification: field === 'commentNotification' ? checked : this.data.commentNotification,
        systemNotification: field === 'systemNotification' ? checked : this.data.systemNotification,
      })
    }
  },

  async submitProfile() {
    const session = getSession()
    if (!session) {
      this.goLogin()
      return
    }

    if (this.data.saving) {
      return
    }

    this.setData({ saving: true })

    const tagArray = this.data.tagText
      .split(/[、,，\s]+/)
      .map((item) => item.trim())
      .filter(Boolean)

    // 将tags数组转换为Map格式
    const tagsMap: Record<string, any> = {}
    tagArray.forEach((tag, index) => {
      tagsMap[tag] = true
    })

    const profilePayload = {
      nickname: this.data.nickname || undefined,
      avatar: this.data.avatar || undefined,
      city: this.data.city || undefined,
      profession: this.data.profession || undefined,
      income: this.data.income || undefined,
      birthday: this.data.birthday || undefined,
      tags: Object.keys(tagsMap).length > 0 ? tagsMap : undefined,
      gender: genderOptions[this.data.genderIndex].value,
      marriage: marriageOptions[this.data.marriageIndex].value,
    }

    const settingPayload = {
      likeNotification: this.data.likeNotification ? 1 : 0,
      commentNotification: this.data.commentNotification ? 1 : 0,
      systemNotification: this.data.systemNotification ? 1 : 0,
    }

    console.log('提交个人资料，用户ID:', session.userId)
    console.log('个人资料数据:', profilePayload)
    console.log('设置数据:', settingPayload)

    try {
      await updateUserProfile(session.userId, profilePayload)
      console.log('更新个人资料成功')
      await updateUserSetting(session.userId, settingPayload)
      console.log('更新设置成功')

      const app = getApp<IAppOption>()
      app.globalData.profileDraft = {
        nickname: this.data.nickname,
        avatar: this.data.avatar,
      }

      saveNotificationSettingDraft(session.userId, settingPayload)

      wx.showToast({
        title: '保存成功',
        icon: 'success',
      })
    } catch (error) {
      console.error('保存失败:', error)
      wx.showToast({
        title: getErrorMessage(error),
        icon: 'none',
      })
    } finally {
      this.setData({ saving: false })
    }
  },

  resetNotificationSetting() {
    if (!this.data.userId) {
      return
    }

    this.setData({
      likeNotification: defaultNotificationSetting.likeNotification,
      commentNotification: defaultNotificationSetting.commentNotification,
      systemNotification: defaultNotificationSetting.systemNotification,
    })

    saveNotificationSettingDraft(this.data.userId, defaultNotificationSetting)
  },
})
