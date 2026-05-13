<template>
  <div class="modal-overlay" @click="handleOverlayClick">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ isEdit ? '编辑招新计划' : '发布招新计划' }}</h3>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>
      
      <form @submit.prevent="handleSubmit" class="recruitment-form">
        <div class="form-group">
          <label>选择社团 *</label>
          <select v-model="formData.clubId" required>
            <option value="">请选择社团</option>
            <option v-for="club in clubs" :key="club.id" :value="club.id">
              {{ club.name }}
            </option>
          </select>
        </div>
        
        <div class="form-row">
          <div class="form-group">
            <label>招新标题 *</label>
            <input 
              v-model="formData.title" 
              type="text" 
              placeholder="请输入招新标题"
              required
            />
          </div>
          
          <div class="form-group">
            <label>招新人数 *</label>
            <input 
              v-model.number="formData.quota" 
              type="number" 
              min="1"
              placeholder="请输入招新人数"
              required
            />
          </div>
        </div>
        
        <div class="form-row">
          <div class="form-group">
            <label>开始时间 *</label>
            <input 
              v-model="formData.startTime" 
              type="date" 
              required
            />
          </div>
          
          <div class="form-group">
            <label>截止时间 *</label>
            <input 
              v-model="formData.endTime" 
              type="date" 
              required
            />
          </div>
        </div>
        
        <div class="form-group">
          <label>招新要求</label>
          <textarea 
            v-model="formData.requirements" 
            placeholder="请输入招新要求（可选）"
            rows="3"
          ></textarea>
        </div>
        
        <div class="form-group">
          <label>详细介绍</label>
          <textarea 
            v-model="formData.description" 
            placeholder="请输入招新详细介绍（可选）"
            rows="4"
          ></textarea>
        </div>
        
        <div class="form-actions">
          <button type="button" class="btn btn-outline" @click="$emit('close')">
            取消
          </button>
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? '提交中...' : (isEdit ? '保存修改' : '发布计划') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
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
  clubId: '',
  title: '',
  quota: '',
  startTime: '',
  endTime: '',
  requirements: '',
  description: ''
})

// 初始化表单数据
watch(() => props.initialData, (newVal) => {
  if (props.isEdit && newVal) {
    Object.assign(formData, {
      clubId: newVal.clubId || '',
      title: newVal.title || '',
      quota: newVal.quota || '',
      startTime: newVal.startTime || '',
      endTime: newVal.endTime || '',
      requirements: newVal.requirements || '',
      description: newVal.description || ''
    })
  }
}, { immediate: true })

// 设置默认日期
const today = new Date().toISOString().split('T')[0]
formData.startTime = formData.startTime || today
formData.endTime = formData.endTime || today

const handleSubmit = async () => {
  if (!formData.clubId || !formData.title || !formData.quota || 
      !formData.startTime || !formData.endTime) {
    alert('请填写所有必填项')
    return
  }
  
  if (new Date(formData.startTime) >= new Date(formData.endTime)) {
    alert('开始时间必须早于截止时间')
    return
  }
  
  loading.value = true
  
  try {
    const submitData = {
      ...formData,
      clubName: clubs.value.find(c => c.id === formData.clubId)?.name
    }
    
    emit('submit', submitData)
  } catch (error) {
    console.error('提交失败:', error)
    alert('提交失败，请重试')
  } finally {
    loading.value = false
  }
}

const loadClubs = async () => {
  const response = await getClubList(1, 100, 'approved', '')
  const records = response.data?.records || []
  clubs.value = records.map(club => ({ id: club.clubId, name: club.clubName }))
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
  max-width: 600px;
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

.recruitment-form {
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
