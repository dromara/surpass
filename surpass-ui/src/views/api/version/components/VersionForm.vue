<template>
  <el-drawer
      :title="dialogTitle"
      :model-value="drawerVisible"
      direction="rtl"
      size="60%"
      :before-close="handleDrawerClose"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      @update:model-value="$emit('update:visible', $event)"
  >
    <template #header>
      <h4>{{ dialogTitle }}</h4>
    </template>
    <div class="drawer-content">
      <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-width="100px"
      >
        <el-tabs v-model="activeTab" class="form-tabs">
          <el-tab-pane label="基础配置" name="basic">
            <div class="tab-content">
              <el-form-item label="版本号" prop="version">
                <el-input-number
                    :model-value="formData.version"
                    :min="1"
                    :max="999"
                    placeholder="请输入版本号"
                    @update:model-value="$emit('update:formData', { ...props.formData, version: $event })"
                />
              </el-form-item>
              <el-form-item label="操作类型" prop="supportsPaging">
                <el-radio-group
                    :model-value="formData.supportsPaging"
                    @update:model-value="$emit('update:formData', { ...props.formData, supportsPaging: $event })"
                    @change="handlePagingParams"
                >
                  <el-radio-button label="1">
                    分页
                  </el-radio-button>
                  <el-radio-button label="2">
                    列表
                  </el-radio-button>
                  <el-radio-button label="3">
                    单记录
                  </el-radio-button>
                  <el-radio-button label="4">
                    增加
                  </el-radio-button>
                  <el-radio-button label="5">
                    修改
                  </el-radio-button>
                  <el-radio-button label="6">
                    删除
                  </el-radio-button>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="分页大小" prop="pageSizeMax" v-if="formData.supportsPaging === '1'">
                <el-input-number
                    style="width: 200px;"
                    :model-value="formData.pageSizeMax"
                    :min="1"
                    :max="9999"
                    placeholder=""
                    controls-position="right"
                    @update:model-value="$emit('update:formData', { ...props.formData, pageSizeMax: $event })"
                />
                <div class="form-item-tip">限制每页最大记录数</div>
              </el-form-item>

              <el-form-item label="描述" prop="description">
                <el-input
                    :model-value="formData.description"
                    type="textarea"
                    :rows="2"
                    placeholder="请输入版本描述"
                    @update:model-value="$emit('update:formData', { ...props.formData, description: $event })"
                />
              </el-form-item>
            </div>
          </el-tab-pane>

          <el-tab-pane label="请求参数" name="request">
            <div class="tab-content">
              <el-form-item label="SQL模板" prop="sqlTemplate">
                <el-button type="warning" @click="formatSql">格式化</el-button>

                <el-button type="primary" text bg @click="tagSqlIf"> &lt;if&gt;</el-button>
                <el-button type="primary" text bg @click="tagSqlForeach"> &lt;foreach&gt;</el-button>
                <el-button type="primary" text bg @click="tagSqlChoose"> &lt;choose&gt;</el-button>
                <el-button type="primary" text bg @click="tagSqlTrim"> &lt;trim&gt;</el-button>
                <el-button type="primary" text bg @click="tagSqlWhere"> &lt;where&gt;</el-button>
                <el-button type="primary" text bg @click="tagSqlSet"> &lt;set&gt;</el-button>

                <code-mirror ref="sqlCodeEditor"
                             :lang="sql()"
                             style="width:100%;min-height:60px;"
                             class="template-code"
                             basic
                             wrap
                             v-model="formData.sqlTemplate"
                             @update:model-value="handleSqlTemplateChange"
                             placeholder="请输入SQL模板，支持命名参数如 #{name}"/>
              </el-form-item>
              <el-form-item label="请求参数定义" prop="paramDefinition">
                <div class="param-definition-container">
                  <div class="param-header">
                    <span></span>
                    <el-button type="primary" size="small" @click="addParam">
                      添加参数
                    </el-button>
                  </div>
                  <el-table :data="paramList" border style="width: 100%; margin-top: 10px;">
                    <el-table-column prop="name" label="参数名" width="180">
                      <template #default="{ row, $index }">
                        <el-input
                            :model-value="row.name"
                            placeholder="参数名"
                            :readonly="row.readOnly"
                            :class="{ 'read-only-param': row.readOnly, 'input-error': !row.name }"
                            @input="updateParam($index, 'name', $event)"
                        />
                        <el-text v-if="!row.name" type="warning" size="small">请输入参数名</el-text>
                      </template>
                    </el-table-column>
                    <el-table-column prop="type" label="类型" width="200">
                      <template #default="{ row, $index }">
                        <el-select
                            :model-value="row.type"
                            placeholder="选择类型"
                            :disabled="row.readOnly"
                            @update:model-value="updateParam($index, 'type', $event)"
                        >
                          <template v-for="(type, index) in paramInfoList" :key="index">
                            <el-option :label="type.label" :value="type.value"></el-option>
                          </template>
                        </el-select>
                      </template>
                    </el-table-column>
                    <el-table-column prop="rules" label="输入规则">
                      <template #default="{ row }">
                        <el-button
                            link
                            type="primary"
                            @click="openRuleConfig(row)"
                            :title="JSON.stringify(row.rules, null, 2)"
                            :disabled="row.readOnly"
                        >
                          {{ getRuleDisplayText(row.rules) || '配置规则' }}
                        </el-button>
                      </template>
                    </el-table-column>
                    <el-table-column prop="description" label="描述">
                      <template #default="{ row, $index }">
                        <el-input
                            :model-value="row.description"
                            placeholder="参数描述"
                            :readonly="row.readOnly"
                            @update:model-value="updateParam($index, 'description', $event)"
                        />
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" width="60" align="center">
                      <template #default="{ $index }">
                        <el-button
                            icon="Delete"
                            link
                            type="danger"
                            size="small"
                            @click="removeParam($index)"
                            :disabled="paramList[$index]?.readOnly"
                        ></el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </el-form-item>
            </div>
          </el-tab-pane>

          <el-tab-pane label="响应参数" name="response">
            <div class="tab-content">
              <el-form-item label="响应模板" prop="responseTemplate">
                <code-mirror :lang="json()" style="width:100%;min-height:60px;" class="template-code" basic
                             v-model="formData.responseTemplate"
                             placeholder="请输入响应模板，支持 #{data} 占位符代表结果数据"/>
                <div class="template-tips">
                  <p><strong>模板提示：</strong></p>
                  <p>• 使用 <code>#{data}</code> 占位符代表查询结果数据（必须包含）</p>
                  <p>• 示例：<code>{"code": 0, "message": "success", "data": #{data}}</code></p>
                  <p>• 支持JSON格式，系统会自动将查询结果替换到 #{data} 位置</p>
                </div>
              </el-form-item>
              <el-form-item label="响应参数定义" prop="responseDefinition">
                <div class="param-definition-container">
                  <div class="param-header">
                    <span></span>
                    <el-button type="primary" size="small" @click="addResponse">
                      添加响应参数
                    </el-button>
                  </div>
                  <el-table :data="responseList" border style="width: 100%; margin-top: 10px;">
                    <el-table-column prop="name" label="参数名" width="180">
                      <template #default="{ row, $index }">
                        <el-input
                            :model-value="row.name"
                            placeholder="响应参数名"
                            :class="{ 'input-error': !row.name }"
                            @input="updateResponse($index, 'name', $event)"
                        />
                        <el-text v-if="!row.name" type="warning" size="small">请输入参数名</el-text>
                      </template>
                    </el-table-column>
                    <el-table-column prop="type" label="类型" width="200">
                      <template #default="{ row, $index }">
                        <el-select
                            :model-value="row.type"
                            placeholder="选择类型"
                            @update:model-value="updateResponse($index, 'type', $event)"
                        >
                          <template v-for="(type, index) in paramInfoList" :key="index">
                            <el-option :label="type.label" :value="type.value"></el-option>
                          </template>
                        </el-select>
                      </template>
                    </el-table-column>
                    <el-table-column prop="description" label="描述">
                      <template #default="{ row, $index }">
                        <el-input
                            :model-value="row.description"
                            placeholder="参数描述"
                            @update:model-value="updateResponse($index, 'description', $event)"
                        />
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" width="60" align="center">
                      <template #default="{ $index }">
                        <el-button
                            icon="Delete"
                            link
                            type="danger"
                            size="small"
                            @click="removeResponse($index)"
                        ></el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </el-form-item>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-form>

      <div class="drawer-footer">
        <el-button @click="handleTest">测试</el-button>
        <el-button @click="handleDrawerClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          保存
        </el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import {ref, computed, defineProps, defineEmits} from 'vue'
import {ElMessage} from 'element-plus'
import {apiParamTypeList} from '@/utils/enums/ApiContants.ts'
import {json} from "@codemirror/lang-json";  //引入json语言支持
import {sql} from "@codemirror/lang-sql";
import CodeMirror from 'vue-codemirror6';
import {format} from '@/api/formatter.js'
import {Parser} from 'node-sql-parser';
import {parseSqlParameters, checkSqlStructure, syncResponseParams, normalizeMybatisSql} from "@/utils/SqlUtil.ts"

const parser = new Parser();
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  isEdit: {
    type: Boolean,
    default: false
  },
  formData: {
    type: Object,
    default: () => ({
      id: null,
      apiId: null,
      version: 1,
      sqlTemplate: '',
      paramDefinition: '',
      responseTemplate: '',
      description: '',
      supportsPaging: '1',
      pageSizeMax: 20,
      rateLimit: 0
    })
  },
  paramList: {
    type: Array,
    default: () => []
  },
  responseList: {
    type: Array,
    default: () => []
  },
  paramInfoList: {
    type: Array,
    default: () => [...apiParamTypeList]
  },
  submitting: {
    type: Boolean,
    default: false
  },
  codeMirrorCursor: 0
})

