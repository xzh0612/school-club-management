<template>
  <div>
    <div class="stats-grid">
      <div class="stat-card"><div class="icon-bg">📋</div><div class="label">待我审批</div><div class="value">15</div></div>
      <div class="stat-card"><div class="icon-bg">✅</div><div class="label">已通过</div><div class="value">89</div></div>
      <div class="stat-card"><div class="icon-bg">❌</div><div class="label">已驳回</div><div class="value">6</div></div>
      <div class="stat-card"><div class="icon-bg">📊</div><div class="label">本月处理</div><div class="value">34</div></div>
    </div>
    <div class="card">
      <div class="card-header"><h3>待审批列表</h3><div style="display:flex;gap:8px"><button class="btn btn-primary btn-sm" :class="{'btn-outline':tab!=='all'}" @click="tab='all'">全部</button><button class="btn btn-primary btn-sm" :class="{'btn-outline':tab!=='club'}" @click="tab='club'">社团成立</button><button class="btn btn-primary btn-sm" :class="{'btn-outline':tab!=='activity'}" @click="tab='activity'">活动申请</button><button class="btn btn-outline btn-sm" @click="tab='fund'">经费审批</button></div></div>
      <table class="data-table">
        <thead><tr><th>申请编号</th><th>申请类型</th><th>申请人</th><th>申请内容</th><th>提交时间</th><th>紧急程度</th><th>操作</th></tr></thead>
        <tbody><tr v-for="a in approvals" :key="a.id"><td>{{a.id}}</td><td><span :class="['tag',a.typeClass]">{{a.type}}</span></td><td>{{a.applicant}}</td><td>{{a.content}}</td><td>{{a.time}}</td><td><span :class="['tag',a.urgentClass]">{{a.urgent}}</span></td><td><button class="btn btn-success btn-sm" style="margin-right:8px" @click="handleApprove(a, 'approved')">通过</button><button class="btn btn-danger btn-sm" @click="handleApprove(a, 'rejected')">驳回</button></td></tr></tbody>
      </table>
    </div>
    <div class="card" style="margin-top:16px">
      <div class="card-header"><h3>审批进度追踪</h3></div>
      <div class="progress-track">
        <div class="step done"><div class="step-circle">✓</div><div class="step-label" style="color:#10B981">提交申请</div></div>
        <div class="step-line done"></div>
        <div class="step done"><div class="step-circle">✓</div><div class="step-label" style="color:#10B981">指导老师审核</div></div>
        <div class="step-line active"></div>
        <div class="step current"><div class="step-circle">3</div><div class="step-label" style="color:#3B82F6;font-weight:600">团委审核</div></div>
        <div class="step-line"></div>
        <div class="step"><div class="step-circle">4</div><div class="step-label">公示期</div></div>
        <div class="step-line"></div>
        <div class="step"><div class="step-circle">5</div><div class="step-label">归档完成</div></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getApprovalList, approveApplication, rejectApplication } from '../../api/approval'

const tab = ref('all')
const approvals = ref([
  { approvalId: 1, id:'SP-2026-0089', type:'社团成立', typeClass:'tag-blue', applicant:'李小明', content:'机器人创新社成立申请', time:'2026-03-19 09:30', urgent:'紧急', urgentClass:'tag-red' },
  { approvalId: 2, id:'HD-2026-0156', type:'活动申请', typeClass:'tag-orange', applicant:'奚梓恒', content:'编程马拉松大赛', time:'2026-03-18 16:00', urgent:'普通', urgentClass:'tag-orange' },
  { approvalId: 3, id:'JF-2026-0034', type:'经费审批', typeClass:'tag-purple', applicant:'马虹华', content:'文学社春季读书会 - ¥2,000', time:'2026-03-18 14:20', urgent:'普通', urgentClass:'tag-orange' },
  { approvalId: 4, id:'NJ-2026-0078', type:'年审材料', typeClass:'tag-gray', applicant:'王宇航', content:'辩论社2025年度年审', time:'2026-03-17 10:00', urgent:'一般', urgentClass:'tag-blue' },
  { approvalId: 5, id:'HD-2026-0155', type:'活动申请', typeClass:'tag-orange', applicant:'刘硕', content:'篮球社新生杯比赛', time:'2026-03-17 09:15', urgent:'一般', urgentClass:'tag-blue' },
])

// 处理审批操作
const handleApprove = async (approval, action) => {
  try {
    console.log('开始审批操作:', approval.approvalId, action)
    if (action === 'approved') {
      const result = await approveApplication(approval.approvalId, '审批通过')
      console.log('通过审批结果:', result)
      alert('审批通过成功！')
    } else {
      const result = await rejectApplication(approval.approvalId, '审批驳回')
      console.log('驳回审批结果:', result)
      alert('审批驳回成功！')
    }
    // 从列表中移除已处理的审批
    approvals.value = approvals.value.filter(a => a.approvalId !== approval.approvalId)
  } catch (error) {
    console.error('审批操作失败:', error)
    console.error('错误详情:', error.response?.data || error.message)
    const errorMsg = error.response?.data?.msg || error.response?.data?.message || error.message || '未知错误'
    alert(`审批操作失败: ${errorMsg}`)
  }
}
</script>

<style scoped>
.progress-track { display:flex; align-items:center; gap:24px; padding:16px; background:#F9FAFB; border-radius:8px; flex-wrap:wrap; }
.step { text-align:center; min-width:60px; }
.step-circle { width:40px; height:40px; border-radius:50%; background:#E5E7EB; color:#9CA3AF; display:flex; align-items:center; justify-content:center; margin:0 auto 6px; font-weight:600; }
.step.done .step-circle { background:#10B981; color:#fff; }
.step.current .step-circle { background:#3B82F6; color:#fff; }
.step-label { font-size:12px; color:#9CA3AF; }
.step-line { flex:1; height:2px; background:#E5E7EB; min-width:20px; margin-top:-20px; }
.step-line.done { background:#10B981; }
.step-line.active { background:#3B82F6; }
</style>