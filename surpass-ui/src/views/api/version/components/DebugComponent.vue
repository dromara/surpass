<template>
  <div class="debug-component">
    <!-- API信息 -->
    <div class="api-info" v-if="apiInfo && versionInfo">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="API名称">
          {{ apiInfo.name }}
        </el-descriptions-item>
        <el-descriptions-item label="路径">
          <el-tag>{{ apiInfo.path }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="方法">
          <el-tag :type="getMethodTagType(apiInfo.method)">
            {{ apiInfo.method }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="当前版本">
          <el-tag type="success">v{{ versionInfo.version }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="所属应用">
          <el-tag>{{ apiInfo.belongApp }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="应用上下文路径">
          <el-tag>{{ apiInfo.contextPath }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <!-- SQL模板预览 -->
    <div class="sql-preview" v-if="versionInfo">
      <div class="code-block">
        <pre><code class="sql">{{ versionInfo.sqlTemplate }}</code></pre>
      </div>
    </div>

    <!-- 请求配置 -->
    <div class="request-config" v-if="versionInfo">
      <el-card class="config-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>请求参数</span>
            <el-button type="primary" size="small" @click="addParam">
              添加参数
            </el-button>
          </div>
        </template>

        <!-- 参数配置 -->
        <div class="params-section">
          <div class="params-list">
            <div
                v-for="(param, index) in requestParams"
                :key="index"
                class="param-item"
                :class="{ 'param-error': param.error }"
            >
              <el-input
                  v-model="param.name"
                  placeholder="参数名"
                  style="width: 150px"
                  readonly
              />
              <el-select
                  v-model="param.type"
                  placeholder="类型"
                  style="width: 250px"
                  :disabled="param.readOnly"
              >
                <template v-for="(type, index) in paramInfoList" :key="index">
                  <el-option :label="type.label" :value="type.value"></el-option>
                </template>
              </el-select>
              <el-input
                  v-if="!param.type.startsWith('Array')"
                  v-model="param.value"
                  placeholder="参数值"
                  @input="validateParam(param)"
                  :class="{ 'invalid-param': param.error }"
              />
              <div v-else class="array-params">
                <el-button icon="plus" type="primary" @click="param.value.push('')"></el-button>
                <template v-for="(item, index) in param.value" :key="index">
                  <el-input
                      v-model="param.value[index]"
                      :placeholder="'参数['+index+']值'"
                      style="width: 200px;"
                      @input="validateParam(param)"
                      :class="{ 'invalid-param': param.error }"
                  >
                    <template #append>
                      <el-button slot="append" icon="Delete" size="small"
                                 @click="param.value.splice(index, 1)"></el-button>
                    </template>
                  </el-input>
                </template>
                <el-button v-if="param.value.length > 0" icon="DeleteFilled" type="danger" size="small"
                           @click="param.value.length = 0">清空
                </el-button>
              </div>
              <div class="param-info">
                <el-tooltip
                    :content="getRuleDisplayText(param.rules)"
                    placement="top"
                >
                  <el-icon :color="param.rules.required ? '#f56c6c' : '#909399'">
                    <InfoFilled/>
                  </el-icon>
                </el-tooltip>
              </div>
              <div class="param-status">
                <el-icon v-if="param.error" color="#f56c6c">
                  <CircleClose/>
                </el-icon>
                <el-icon v-else-if="param.value || !param.required" color="#67c23a">
                  <Success/>
                </el-icon>
              </div>
            </div>
          </div>
          <div class="validation-info" v-if="hasValidationErrors">
            <el-alert
                title="参数验证失败，请检查标红的参数"
                type="error"
                show-icon
                :closable="false"
            />
          </div>
        </div>

        <!-- 执行按钮 -->
        <div class="execute-section">
          <el-button
              type="primary"
              @click="executeApi"
              :loading="executing"
              :disabled="!apiInfo"
              size="large"
          >
            <el-icon>
              <Promotion/>
            </el-icon>
            执行API
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 响应结果 -->
    <div class="response-result" v-if="responseData">
      <el-card class="response-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>响应结果</span>
            <el-tag :type="responseData.code === 0 ? 'success' : 'danger'">
              {{ responseData.code === 0 ? '成功' : '失败' }}
            </el-tag>
          </div>
        </template>

        <div class="response-info">
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="状态码">
              {{ responseData.code }}
            </el-descriptions-item>
            <el-descriptions-item label="执行时间">
              {{ executionTime }}ms
            </el-descriptions-item>
            <el-descriptions-item label="消息">
              {{ responseData.message }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="response-data">
          <h4>响应数据：</h4>
          <div class="code-block">
            <pre><code class="json">{{ formatResponseData(responseData) }}</code></pre>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 空状态 -->
    <div v-if="!apiInfo" class="no-api-selected">
      <el-empty description="API信息加载中..." :image-size="200"/>
    </div>
  </div>
</template>

<script setup>
import {ref, computed, watch} from 'vue'
import {ElMessage, ElNotification} from 'element-plus'
import {gatewayApi} from '@/api/api-service/gatewayApi.ts'
import {apiParamTypeList} from "@/utils/enums/ApiContants.ts";

// 定义props
const props = defineProps({
  apiInfo: {
    type: Object,
    default: () => null
  },
  versionInfo: {
    type: Object,
    default: () => null
  },
  paramDefinition: {
    type: String,
    default: ''
  }
})

// 响应式数据
const executing = ref(false)
const responseData = ref(null)
const executionTime = ref(0)

const requestParams = ref([])
const paramInfoList = ref([...apiParamTypeList])

// 计算属性
const hasValidationErrors = computed(() => {
  return requestParams.value.some(param => param.error)
})
const addParam = () => {
  requestParams.value.push({
    name: '',
    value: '',
    type: 'String',
    required: false,
    rules: {},
    error: false,
    errorMessage: '',
    readOnly: false,
  })
}
// 方法
const initRequestParams = (paramDefinition) => {
  if (!paramDefinition) {
    requestParams.value = []
    return
  }

  try {
    const params = JSON.parse(paramDefinition)
    if (Array.isArray(params)) {
      requestParams.value = params.map(param => {
        const type = param.type || 'String'
        return {
          name: param.name || '',
          value: type.startsWith('Array[') ? [] : '',
          type: param.type || 'String',
          required: param.required || false,
          rules: param.rules || {},
          error: false,
          errorMessage: '',
          readOnly: true
        }
      })
    } else if (typeof params === 'object') {
      requestParams.value = Object.keys(params).map(key => {
        const type = param.type || 'String'
        return {
          name: key,
          value: type.startsWith('Array[') ? [] : '',
          type: params[key].type || 'String',
          required: params[key].required || false,
          rules: params[key].rules || {},
          error: false,
          errorMessage: '',
          readOnly: true
        }
      })
    }
  } catch (error) {
    console.error('解析参数定义失败:', error)
    requestParams.value = []
  }
}

// 验证单个参数
const validateParam = (param) => {
  console.log('验证参数:', param)

  // 重置错误状态
  param.error = false
  param.errorMessage = ''

  // 必填验证
  if (param.rules.required && !param.value) {
    param.error = true
    param.errorMessage = '该参数为必填项'
    return
  }

  // 如果参数为空且非必填，则跳过其他验证
  if (!param.value) {
    return
  }

  // 类型验证和规则验证
  switch (param.type) {
    case 'Integer':
    case 'Long':
    case 'Float':
    case 'Double':
      // 数字类型验证
      if (isNaN(Number(param.value))) {
        param.error = true
        param.errorMessage = '请输入有效的数字'
        return
      }
      const numValue = Number(param.value)
      // 最小值验证
      if (param.rules.minValue !== undefined && numValue < param.rules.minValue) {
        param.error = true
        param.errorMessage = `数值不能小于 ${param.rules.minValue}`
        return
      }

      // 最大值验证
      if (param.rules.maxValue !== undefined && numValue > param.rules.maxValue) {
        param.error = true
        param.errorMessage = `数值不能大于 ${param.rules.maxValue}`
        return
      }

      // 正则表达式验证
      if (param.rules.pattern && !validatePattern(param)) {
        return
      }
      break
    case 'String':
      // 字符串长度验证
      if (param.rules.minLength !== undefined && param.value.length < param.rules.minLength) {
        param.error = true
        param.errorMessage = `字符串长度不能少于 ${param.rules.minLength} 个字符`
        return
      }

      if (param.rules.maxLength !== undefined && param.value.length > param.rules.maxLength) {
        param.error = true
        param.errorMessage = `字符串长度不能超过 ${param.rules.maxLength} 个字符`
        return
      }

      // 正则表达式验证
      if (param.rules.pattern && !validatePattern(param)) {
        return
      }

      // 枚举值验证
      if (param.rules.enumValues && Array.isArray(param.rules.enumValues)) {
        if (!param.rules.enumValues.includes(param.value)) {
          param.error = true
          param.errorMessage = `只能输入以下值之一: ${param.rules.enumValues.join(', ')}`
          return
        }
      }
      break
    case 'Boolean':
      // 布尔类型验证
      const validBooleanValues = ['true', 'false', '1', '0']
      if (!validBooleanValues.includes(param.value.toLowerCase())) {
        param.error = true
        param.errorMessage = '布尔值只能是 true/false 或 1/0'
        return
      }
      break
  }
}

const validatePattern = (param) => {
  try {
    const regExp = new RegExp(param.rules.pattern)
    if (!regExp.test(param.value)) {
      param.error = true
      param.errorMessage = '输入格式不符合要求'
      if (param.rules.format) {
        const formatMessages = {
          'email': '请输入有效的邮箱地址',
          'phone': '请输入有效的手机号码',
          'url': '请输入有效的URL',
          'date': '请输入有效的日期格式',
          'time': '请输入有效的时间格式',
          'ipv4': '请输入有效的IPv4地址',
          'ipv6': '请输入有效的IPv6地址'
        }
        param.errorMessage = formatMessages[param.rules.format] || '输入格式不符合要求'
      }
      return false
    }
  } catch (e) {
    console.error('正则表达式错误:', e)
  }
  return true
}


// 获取规则显示文本
const getRuleDisplayText = (rulesObj) => {
  if (!rulesObj || Object.keys(rulesObj).length === 0) return '无要求'

  try {
    const descriptions = []

    if (rulesObj.required) descriptions.push('必填')
    if (rulesObj.minLength !== undefined) descriptions.push(`最短${rulesObj.minLength}字符`)
    if (rulesObj.maxLength !== undefined) descriptions.push(`最长${rulesObj.maxLength}字符`)
    if (rulesObj.pattern) descriptions.push('正则匹配')
    if (rulesObj.minValue !== undefined) descriptions.push(`最小值${rulesObj.minValue}`)
    if (rulesObj.maxValue !== undefined) descriptions.push(`最大值${rulesObj.maxValue}`)
    if (rulesObj.enumValues) descriptions.push(`枚举值(${rulesObj.enumValues.length}个)`)
    if (rulesObj.format) descriptions.push(`${rulesObj.format}格式`)

    return descriptions.join(', ') || '已配置'
  } catch (e) {
    return '规则格式错误'
  }
}

const executeApi = async () => {
  if (!props.apiInfo) {
    ElMessage.warning('API信息不存在')
    return
  }

  // 验证所有参数
  requestParams.value.forEach(param => validateParam(param))

  // 检查是否有验证错误
  const hasErrors = requestParams.value.some(param => param.error)
  const missingRequiredParams = requestParams.value
      .filter(param => param.required && !param.value)
      .map(param => param.name)

  if (hasErrors || missingRequiredParams.length > 0) {
    if (missingRequiredParams.length > 0) {
      ElMessage.warning(`请填写必填参数: ${missingRequiredParams.join(', ')}`)
    } else {
      ElMessage.warning('参数验证失败，请检查标红的参数')
    }
    return
  }

  let startTime = 0

  try {
    executing.value = true
    executionTime.value = 0

    // 构建请求参数
    const params = {}
    requestParams.value.forEach(param => {
      if (param.name && param.value !== '') {
        // 根据类型转换参数值
        let value = param.value
        if (param.type === 'number') {
          value = Number(value)
        } else if (param.type === 'boolean') {
          value = value.toLowerCase() === 'true' || value === '1'
        }
        params[param.name] = value
      }
    })

    // 记录开始时间
    startTime = Date.now()

    // 执行API
    console.log(props.versionInfo)
    const response = await gatewayApi.executeTest(
        props.apiInfo.path,
        props.apiInfo.method,
        props.apiInfo.contextPath,
        {
          "apiVersion": JSON.stringify(props.versionInfo),
          "data": params
        }
    )

    // 计算执行时间
    executionTime.value = Date.now() - startTime

    responseData.value = response

    if (response.code === 0) {
      ElNotification({
        title: '执行成功',
        message: `API执行成功 (${executionTime.value}ms)`,
        type: 'success',
        duration: 3000
      })
    } else {
      ElNotification({
        title: '执行失败',
        message: `API执行失败: ${response.message}`,
        type: 'error',
        duration: 3000
      })
    }

  } catch (error) {
    executionTime.value = startTime ? Date.now() - startTime : 0
    ElMessage.error('API执行失败')
    console.error('API执行失败:', error)
    responseData.value = {
      code: 1,
      message: error.message || 'API执行失败',
      data: null
    }
  } finally {
    executing.value = false
  }
}

const formatResponseData = (data) => {
  if (typeof data === 'string') {
    try {
      return JSON.stringify(JSON.parse(data), null, 2)
    } catch {
      return data
    }
  }
  return JSON.stringify(data, null, 2)
}

const getMethodTagType = (method) => {
  const types = {
    'GET': 'success',
    'POST': 'primary',
    'PUT': 'warning',
    'DELETE': 'danger',
    'PATCH': 'info'
  }
  return types[method] || 'info'
}

// 监听参数定义变化
watch(() => props.paramDefinition, (newVal) => {
  initRequestParams(newVal)
}, {immediate: true})

// 监听版本信息变化
watch(() => props.versionInfo, (newVal) => {
  if (newVal && newVal.paramDefinition) {
    initRequestParams(newVal.paramDefinition)
  }
}, {immediate: true})

</script>

<style scoped>
/* 主要内容区域 */
.debug-component {
  margin: 0 auto;
}

.api-info, .sql-preview {
  margin-bottom: 16px;
}

.sql-preview {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  overflow: hidden;
}

/* 卡片样式 */
.info-card,
.template-card,
.config-card,
.response-card {
  margin-bottom: 24px;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #303133;
}

/* 代码块样式 */
.code-block {

}

.code-block pre {
  margin: 0;
  padding: 16px;
  font-family: 'JetBrains Mono', 'Fira Code', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.5;
  color: #24292e;
  background: transparent;
  overflow-x: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

.code-block code.sql {
  color: #d73a49;
}

.code-block code.json {
  color: #032f62;
}

/* 参数配置样式 */
.params-section {
  margin-bottom: 20px;
}

.params-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
}

.params-list {
  margin-bottom: 12px;
}

.param-item {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  align-items: center;
  padding: 8px;
  border-radius: 4px;
  transition: all 0.3s;
}

.param-item:hover {
  background-color: #f5f7fa;
}

.param-item.param-error {
  background-color: #fef0f0;
  border: 1px solid #fde2e2;
}

.param-item .array-params {
  display: flex;
  align-items: center;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 4px;
  width: 100%;
}

.param-info {
  display: flex;
  gap: 4px;
}

.param-status {
  width: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.invalid-param :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #f56c6c inset !important;
}

.validation-info {
  margin-top: 12px;
}

.execute-section {
  text-align: center;
  padding: 20px 0;
}

/* 响应信息样式 */
.response-info {
  margin-bottom: 16px;
}

.response-data h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

/* 空状态 */
.no-api-selected {
  padding: 80px 0;
  text-align: center;
}

</style>