const emit = defineEmits([
  'update:visible',
  'update:formData',
  'update:paramList',
  'update:responseList',
  'close',
  'submit',
  'rule-config',
  'paging-params-change'
])

const formRef = ref()
const sqlCodeEditor = ref();
const activeTab = ref('basic')
const dialogTitle = computed(() => props.isEdit ? '编辑版本' : '新增版本')
const drawerVisible = computed({
  get() {
    return props.visible
  },
  set(value) {
    emit('update:visible', value)
  }
})

const validateSqlTemplate = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入SQL'))
    return
  }

  // 根据操作类型验证SQL语法
  const operationType = props.formData.supportsPaging
  const trimmedSql = value.trim().toUpperCase()

  // 验证SQL语法
  if (!isValidSqlSyntax(trimmedSql)) {
    callback(new Error('SQL语法不合法'))
    return
  }

  // 验证占位符参数名格式
  if (!validatePlaceholderParameters(value)) {
    callback(new Error('占位符参数名格式不正确，参数名必须以英文字母开头，可包含英文字母、数字、下划线'))
    return
  }

  // 根据操作类型验证SQL语句类型
  if (operationType === '1' || operationType === '2') { // 分页查询或列表查询
    if (!trimmedSql.startsWith('SELECT')) {
      callback(new Error('分页查询或列表查询操作类型必须使用SELECT语句'))
      return
    }
  } else if (operationType === '3') { // 单个查询
    if (!trimmedSql.startsWith('SELECT')) {
      callback(new Error('单个查询操作类型必须使用SELECT语句'))
      return
    }
  } else if (operationType === '4') { // 增加操作
    if (!trimmedSql.startsWith('INSERT')) {
      callback(new Error('增加操作类型必须使用INSERT语句'))
      return
    }
  } else if (operationType === '5') { // 修改操作
    if (!trimmedSql.startsWith('UPDATE')) {
      callback(new Error('修改操作类型必须使用UPDATE语句'))
      return
    }
  } else if (operationType === '6') { // 删除操作
    if (!trimmedSql.startsWith('DELETE')) {
      callback(new Error('删除操作类型必须使用DELETE语句'))
      return
    }
  }

  callback()
}

