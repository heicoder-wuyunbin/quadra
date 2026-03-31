import {
  addBlacklist,
  formatDateTime,
  getErrorMessage,
  getSession,
  listMyBlacklist,
  removeBlacklist,
} from '../../utils/api'

Page({
  data: {
    loggedIn: false,
    loading: false,
    saving: false,
    pageNo: 1,
    pageSize: 10,
    hasMore: true,
    targetUserId: '',
    items: [] as Array<{
      userId: number
      nickname: string
      avatar: string
      blacklistedAt: string
    }>,
  },

  onShow() {
    this.bootstrap()
  },

  async bootstrap() {
    const session = getSession()
    if (!session) {
      this.setData({
        loggedIn: false,
        items: [],
      })
      return
    }

    this.setData({
      loggedIn: true,
      pageNo: 1,
      hasMore: true,
    })

    await this.loadList(true)
  },

  async loadList(reset = false) {
    if (this.data.loading) {
      return
    }

    this.setData({ loading: true })

    try {
      const pageNo = reset ? 1 : this.data.pageNo
      const result = await listMyBlacklist(pageNo, this.data.pageSize)
      const incoming = result.list.map((item) => ({
        userId: item.userId,
        nickname: item.nickname || `用户 ${item.userId}`,
        avatar: item.avatar || '',
        blacklistedAt: formatDateTime(item.blacklistedAt),
      }))

      this.setData({
        items: reset ? incoming : this.data.items.concat(incoming),
        pageNo: pageNo + 1,
        hasMore: result.list.length >= this.data.pageSize,
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

  handleTargetInput(event: WechatMiniprogram.Input) {
    this.setData({
      targetUserId: event.detail.value.trim(),
    })
  },

  async submitAdd() {
    if (!this.data.targetUserId) {
      wx.showToast({
        title: '请输入目标用户 ID',
        icon: 'none',
      })
      return
    }

    if (this.data.saving) {
      return
    }

    this.setData({ saving: true })

    try {
      await addBlacklist(Number(this.data.targetUserId))
      wx.showToast({
        title: '拉黑成功',
        icon: 'success',
      })
      this.setData({
        targetUserId: '',
      })
      await this.loadList(true)
    } catch (error) {
      wx.showToast({
        title: getErrorMessage(error),
        icon: 'none',
      })
    } finally {
      this.setData({ saving: false })
    }
  },

  async handleRemove(event: WechatMiniprogram.BaseEvent) {
    const { userId } = event.currentTarget.dataset as { userId: number }

    try {
      await removeBlacklist(Number(userId))
      wx.showToast({
        title: '已移除',
        icon: 'success',
      })
      await this.loadList(true)
    } catch (error) {
      wx.showToast({
        title: getErrorMessage(error),
        icon: 'none',
      })
    }
  },

  loadMore() {
    if (!this.data.hasMore) {
      return
    }

    this.loadList(false)
  },

  goLogin() {
    wx.navigateTo({
      url: '/pages/login/index',
    })
  },
})
