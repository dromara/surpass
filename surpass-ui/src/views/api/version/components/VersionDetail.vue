<template>
  <el-dialog
      title="版本详情"
      :model-value="visible"
      width="900px"
      class="version-detail-dialog"
      @update:model-value="$emit('update:visible', $event)"
  >
    <div class="version-detail-content" v-if="currentVersion">
      <!-- 基本信息卡片 -->
      <el-card class="info-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="版本号">
            <span class="version-number">v{{ currentVersion.version }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTagType(currentVersion.status)" size="small">
              {{ getStatusText(currentVersion.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ currentVersion.createdDate || currentVersion.createTime }}
          </el-descriptions-item>
          <el-descriptions-item label="描述">
            {{ currentVersion.description || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="操作类型">
            <el-tag size="small">
              {{ getOperationTypeText(currentVersion.supportsPaging) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="分页大小" v-if="currentVersion.supportsPaging === '1'">
            {{ currentVersion.pageSizeMax }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- SQL模板卡片 -->
      <el-card class="template-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>SQL模板</span>
          </div>
        </template>
        <div class="code-block">
          <pre><code class="sql">{{ currentVersion.sqlTemplate }}</code></pre>
        </div>
      </el-card>

      <!-- 参数定义卡片 -->
      <el-card class="param-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>参数定义</span>
            <el-tag v-if="parsedParams && parsedParams.length > 0" type="info" size="small">
              {{ parsedParams.length }} 个参数
            </el-tag>
          </div>
        </template>
        <div v-if="parsedParams && parsedParams.length > 0">
          <el-table :data="parsedParams" border size="small" class="param-table">
            <el-table-column prop="name" label="参数名" width="120" align="center">
              <template #default="{ row }">
                <el-tag type="primary" size="small">{{ row.name }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="type" label="类型" align="center">
              <template #default="{ row }">
                <el-tag :type="getParamTypeTagType(row.type)" size="small">
                  {{ getParamTypeObj(row.type).label }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述">
              <template #default="{ row }">
                {{ row.description || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="required" label="必填" width="80" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.rules.required" type="danger" size="small">是</el-tag>
                <el-tag v-else type="info" size="small">否</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="rules" label="输入规则">
              <template #default="{ row }">
                {{ getRuleDisplayText(row.rules) || '无' }}
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-else class="empty-state">
          <el-empty description="无参数定义" :image-size="80"/>
        </div>
      </el-card>

      <!-- 响应模板卡片 -->
      <el-card class="response-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>响应模板</span>
            <el-tag v-if="currentVersion.responseTemplate" type="success" size="small">
              已配置
            </el-tag>
          </div>
        </template>
        <div v-if="currentVersion.responseTemplate">
          <div class="code-block">
            <pre><code class="json">{{ formatResponseTemplate(currentVersion.responseTemplate) }}</code></pre>
          </div>
          <div class="template-tips">
            <p><strong>模板说明：</strong></p>
            <p>• <code>#{data}</code> 占位符将被实际查询结果替换</p>
            <p>• 支持JSON格式的响应模板</p>
          </div>
        </div>
        <div v-else class="empty-state">
          <el-empty description="无响应模板" :image-size="80"/>
        </div>
      </el-card>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  currentVersion: {
    type: Object,
    default: null
  },
  paramInfoList: {
    type: Array,
    default: () => [
      {
        "value": "String",
        "label": "字符串(String)"
      },
      {
        "value": "Byte",
        "label": "字节(Byte)"
      },
      {
        "value": "Short",
        "label": "短整型(Short)"
      },
      {
        "value": "Integer",
        "label": "整数(Integer)"
      },
      {
        "value": "Long",
        "label": "长整数(Long)"
      },
      {
        "value": "Float",
        "label": "单精度浮点(Float)"
      },
      {
        "value": "Double",
        "label": "双精度浮点(Double)"
      },
      {
        "value": "Boolean",
        "label": "布尔(Boolean)"
      },
      {
        "value": "Array[Integer]",
        "label": "整型数组(Array[Integer])"
      },
      {
        "value": "Array[String]",
        "label": "字符数组(Array[String])"
      }
    ]
  }
})

const emit = defineEmits(['update:visible'])

const visible = computed({
  get() {
    return props.visible
  },
  set(value) {
    emit('update:visible', value)
  }
})

const parsedParams = computed(() => {
  if (!props.currentVersion || !props.currentVersion.paramDefinition) {
    return []
  }

  try {
    return JSON.parse(props.currentVersion.paramDefinition)
  } catch (error) {
    console.error('解析参数定义失败:', error)
    return []
  }
})

const getStatusTagType = (status) => {
  const types = {
    0: 'info',    // 草稿
    1: 'warning', // 待发布
    2: 'success', // 已发布
    3: 'danger'   // 下线
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '草稿',
    1: '待发布',
    2: '已发布',
    3: '下线'
  }
  return texts[status] || '未知'
}

const getOperationTypeText = (type) => {
  const texts = {
    '1': '分页查询',
    '2': '列表查询',
    '3': '单个查询',
    '4': '增加',
    '5': '修改',
    '6': '删除'
  }
  return texts[type] || '未知类型'
}

const getParamTypeTagType = (type) => {
  const types = {
    'String': 'success',
    'Integer': 'warning',
    'Boolean': 'danger',
    'Array[Integer]': 'info',
    'Array[String]': 'primary'
  }
  return types[type] || 'info'
}

const getParamTypeObj = (type) => {
  return props.paramInfoList.find(item => item.value === type) || {label: type, value: type}
}

const getRuleDisplayText = (rulesObj) => {
  if (!rulesObj || Object.keys(rulesObj).length === 0) return ''

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

const formatResponseTemplate = (template) => {
  if (!template) return ''

  try {
    // 尝试解析为JSON进行格式化
    const jsonStr = template.replace(/#\{data\}/g, '"#{data}"')
    const parsed = JSON.parse(jsonStr)
    return JSON.stringify(parsed, null, 2).replace(/"#\{data\}"/g, '#{data}')
  } catch (error) {
    // 如果不是有效的JSON，直接返回原内容
    return template
  }
}
</script>

<style scoped lang="scss">
.version-detail-dialog .el-dialog__body {
  padding: 0;
}

.version-detail-content {
  padding: 20px;
  max-height: 70vh;
  overflow-y: auto;
}

.version-detail-content .el-card {
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
}

.version-detail-content .el-card:last-child {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #303133;
}

.version-number {
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
}

.code-block {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  overflow: hidden;
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

.code-block code.json .data-placeholder {
  color: #e36209;
  font-weight: bold;
}

.param-table {
  margin-top: 0;
}

.param-table .el-table {
  border-radius: 6px;
}

.param-table .el-table th {
  background-color: #f8f9fa;
  font-weight: 600;
}

.empty-state {
  padding: 40px 0;
}

.response-card .template-tips {
  margin-top: 12px;
  padding: 12px;
  background: #f0f9ff;
  border: 1px solid #e1f5fe;
  border-radius: 6px;
  font-size: 12px;
  color: #666;
}

.response-card .template-tips p {
  margin: 4px 0;
}

.response-card .template-tips code {
  background: #e8f4fd;
  padding: 2px 6px;
  border-radius: 3px;
  color: #1890ff;
  font-family: 'Courier New', monospace;
  font-weight: 500;
}

.info-card .el-descriptions {
  margin-top: 0;
}

.info-card .el-descriptions__label {
  font-weight: 600;
  color: #606266;
}

.info-card .el-descriptions__content {
  color: #303133;
}
</style>