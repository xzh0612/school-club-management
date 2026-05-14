<template>
  <div>
    <div class="stats-grid">
      <div class="stat-card"><div class="icon-bg">📢</div><div class="label">公告总数</div><div class="value">{{ announcements.length }}</div></div>
      <div class="stat-card"><div class="icon-bg">📌</div><div class="label">置顶公告</div><div class="value">{{ announcements.filter(a => a.isTop).length }}</div></div>
      <div class="stat-card"><div class="icon-bg">👁️</div><div class="label">浏览量</div><div class="value">{{ totalViews }}</div></div>
      <div class="stat-card"><div class="icon-bg">💬</div><div class="label">已发布</div><div class="value">{{ announcements.filter(a => a.status === 'published').length }}</div></div>
    </div>
    <div class="card">
      <div class="card-header"><h3>公告列表</h3><button class="btn btn-primary btn-sm" @click="openAnnouncementForm">＋ 发布公告</button></div>
      <div class="search-bar"><input v-model="searchKeyword" class="search-input" placeholder="搜索公告标题/内容..." @keyup.enter="handleSearch" /><button class="btn btn-primary btn-sm" @click="handleSearch">搜索</button></div>
      <table class="data-table">
        <thead><tr><th>标题</th><th>范围</th><th>发布人</th><th>状态</th><th>发布时间</th><th>浏览量</th><th>置顶</th><th>操作</th></tr></thead>
        <tbody><tr v-for="a in announcements" :key="a.announcementId"><td>{{ a.isTop ? '📌 ' : '' }}{{ a.title }}</td><td>{{ a.targetType === 'all' ? '全校' : '社团' }}</td><td>{{ a.publisherName }}</td><td><span class="tag tag-blue">{{ statusText(a.status) }}</span></td><td>{{ formatDate(a.publishTime) }}</td><td>{{ a.viewCount }}</td><td>{{ a.isTop ? '是' : '否' }}</td><td><button class="btn btn-warning btn-sm" @click="editAnnouncement(a)">编辑</button><button v-if="isAdmin" class="btn btn-outline btn-sm" @click="togglePin(a)" style="margin-left:8px">{{ a.isTop ? '取消置顶' : '置顶' }}</button><button class="btn btn-danger btn-sm" @click="removeAnnouncement(a)" style="margin-left:8px">归档</button></td></tr></tbody>
      </table>
      <div v-if="announcements.length === 0" class="empty-state">暂无公告数据</div>
      <div class="pagination"><button class="btn btn-outline btn-sm" :disabled="currentPage <= 1" @click="changePage(currentPage - 1)">上一页</button><span class="page-info">第 {{ currentPage }} 页，共 {{ totalPages }} 页</span><button class="btn btn-outline btn-sm" :disabled="currentPage >= totalPages" @click="changePage(currentPage + 1)">下一页</button></div>
    </div>
    <AnnouncementForm v-if="showAnnouncementForm" :is-edit="isEditing" :initial-data="editingAnnouncement" @close="closeAnnouncementForm" @submit="handleAnnouncementSubmit" />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AnnouncementForm from '../../components/AnnouncementForm.vue'
import { createAnnouncement, deleteAnnouncement, getAnnouncementList, publishAnnouncement, searchAnnouncements, setAnnouncementTop, updateAnnouncement } from '../../api/announcement'

const searchKeyword = ref('')
const showAnnouncementForm = ref(false)
const isEditing = ref(false)
const editingAnnouncement = ref(null)
const announcements = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentUser = computed(() => JSON.parse(sessionStorage.getItem('user') || '{}'))
const isAdmin = computed(() => currentUser.value.role === 'admin')
const extractRecords = (payload) => payload?.records || payload?.list || (Array.isArray(payload) ? payload : [])
const totalViews = computed(() => announcements.value.reduce((sum, item) => sum + (item.viewCount || 0), 0))
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))
const statusText = (status) => ({ draft: '草稿', published: '已发布', archived: '已归档' }[status] || status)
const formatDate = (value) => value ? new Date(value).toLocaleString('zh-CN') : ''

const fetchAnnouncements = async () => {
  const response = searchKeyword.value
    ? await searchAnnouncements(searchKeyword.value, currentPage.value, pageSize.value)
    : await getAnnouncementList({ page: currentPage.value, size: pageSize.value })
  announcements.value = extractRecords(response.data).map(item => ({ ...item, isTop: Number(item.isTop) === 1 }))
  total.value = response.data?.total || announcements.value.length
}
const handleSearch = async () => { currentPage.value = 1; await fetchAnnouncements() }
const changePage = async (page) => { currentPage.value = Math.min(Math.max(1, page), totalPages.value); await fetchAnnouncements() }
const openAnnouncementForm = () => { isEditing.value = false; editingAnnouncement.value = null; showAnnouncementForm.value = true }
const closeAnnouncementForm = () => { showAnnouncementForm.value = false; editingAnnouncement.value = null }
const editAnnouncement = (announcement) => { isEditing.value = true; editingAnnouncement.value = { ...announcement, pinned: announcement.isTop }; showAnnouncementForm.value = true }
const handleAnnouncementSubmit = async (data) => {
  const payload = { title: data.title, content: data.content, targetType: data.targetType, targetId: data.targetId, isTop: data.pinned ? 1 : 0, status: 'published' }
  if (isEditing.value) await updateAnnouncement(editingAnnouncement.value.announcementId, payload)
  else {
    const created = await createAnnouncement(payload)
    await publishAnnouncement(created.data.announcementId)
  }
  ElMessage.success('保存成功')
  closeAnnouncementForm()
  await fetchAnnouncements()
}
const togglePin = async (announcement) => {
  await setAnnouncementTop(announcement.announcementId, !announcement.isTop)
  await fetchAnnouncements()
}
const removeAnnouncement = async (announcement) => {
  await ElMessageBox.confirm(`确定归档 "${announcement.title}" 吗？`, '提示')
  await deleteAnnouncement(announcement.announcementId)
  await fetchAnnouncements()
}

onMounted(fetchAnnouncements)
</script>
