import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录', public: true }
  },
  // ===== Admin routes =====
  {
    path: '/admin',
    component: () => import('../components/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '仪表盘' } },
      { path: 'users', name: 'UserManagement', component: () => import('../views/admin/UserManagement.vue'), meta: { title: '用户管理' } },
      { path: 'roles', name: 'RoleManagement', component: () => import('../views/admin/RoleManagement.vue'), meta: { title: '权限配置' } },
      { path: 'clubs', name: 'ClubManagement', component: () => import('../views/admin/ClubManagement.vue'), meta: { title: '社团管理' } },
      { path: 'approval', name: 'ApprovalCenter', component: () => import('../views/admin/ApprovalCenter.vue'), meta: { title: '审批中心' } },
      { path: 'activities', name: 'ActivityManagement', component: () => import('../views/admin/ActivityManagement.vue'), meta: { title: '活动管理' } },
      { path: 'recruitment', name: 'RecruitmentManagement', component: () => import('../views/admin/RecruitmentManagement.vue'), meta: { title: '招新管理' } },
      { path: 'announcements', name: 'AnnouncementManagement', component: () => import('../views/admin/AnnouncementManagement.vue'), meta: { title: '公告资讯' } },
      { path: 'logs', name: 'OperationLogs', component: () => import('../views/admin/OperationLogs.vue'), meta: { title: '操作日志' } },
      { path: 'statistics', name: 'Statistics', component: () => import('../views/admin/Statistics.vue'), meta: { title: '数据统计' } },
    ]
  },
  // ===== Student routes =====
  {
    path: '/student',
    component: () => import('../components/StudentLayout.vue'),
    redirect: '/student/square',
    children: [
      { path: 'square', name: 'ClubSquare', component: () => import('../views/student/ClubSquare.vue'), meta: { title: '社团广场' } },
      { path: 'my-clubs', name: 'MyClubs', component: () => import('../views/student/MyClubs.vue'), meta: { title: '我的社团' } },
      { path: 'activities', name: 'StudentActivities', component: () => import('../views/student/ActivityCenter.vue'), meta: { title: '活动中心' } },
      { path: 'announcements', name: 'StudentAnnouncements', component: () => import('../views/student/Announcements.vue'), meta: { title: '公告通知' } },
      { path: 'profile', name: 'StudentProfile', component: () => import('../views/student/Profile.vue'), meta: { title: '个人中心' } },
    ]
  },
  // ===== Catch all → login =====
  { path: '/', redirect: '/login' },
  { path: '/:pathMatch(.*)*', redirect: '/login' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

function normalizeRole(role) {
  const roleMap = {
    admin: 'admin',
    ADMIN: 'admin',
    '系统管理员': 'admin',
    teacher: 'teacher',
    TEACHER: 'teacher',
    '指导老师': 'teacher',
    student: 'student',
    STUDENT: 'student',
    '普通学生': 'student',
    leader: 'club_leader',
    LEADER: 'club_leader',
    club_leader: 'club_leader',
    CLUB_LEADER: 'club_leader',
    '社团负责人': 'club_leader',
    '社团管理员': 'club_leader'
  }
  return roleMap[role] || ''
}

const routePermissions = {
  '/admin/dashboard': ['admin', 'club_leader', 'teacher'],
  '/admin/users': ['admin'],
  '/admin/roles': ['admin'],
  '/admin/clubs': ['admin', 'club_leader', 'teacher'],
  '/admin/approval': ['admin', 'teacher'],
  '/admin/activities': ['club_leader'],
  '/admin/recruitment': ['club_leader'],
  '/admin/announcements': ['admin', 'club_leader'],
  '/admin/logs': ['admin'],
  '/admin/statistics': ['admin', 'teacher'],
}

function fallbackRoute(role) {
  const map = {
    admin: '/admin/dashboard',
    club_leader: '/admin/clubs',
    teacher: '/admin/approval',
    student: '/student/square'
  }
  return map[role] || '/login'
}

// Navigation guard
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '社团管理系统'} - 学校社团管理系统`
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  const normalizedRole = normalizeRole(user?.role)
  
  // 检查是否是公共路径（登录页）
  if (to.meta.public) {
    next()
    return
  }
  
  // 未登录，跳转到登录页
  if (!user) {
    next({ path: '/login', replace: true })
    return
  }
  
  // 检查管理员路径权限
  if (to.path.startsWith('/admin')) {
    if (normalizedRole !== 'admin' && normalizedRole !== 'club_leader' && normalizedRole !== 'teacher') {
      next({ path: '/student/square', replace: true })
      return
    }
    const allowedRoles = routePermissions[to.path]
    if (allowedRoles && !allowedRoles.includes(normalizedRole)) {
      next({ path: fallbackRoute(normalizedRole), replace: true })
      return
    }
  }
  
  // 检查学生路径权限
  if (to.path.startsWith('/student')) {
    if (normalizedRole !== 'student') {
      next({ path: '/admin/dashboard', replace: true })
      return
    }
  }
  
  next()
})

export default router
