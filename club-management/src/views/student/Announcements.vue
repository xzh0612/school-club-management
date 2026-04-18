<template>
  <div>
    <div class="card" style="margin-bottom:16px">
      <div class="search-bar" style="margin-bottom:0">
        <input class="search-input" placeholder="搜索公告..." v-model="searchKeyword" />
        <select class="filter-select" v-model="typeFilter">
          <option value="">全部分类</option>
          <option value="通知公告">通知公告</option>
          <option value="活动预告">活动预告</option>
          <option value="系统公告">系统公告</option>
        </select>
        <button class="btn btn-primary" @click="searchAnnouncements">🔍</button>
      </div>
    </div>
    
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>正在加载公告...</p>
    </div>
    
    <div v-else>
      <div class="announce-card" v-for="a in filteredAnnouncements" :key="a.id" :style="{borderLeftColor: getTypeColor(a.type)}">
        <div style="display:flex;justify-content:space-between;align-items:center">
          <div>
            <h4>{{a.pinned?'📌 ':''}}{{a.title}}</h4>
            <div class="meta">{{a.author}} · {{formatDate(a.createdAt)}} · 浏览 {{a.viewCount}}</div>
          </div>
          <span :class="['tag',getTypeClass(a.type)]">{{a.pinned?'置顶':a.type}}</span>
        </div>
        <p class="content">{{a.content}}</p>
      </div>
      
      <div v-if="filteredAnnouncements.length === 0" class="empty-state">
        <div class="empty-icon">📢</div>
        <p>暂无公告信息</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAnnouncementList } from '../../api/announcement'

// 响应式数据
const searchKeyword = ref('')
const typeFilter = ref('')
const loading = ref(false)

const announcements = ref([])

// 计算属性：过滤后的公告
const filteredAnnouncements = computed(() => {
  let result = [...announcements.value]
  
  // 按类型过滤
  if (typeFilter.value) {
    result = result.filter(announcement => announcement.type === typeFilter.value)
  }
  
  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(announcement => 
      announcement.title.toLowerCase().includes(keyword) ||
      announcement.content.toLowerCase().includes(keyword)
    )
  }
  
  // 置顶公告排在前面
  result.sort((a, b) => {
    if (a.pinned && !b.pinned) return -1
    if (!a.pinned && b.pinned) return 1
    return new Date(b.createdAt) - new Date(a.createdAt)
  })
  
  return result
})

// 获取类型样式类
const getTypeClass = (type) => {
  const typeMap = {
    '通知公告': 'tag-red',
    '活动预告': 'tag-orange',
    '系统公告': 'tag-blue'
  }
  return typeMap[type] || 'tag-gray'
}

// 获取类型边框颜色
const getTypeColor = (type) => {
  const colorMap = {
    '通知公告': '#EF4444',
    '活动预告': '#F59E0B',
    '系统公告': '#3B82F6'
  }
  return colorMap[type] || '#D1D5DB'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 获取公告列表
const fetchAnnouncements = async () => {
  loading.value = true
  try {
    const response = await getAnnouncementList(1, 50, '')
    if (response.code === 200) {
      announcements.value = response.data.list || response.data || []
    } else {
      // API调用失败时使用模拟数据
      generateMockAnnouncements()
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
    // 网络错误时使用模拟数据
    generateMockAnnouncements()
  } finally {
    loading.value = false
  }
}

// 生成模拟公告数据
const generateMockAnnouncements = () => {
  const mockAnnouncements = [
    {
      id: 1,
      title: '关于2026年春季社团年审工作的通知',
      content: '各社团负责人：根据《学生社团管理办法》，现启动2026年春季社团年度审查工作。请各社团于4月15日前提交年审材料，包括年度活动总结、财务报告、社员名册等。年审结果将影响社团下一年度的活动资格和经费支持。',
      type: '通知公告',
      author: '团委办公室',
      viewCount: 2341,
      pinned: true,
      createdAt: new Date(Date.now() - 6 * 24 * 60 * 60 * 1000).toISOString()
    },
    {
      id: 2,
      title: '2026年度优秀社团评选通知',
      content: '为表彰优秀社团，激发社团活力，现开展2026年度优秀社团评选。评选标准包括社团活跃度、活动质量、社员满意度等维度。获奖社团将获得荣誉证书和专项活动经费支持。报名截止时间：4月30日。',
      type: '通知公告',
      author: '团委办公室',
      viewCount: 1523,
      pinned: true,
      createdAt: new Date(Date.now() - 13 * 24 * 60 * 60 * 1000).toISOString()
    },
    {
      id: 3,
      title: '计算机协会编程马拉松报名开启',
      content: '第8届编程马拉松大赛来了！24小时限时挑战，3-5人组队参赛，丰厚奖金等你来拿！本次比赛主题为"AI应用创新"，鼓励参赛者开发具有实际应用价值的人工智能项目。报名截止：3月24日。',
      type: '活动预告',
      author: '计算机协会',
      viewCount: 678,
      pinned: false,
      createdAt: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000).toISOString()
    },
    {
      id: 4,
      title: '社团管理系统升级维护通知',
      content: '为提升系统性能和用户体验，计划于3月20日凌晨2:00-6:00进行系统升级维护，届时系统将暂停服务。请各位用户提前做好相关安排，给您带来的不便敬请谅解。',
      type: '系统公告',
      author: '系统管理员',
      viewCount: 1856,
      pinned: false,
      createdAt: new Date(Date.now() - 11 * 24 * 60 * 60 * 1000).toISOString()
    },
    {
      id: 5,
      title: '篮球社新生杯赛程公布',
      content: '2026年新生杯篮球赛程正式公布！小组赛将于3月20日开打，共16支队伍参赛。比赛分为A、B两个小组，采用循环赛制。决赛将于4月5日在体育馆举行，欢迎大家前来观赛加油！',
      type: '活动预告',
      author: '篮球社',
      viewCount: 445,
      pinned: false,
      createdAt: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString()
    },
    {
      id: 6,
      title: '摄影社春季外拍活动招募',
      content: '春天来了，让我们一起走出教室，用镜头记录校园美景！本次外拍活动定于3月22日（周六）上午8点集合，地点为学校南门。提供专业相机借用，欢迎摄影爱好者报名参加。',
      type: '活动预告',
      author: '摄影社',
      viewCount: 312,
      pinned: false,
      createdAt: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString()
    }
  ]
  
  announcements.value = mockAnnouncements
  ElMessage.info('使用模拟数据展示公告信息')
}

// 搜索公告
const searchAnnouncements = () => {
  // 搜索逻辑已在计算属性中实现
  console.log('搜索关键词:', searchKeyword.value)
}

// 组件挂载时获取数据
onMounted(() => {
  fetchAnnouncements()
})
</script>

<style scoped>
.announce-card { background:#fff; border-radius:12px; padding:20px 24px; border-left:4px solid #D1D5DB; box-shadow:0 1px 3px rgba(0,0,0,0.06); margin-bottom:12px; }
.announce-card h4 { font-size:14px; margin-bottom:4px; }
.meta { font-size:12px; color:#9CA3AF; }
.content { font-size:13px; color:#6B7280; margin-top:10px; line-height:1.8; }

.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: #6B7280;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #E5E7EB;
  border-top: 4px solid #3B82F6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #6B7280;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}
</style>