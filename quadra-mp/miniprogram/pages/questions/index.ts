import {
  addQuestion,
  deleteQuestion,
  formatDateTime,
  getErrorMessage,
  getSession,
  listMyQuestions,
  updateQuestion,
} from '../../utils/api'

Page({
  data: {
    loggedIn: false,
    loading: false,
    saving: false,
    pageNo: 1,
    pageSize: 10,
    hasMore: true,
    editingId: 0,
    question: '',
    sortOrder: '1',
    items: [] as Array<{
      questionId: number
      question: string
      sortOrder: number
      createdAt: string
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
      const result = await listMyQuestions(pageNo, this.data.pageSize)
      const incoming = result.list.map((item) => ({
        questionId: item.questionId,
        question: item.question,
        sortOrder: item.sortOrder || 0,
        createdAt: formatDateTime(item.createdAt),
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

  handleQuestionInput(event: WechatMiniprogram.Input) {
    this.setData({
      question: event.detail.value,
    })
  },

  handleSortInput(event: WechatMiniprogram.Input) {
    this.setData({
      sortOrder: event.detail.value.trim(),
    })
  },

  async submitQuestion() {
    if (!this.data.question.trim()) {
      wx.showToast({
        title: '请输入问题内容',
        icon: 'none',
      })
      return
    }

    if (this.data.saving) {
      return
    }

    this.setData({ saving: true })

    try {
      const sortOrder = Number(this.data.sortOrder || 0)
      if (this.data.editingId) {
        await updateQuestion(this.data.editingId, this.data.question.trim(), sortOrder)
      } else {
        await addQuestion(this.data.question.trim(), sortOrder)
      }

      wx.showToast({
        title: this.data.editingId ? '更新成功' : '添加成功',
        icon: 'success',
      })

      this.setData({
        editingId: 0,
        question: '',
        sortOrder: '1',
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

  startEdit(event: WechatMiniprogram.BaseEvent) {
    const { questionId } = event.currentTarget.dataset as { questionId: number }
    const target = this.data.items.find((item) => item.questionId === Number(questionId))
    if (!target) {
      return
    }

    this.setData({
      editingId: target.questionId,
      question: target.question,
      sortOrder: String(target.sortOrder || 1),
    })
  },

  async handleDelete(event: WechatMiniprogram.BaseEvent) {
    const { questionId } = event.currentTarget.dataset as { questionId: number }

    try {
      await deleteQuestion(Number(questionId))
      wx.showToast({
        title: '已删除',
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

  cancelEdit() {
    this.setData({
      editingId: 0,
      question: '',
      sortOrder: '1',
    })
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