const isValidSqlSyntax = (sql) => {
  // 基础SQL语法验证
  // 检查是否有基本的SQL结构
  if (!sql || sql.length < 6) {
    return false
  }
  // 基本语法检查
  const sqlWithoutComments = removeSqlComments(sql)
  // 检查是否存在常见的SQL语法错误
  // 检查括号是否匹配
  if (!hasMatchingParentheses(sqlWithoutComments)) {
    return false
  }
  // 检查是否包含危险字符或结构
  if (containsDangerousSql(sqlWithoutComments)) {
    return false
  }
  // 检查SQL语句结构
  return checkSqlStructure(sqlWithoutComments);
}

const removeSqlComments = (sql) => {
  // 移除SQL注释
  let result = sql
  // 移除单行注释 --
  result = result.replace(/--.*$/gm, '')
  // 移除多行注释 /* */
  result = result.replace(/\/\*[\s\S]*?\*\//g, '')
  return result
}

const hasMatchingParentheses = (sql) => {
  let count = 0
  for (let i = 0; i < sql.length; i++) {
    if (sql[i] === '(') {
      count++
    } else if (sql[i] === ')') {
      count--
      if (count < 0) return false
    }
  }
  return count === 0
}

const containsDangerousSql = (sql) => {
  // 检查是否包含危险的SQL关键字
  const dangerousKeywords = ['DROP', 'TRUNCATE', 'ALTER', 'CREATE', 'DELETE FROM', 'UPDATE.*SET.*WHERE.*1=1', 'EXEC', 'EXECUTE']
  for (const keyword of dangerousKeywords) {
    const regex = new RegExp(keyword.replace(/[.*+?^${}()|\[\]\\]/g, '\\$&'), 'i');
    if (regex.test(sql)) {
      return true
    }
  }
  return false
}

// 验证占位符参数名格式
const validatePlaceholderParameters = (sql) => {
  // 使用正则表达式匹配 #{参数名} 格式的参数
  const regex = /#\{([^}]+)\}/g;
  let match;

  while ((match = regex.exec(sql)) !== null) {
    const paramName = match[1].trim();

    // 检查参数名格式：必须以英文字母开头，可包含英文字母、数字、下划线
    if (!isValidParameterName(paramName)) {
      return false;
    }
  }

  return true;
}

// 检查参数名格式是否有效
const isValidParameterName = (paramName) => {
  // 参数名必须以英文字母开头，可包含英文字母、数字、下划线
  const paramRegex = /^[a-zA-Z][a-zA-Z0-9_]*$/;
  return paramRegex.test(paramName);
}

const validateResponseTemplate = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入响应参数模板'))
    return
  }
  // 检查是否包含 #{data} 占位符
  if (!value.includes('#{data}')) {
    callback(new Error('响应模板必须包含 #{data} 占位符'))
    return
  }
  callback()
}

