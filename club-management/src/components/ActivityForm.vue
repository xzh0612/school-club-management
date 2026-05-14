<template>
  <div class="modal-overlay" @click="handleOverlayClick">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ isEdit ? '编辑活动' : '创建活动' }}</h3>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>
      
      <form @submit.prevent="handleSubmit" class="activity-form">
        <div class="form-group">
          <label>活动名称 *</label>
          <input 
            v-model="formData.name" 
            type="text" 
            placeholder="请输入活动名称"
            required
          />
        </div>
        
        <div class="form-row">
          <div class="form-group">
            <label>所属社团 *</label>
            <select v-model="formData.clubId" required>
              <option value="">请选择社团</option>
              <option v-for="club in clubs" :key="club.id" :value="club.id">
                {{ club.name }}
              </option>
            </select>
          </div>
          
          <div class="form-group">
            <label>活动类型 *</label>
            <select v-model="formData.type" required>
              <option value="">请选择活动类型</option>
              <option value="学术科技">学术科技</option>
              <option value="文艺体育">文艺体育</option>
              <option value="志愿服务">志愿服务</option>
              <option value="创新创业">创新创业</option>
              <option value="其他">其他</option>
            </select>
          </div>
        </div>
        
        <div class="form-row">
          <div class="form-group">
            <label>最大参与人数 *</label>
            <input 
              v-model.number="formData.maxParticipants" 
              type="number" 
              min="1"
              placeholder="请输入最大参与人数"
              required
            />
          </div>
          
          <div class="form-group">
            <label>报名截止时间 *</label>
            <input 
              v-model="formData.registrationDeadline" 
              type="datetime-local" 
              required
            />
          </div>
        </div>
        
        <div class="form-row">
          <div class="form-group">
            <label>活动开始时间 *</label>
            <input 
              v-model="formData.startTime" 
              type="datetime-local" 
              required
            />
          </div>
          
          <div class="form-group">
            <label>活动结束时间 *</label>
            <input 
              v-model="formData.endTime" 
              type="datetime-local" 
              required
            />
          </div>
        </div>
        
        <div class="form-group">
          <label>活动地点 *</label>
          <input 
            v-model="formData.location" 
            type="text" 
            placeholder="请输入活动地点"
            required
          />
        </div>
        
        <div class="form-group">
          <label>活动负责人 *</label>
          <input 
            v-model="formData.organizer" 
            type="text" 
            placeholder="请输入活动负责人姓名"
            required
          />
        </div>
        
        <div class="form-group">
          <label>联系方式 *</label>
          <input 
            v-model="formData.contact" 
            type="text" 
            placeholder="请输入联系电话或邮箱"
            required
          />
        </div>
        
        <div class="form-group">
          <label>活动简介</label>
          <textarea 
            v-model="formData.description" 
            placeholder="请输入活动简介（可选）"
            rows="3"
          ></textarea>
        </div>
        
        <div class="form-group">
          <label>活动详情</label>
          <textarea 
            v-model="formData.details" 
            placeholder="请输入活动详细安排（可选）"
            rows="4"
          ></textarea>
        </div>
        
        <div class="form-actions">
          <button type="button" class="btn btn-outline" @click="$emit('close')">
            取消
          </button>
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? '提交中...' : (isEdit ? '保存修改' : '创建活动') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getClubList } from '../api/club'

const props = defineProps({
  isEdit: {
    type: Boolean,
    default: false
  },
  initialData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['close', 'submit'])

const clubs = ref([])

const loading = ref(false)

// 表单数据
const formData = reactive({
  name: '',
  clubId: '',
  type: '',
  maxParticipants: '',
  registrationDeadline: '',
  startTime: '',
  endTime: '',
  location: '',
  organizer: '',
  contact: '',
  description: '',
  details: ''
})

// 初始化表单数据
watch(() => props.initialData, (newVal) => {
  if (props.isEdit && newVal) {
    Object.assign(formData, {
      name: newVal.name || '',
      clubId: newVal.clubId || '',
      type: newVal.type || '',
      maxParticipants: newVal.maxParticipants || '',
      registrationDeadline: newVal.registrationDeadline || '',
      startTime: newVal.startTime || '',
      endTime: newVal.endTime || '',
      location: newVal.location || '',
      organizer: newVal.organizer || '',
      contact: newVal.contact || '',
      description: newVal.description || '',
      details: newVal.details || ''
    })
  }
}, { immediate: true })

// 设置默认时间
const now = new Date()
const tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000)
formData.registrationDeadline = formData.registrationDeadline || tomorrow.toISOString().slice(0, 16)
formData.startTime = formData.startTime || tomorrow.toISOString().slice(0, 16)
formData.endTime = formData.endTime || new Date(tomorrow.getTime() + 2 * 60 * 60 * 1000).toISOString().slice(0, 16)

const handleSubmit = async () => {
  // 表单验证
  if (!formData.name || !formData.clubId || !formData.type || 
      !formData.maxParticipants || !formData.registrationDeadline ||
      !formData.startTime || !formData.endTime || !formData.location ||
      !formData.organizer || !formData.contact) {
    ElMessage.warning('请填写所有必填项')
    return
  }
  
  if (new Date(formData.registrationDeadline) >= new Date(formData.startTime)) {
    ElMessage.warning('报名截止时间必须早于活动开始时间')
    return
  }
  
  if (new Date(formData.startTime) >= new Date(formData.endTime)) {
    ElMessage.warning('活动开始时间必须早于结束时间')
    return
  }
  
  loading.value = true
  
  try {
    const submitData = {
      ...formData,
      clubName: clubs.value.find(c => c.id === formData.clubId)?.name,
      status: props.isEdit && props.initialData.status ? props.initialData.status : 'pending_approval'
    }
    
    emit('submit', submitData)
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    loading.value = false
  }
}

const loadClubs = async () => {
  const response = await getClubList(1, 100, 'approved', '')
  const records = response.data?.records || []
  clubs.value = records
    .filter(club => club.status === 'approved')
    .map(club => ({ id: club.clubId, name: club.clubName }))
}

onMounted(loadClubs)

const handleOverlayClick = () => {
  if (!loading.value) {
    emit('close')
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 700px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #333;
}

.activity-form {
  padding: 24px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3B82F6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: #3B82F6;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #2563EB;
}

.btn-outline {
  background: white;
  color: #6B7280;
  border: 1px solid #D1D5DB;
}

.btn-outline:hover {
  background: #F9FAFB;
  border-color: #9CA3AF;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    width: 95%;
    margin: 10px;
  }
}
</style>
