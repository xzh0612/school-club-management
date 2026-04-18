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

// Navigation guard
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '社团管理系统'} - 学校社团管理系统`
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  
  console.log('路由守卫 - 目标路径:', to.path)
  console.log('路由守卫 - 用户信息:', user)
  
  // 检查是否是公共路径（登录页）
  if (to.meta.public) {
    next()
    return
  }
  
  // 未登录，跳转到登录页
  if (!user) {
    console.log('未登录，跳转到登录页')
    next('/login')
    return
  }
  
  // 检查管理员路径权限
  if (to.path.startsWith('/admin')) {
    if (user.role !== 'admin' && user.role !== 'leader' && user.role !== 'teacher') {
      console.log('无管理员权限，跳转到学生页面')
      next('/student/square')
      return
    }
  }
  
  // 检查学生路径权限
  if (to.path.startsWith('/student')) {
    if (user.role !== 'student') {
      console.log('无学生权限，跳转到管理员页面')
      next('/admin/dashboard')
      return
    }
  }
  
  console.log('允许访问')
  next()
})

export default router