// 同步SQL模板参数到参数定义
const syncSqlParamsToParamList = (sqlTemplate) => {
  const sql = sqlTemplate || props.formData.sqlTemplate

  if (!sql) {
    // 如果SQL模板为空，清空参数列表（但保留只读参数）
    emit('update:paramList', props.paramList.filter(param => param.readOnly));
    return;
  }

  // 解析SQL模板中的参数
  const sqlParams = parseSqlParameters(sql);

  // 创建一个包含现有参数的映射，用于保留已有的参数配置
  const existingParamMap = {};
  props.paramList.forEach(param => {
    existingParamMap[param.name] = param;
  });

  // 合并参数列表：保留只读参数和已有的参数配置
  const newParamList = [];

  // 添加只读参数（分页参数等）
  props.paramList.filter(param => param.readOnly).forEach(param => {
    newParamList.push(param);
  });

  // 添加SQL模板中的参数
  sqlParams.forEach(sqlParam => {
    // 如果参数已存在，保留原有配置
    if (existingParamMap[sqlParam.name]) {
      newParamList.push(existingParamMap[sqlParam.name]);
    } else {
      // 如果是新参数，添加默认配置
      newParamList.push(sqlParam);
    }
  });

  emit('update:paramList', newParamList);

  // 解析响应参数
  const sqlNormalized = normalizeMybatisSql(sql);
  const astRes = parser.astify(sqlNormalized);
  const columns = (astRes.columns || [])
      .map(c => c.as || c.expr?.column)
      .filter(t => t && t !== '*');
  const newResponseList = syncResponseParams(
      props.responseList || [],
      columns
  );
  emit('update:responseList', newResponseList);
}

