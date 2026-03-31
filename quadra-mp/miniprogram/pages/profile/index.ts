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
  { label: '未知', value: 'UNKNOWN' },
  { label: '男', value: 'MALE' },
  { label: '女', value: 'FEMALE' },
] as const

const marriageOptions = [
  { label: '单身', value: 'SINGLE' },
  { label: '已婚', value: 'MARRIED' },
  { label: '离异', value: 'DIVORCED' },
] as const

function findOptionIndex<T extends ReadonlyArray<{ value: string }>>(options: T, value?: string): number {
  const index = options.findIndex((item) => item.value === value)
  return index >= 0 ? index : 0
}

Page({
  data: {
    loggedIn: false,
    loading: false,
    saving: false,
    userId: 0,
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
      this.setData({ loggedIn: false, userId: 0 })
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

  async loadProfile(userId: number) {
    this.setData({ loading: true })
    try {
      const profile = await getUserProfile(userId)
      const app = getApp<IAppOption>()
      app.globalData.profileDraft = {
        nickname: profile.nickname || '',
        avatar: profile.avatar || defaultAvatar,
      }

      this.setData({
        avatar: profile.avatar || defaultAvatar,
        nickname: profile.nickname || '',
        city: profile.city || '',
        profession: profile.profession || '',
        income: profile.income || '',
        birthday: profile.birthday || '',
        tagText: (profile.tags || []).join('、'),
        genderIndex: findOptionIndex(genderOptions, profile.gender),
        marriageIndex: findOptionIndex(marriageOptions, profile.marriage),
      })
    } catch (error) {
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

    const tags = this.data.tagText
      .split(/[、,，\s]+/)
      .map((item) => item.trim())
      .filter(Boolean)

    const profilePayload = {
      nickname: this.data.nickname || undefined,
      avatar: this.data.avatar || undefined,
      city: this.data.city || undefined,
      profession: this.data.profession || undefined,
      income: this.data.income || undefined,
      birthday: this.data.birthday || undefined,
      tags,
      gender: genderOptions[this.data.genderIndex].value,
      marriage: marriageOptions[this.data.marriageIndex].value,
    }

    const settingPayload = {
      likeNotification: this.data.likeNotification,
      commentNotification: this.data.commentNotification,
      systemNotification: this.data.systemNotification,
    }

    try {
      await updateUserProfile(session.userId, profilePayload)
      await updateUserSetting(session.userId, settingPayload)

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
