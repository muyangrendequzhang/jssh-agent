<template>
  <div class="connect-page">
    <h1 class="connect-title">Jssh</h1>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" class="connect-form">
      <el-row :gutter="16">
        <!-- 第一行：连接名称 + 主机 -->
        <el-col :span="12">
          <el-form-item label="连接名称" prop="connectName">
            <el-input v-model="form.connectName" placeholder="请输入连接名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="主机" prop="host">
            <el-input v-model="form.host" placeholder="请输入主机地址" />
          </el-form-item>
        </el-col>

        <!-- 第二行：端口 + 用户名 -->
        <el-col :span="12">
          <el-form-item label="端口" prop="port">
            <el-input-number v-model="form.port" :min="1" :max="65535" controls-position="right" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="用户名" prop="user">
            <el-input v-model="form.user" placeholder="请输入用户名" />
          </el-form-item>
        </el-col>

        <!-- 第三行：密码 + 私钥路径 -->
        <el-col :span="12">
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              show-password
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="私钥路径" prop="privateKeyPath">
            <el-input
              v-model="form.privateKeyPath"
              placeholder="请输入私钥路径，如 D:/ssh/HK.pem"
            />
          </el-form-item>
        </el-col>

        <!-- 按钮行：单独占一整行 -->
        <el-col :span="24">
          <el-form-item>
            <el-button type="primary" @click="submitForm">提交</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import '@/views/connect/views.css'
import axios from 'axios'

const formRef = ref<FormInstance>()
const form = reactive({
  connectName: '',
  host: '',
  port: 22,
  user: '',
  password: '',
  privateKeyPath: '',
})
const rules: FormRules = {
  connectName: [{ required: true, message: '请输入连接名称', trigger: 'blur' }],
  host: [{ required: true, message: '请输入主机地址', trigger: 'blur' }],
  port: [
    { required: true, message: '请输入端口', trigger: 'blur' },
    { type: 'number', min: 1, max: 65535, message: '端口范围为 1-65535', trigger: 'change' },
  ],
  user: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: false, message: '请输入密码或私钥路径', trigger: 'blur' }],
  privateKeyPath: [{ required: false, message: '请输入密码或私钥路径', trigger: 'blur' }],
}

const submitForm = async () => {
  if (!formRef.value) return

  // 手动触发表单校验
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      // 验证通过，执行提交操作（如调用 API）
      console.log('提交的表单数据：', form)

      axios
        .post('http://localhost:8080/base/connect', form)
        .then((response) => {
          // response.data 是 JSON 对象，需 JSON.stringify，或取字段
          console.log('连接结果:', JSON.stringify(response.data))
          const res = response.data
          if (res.code === 200) {
            ElMessage.success(res.message || '连接成功')
          } else {
            ElMessage.error(res.message || '连接失败')
          }
          // 这里可以根据需要进行页面跳转或其他操作
        })
        .catch((error) => {
          console.error('连接失败:', error)
          ElMessage.error('连接失败: ' + (error?.message || '请求异常'))
        })
    } else {
      // 验证不通过
      console.log('表单校验失败', fields)
    }
  })
}

// 重置表单
const resetForm = () => {
  if (!formRef.value) return
  formRef.value.resetFields()
}
</script>

<style scoped>
.connect-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1f2937 0%, #374151 100%);
  padding: 20px;
  box-sizing: border-box;
}

.connect-title {
  color: #f9fafb;
  font-size: 40px;
  font-weight: 700;
  letter-spacing: 2px;
  margin-bottom: 32px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.connect-form {
  width: 100%;
  max-width: 640px;
  background: #ffffff;
  border-radius: 12px;
  padding: 32px 28px 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
}

.connect-form :deep(.el-form-item__label) {
  color: #374151;
  font-weight: 600;
}

.connect-form .el-button {
  min-width: 96px;
}
</style>