const formRules = {
  version: [
    {required: true, message: '请输入版本号', trigger: 'blur'}
  ],
  sqlTemplate: [
    {required: true, message: '请输入SQL', trigger: 'blur'},
    {validator: validateSqlTemplate, trigger: 'blur'},
  ],
  supportsPaging: [
    {required: true, message: '请选择操作类型', trigger: 'blur'}
  ],
  pageSizeMax: [
    {required: true, message: '请输入分页大小', trigger: 'blur'},
  ],
  responseTemplate: [
    {required: true, message: '请输入响应参数模板', trigger: 'blur'},
    {validator: validateResponseTemplate, trigger: 'blur'},
  ],
  responseDefinition: [
    // 不需要校验，留空
  ]
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

const handleTest = () => {

}

const handleDrawerClose = () => {
  emit('close')
}

const addParam = () => {
  const newParamList = [...props.paramList, {
    name: '',
    type: 'String',
    rules: {},
    description: ''
  }]
  console.log("newParamList ", newParamList);
  emit('update:paramList', newParamList)
}

const removeParam = (index) => {
  // 不允许删除分页参数
  if (props.paramList[index].readOnly) {
    ElMessage.warning('只读参数不能删除')
    return
  }
  const newParamList = [...props.paramList]
  newParamList.splice(index, 1)
  console.log("newParamList ", newParamList);
  emit('update:paramList', newParamList)
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    // 验证参数列表
    const invalidParams = props.paramList.filter(param =>
        !param.name?.trim() && !param.readOnly
    )
    if (invalidParams.length > 0) {
      ElMessage.error('请填写所有参数名')
      return
    }

    // 验证参数名重复性
    const nonReadOnlyParams = props.paramList.filter(param => !param.readOnly);
    const paramNames = nonReadOnlyParams.map(param => param.name?.trim());
    const uniqueNames = new Set(paramNames);
    if (uniqueNames.size !== nonReadOnlyParams.length) {
      ElMessage.error('参数名不能重复');
      return;
    }

    // 验证响应参数列表
    const invalidResponses = props.responseList.filter(response =>
        !response.name?.trim()
    )
    if (invalidResponses.length > 0) {
      ElMessage.error('请填写所有响应参数名')
      return
    }

    // 验证响应参数名重复性
    const responseNames = props.responseList.map(response => response.name?.trim());
    const uniqueResponseNames = new Set(responseNames);
    if (uniqueResponseNames.size !== props.responseList.length) {
      ElMessage.error('响应参数名不能重复');
      return;
    }

    emit('submit')
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

const updateParam = (index, field, value) => {
  const newParamList = [...props.paramList]
  newParamList[index] = {...newParamList[index], [field]: value}
  emit('update:paramList', newParamList)
}

const addResponse = () => {
  const newResponseList = [...props.responseList, {
    name: '',
    type: 'String',
    description: ''
  }]
  console.log("newResponseList ", newResponseList);
  emit('update:responseList', newResponseList)
}

const removeResponse = (index) => {
  const newResponseList = [...props.responseList]
  newResponseList.splice(index, 1)
  console.log("newResponseList ", newResponseList);
  emit('update:responseList', newResponseList)
}

const updateResponse = (index, field, value) => {
  const newResponseList = [...props.responseList]
  newResponseList[index] = {...newResponseList[index], [field]: value}
  emit('update:responseList', newResponseList)
}

const handleSqlTemplateChange = (value, viewUpdate) => {
  console.log("handleSqlTemplateChange" + value);
  // 更新表单数据
  emit('update:formData', {...props.formData, sqlTemplate: value})
  // 同步SQL模板参数到参数定义列表
  syncSqlParamsToParamList(value)
}

const openRuleConfig = (param) => {
  // 不允许编辑只读参数的规则
  if (param.readOnly) {
    ElMessage.warning('不能编辑只读参数的规则')
    return
  }
  emit('rule-config', param)
}

const handlePagingParams = () => {
  emit('paging-params-change')
}

const tagSqlIf = (value) => {
  var preSql = (props.formData.sqlTemplate + "").substring(0, sqlCodeEditor.value.getCursor());
  var suffixSql = (props.formData.sqlTemplate + "").substring(sqlCodeEditor.value.getCursor());
  var tagedSql = preSql + ' <if test="property != null"> </if>' + suffixSql;
  emit('update:formData', {...props.formData, sqlTemplate: tagedSql})
}

const tagSqlForeach = (value) => {
  var preSql = (props.formData.sqlTemplate + "").substring(0, sqlCodeEditor.value.getCursor());
  var suffixSql = (props.formData.sqlTemplate + "").substring(sqlCodeEditor.value.getCursor());
  var tagedSql = preSql + ' <foreach item="item" index="index" collection="list" open="(" separator="," close=")" nullable="true"> #{item} </foreach>' + suffixSql;
  emit('update:formData', {...props.formData, sqlTemplate: tagedSql})
}

const tagSqlSet = (value) => {
  var preSql = (props.formData.sqlTemplate + "").substring(0, sqlCodeEditor.value.getCursor());
  var suffixSql = (props.formData.sqlTemplate + "").substring(sqlCodeEditor.value.getCursor());
  var tagedSql = preSql + ' <set> </set>' + suffixSql;
  emit('update:formData', {...props.formData, sqlTemplate: tagedSql})
}

const tagSqlChoose = (value) => {
  var preSql = (props.formData.sqlTemplate + "").substring(0, sqlCodeEditor.value.getCursor());
  var suffixSql = (props.formData.sqlTemplate + "").substring(sqlCodeEditor.value.getCursor());
  var tagedSql = preSql + ' <choose> <when test="property != null"> </when> <otherwise> </otherwise> </choose>' + suffixSql;
  emit('update:formData', {...props.formData, sqlTemplate: tagedSql})
}
const tagSqlTrim = (value) => {
  var preSql = (props.formData.sqlTemplate + "").substring(0, sqlCodeEditor.value.getCursor());
  var suffixSql = (props.formData.sqlTemplate + "").substring(sqlCodeEditor.value.getCursor());
  var tagedSql = preSql + ' <trim prefix="WHERE | SET " prefixOverrides="AND |OR "> </trim>' + suffixSql;
  emit('update:formData', {...props.formData, sqlTemplate: tagedSql})
}

const tagSqlWhere = (value) => {
  var preSql = (props.formData.sqlTemplate + "").substring(0, sqlCodeEditor.value.getCursor());
  var suffixSql = (props.formData.sqlTemplate + "").substring(sqlCodeEditor.value.getCursor());
  var tagedSql = preSql + ' <where> </where>' + suffixSql;
  emit('update:formData', {...props.formData, sqlTemplate: tagedSql})
}


const formatSql = (value) => {
  var fSql = format(props.formData.sqlTemplate);
  console.log("format sqlTemplate " + fSql);
  emit('update:formData', {...props.formData, sqlTemplate: fSql})
}

</script>

<style scoped lang="scss">
.drawer-content {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.read-only-param :deep(.el-input__wrapper) {
  color: #909399 !important;
  background-color: #f5f7fa !important;
}

.read-only-param :deep(input:focus) {
  border-color: #dcdfe6 !important;
}

.input-error :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px rgba(255, 0, 0, 0.5) inset;
}

.drawer-footer {
  margin-top: auto;
  padding-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.param-definition-container {
  width: 100%;
}

.param-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.param-header span {
  font-weight: bold;
  color: #606266;
}

.template-tips {
  margin-top: 8px;
  padding: 12px;
  background: #f0f9ff;
  border: 1px solid #e1f5fe;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
}

.template-tips p {
  margin: 4px 0;
}

.template-tips code {
  background: #e8f4fd;
  padding: 2px 4px;
  border-radius: 2px;
  color: #1890ff;
  font-family: 'Courier New', monospace;
}

.template-code {
  margin-top: 8px;
  padding: 12px;
  border: 1px solid #e1f5fe;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
  width: 100%;
}

.template-code:focus {
  box-shadow: 0 0 0 1px #409eff inset;
  outline: none;
}

.template-code:hover {
  box-shadow: 0 0 0 1px #409eff inset;
  outline: none;
}

.form-item-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.form-tabs {
  :deep(.el-tabs__content) {
    padding: 20px 0;
  }
}

.tab-content {
  max-height: calc(100vh - 300px);
  overflow-y: auto;
  padding-right: 10px;
}
</style>