/**
 * 解析 SQL 模板参数：${}和#{}
 * @param sqlTemplate
 */
export const parseSqlParameters = (sqlTemplate: string): any[] => {
    if (!sqlTemplate) return [];

    const params: any[] = [];

    /** ---------------------------
     * 1. 解析 foreach 映射
     * item -> collection
     * --------------------------- */
    const foreachRegex =
        /<foreach[\s\S]*?item="([^"]+)"[\s\S]*?collection="([^"]+)"[\s\S]*?>/g;

    const foreachMap = new Map(); // item -> collection

    let match;

    while ((match = foreachRegex.exec(sqlTemplate)) !== null) {
        const item = match[1].trim();
        const collection = match[2].trim();

        foreachMap.set(item, collection);

        // 先把 collection 作为 Array 参数加入
        if (!params.some(p => p.name === collection)) {
            params.push({
                name: collection,
                type: 'Array[String]',
                rules: {},
                description: '',
                readOnly: false
            });
        }
    }

    /** ---------------------------
     * 2. 解析 #{...} / ${...}
     * --------------------------- */
    const paramRegex = /[#,$]\{([^}]+)\}/g;

    while ((match = paramRegex.exec(sqlTemplate)) !== null) {
        const paramName = match[1].trim();

        // 如果是 foreach 的 item，跳过
        if (foreachMap.has(paramName)) {
            continue;
        }

        // 已存在则跳过
        if (params.some(p => p.name === paramName)) {
            continue;
        }

        params.push({
            name: paramName,
            type: 'String',
            rules: {},
            description: '',
            readOnly: false
        });
    }

    /** ---------------------------
     * 3. 兜底解析 collection=""
     * 非 foreach 里的情况
     * --------------------------- */
    const collectionRegex = /collection="([^"]+)"/g;

    while ((match = collectionRegex.exec(sqlTemplate)) !== null) {
        const collection = match[1].trim();

        if (!params.some(p => p.name === collection)) {
            params.push({
                name: collection,
                type: 'Array<String>',
                rules: {},
                description: '',
                readOnly: false
            });
        }
    }

    return params;
}


// 检查SQL语句基本结构
export const checkSqlStructure = (sql: string): boolean => {
    // 检查是否存在基本的SQL语句结构
    // SELECT语句检查
    if (sql.includes('SELECT')) {
        if (!sql.includes('FROM')) {
            return false
        }
    }

    // INSERT语句检查
    if (sql.includes('INSERT')) {
        if (!sql.includes('INTO') || !sql.includes('(')) {
            return false
        }
    }

    // UPDATE语句检查
    if (sql.includes('UPDATE')) {
        if (!sql.includes('SET')) {
            return false
        }
    }

    // DELETE语句检查
    if (sql.includes('DELETE')) {
        if (!sql.includes('FROM')) {
            return false
        }
    }

    return true
}

interface ResponseParam {
    name: string;
    type: string;
    description: string;
    readOnly?: boolean;
}

/**
 * SQL 语句归一化
 * @param sql
 */
export const normalizeMybatisSql = (sql: string): string => {
    if (!sql) return '';
    let result = sql;
    /** ----------------------------
     * 1. 移除 XML 注释
     * ---------------------------- */
    result = result.replace(/<!--[\s\S]*?-->/g, ' ');
    /** ----------------------------
     * 2. 移除 <if>/<when> 条件标签
     * 保留内部 SQL
     * ---------------------------- */
    result = result.replace(
        /<(if|when|otherwise)[^>]*>/gi,
        ' '
    );
    result = result.replace(
        /<\/(if|when|otherwise)>/gi,
        ' '
    );
    /** ----------------------------
     * 3. 移除 choose
     * ---------------------------- */
    result = result.replace(/<\/?choose[^>]*>/gi, ' ');
    /** ----------------------------
     * 4. 移除 where/trim/set
     * ---------------------------- */
    result = result.replace(
        /<(where|trim|set)[^>]*>/gi,
        ' '
    );
    result = result.replace(
        /<\/(where|trim|set)>/gi,
        ' '
    );
    /** ----------------------------
     * 5. 处理 foreach（保留内容）
     * ---------------------------- */
    result = result.replace(
        /<foreach[^>]*>/gi,
        ' '
    );
    result = result.replace(
        /<\/foreach>/gi,
        ' '
    );
    /** ----------------------------
     * 6. 移除 bind
     * ---------------------------- */
    result = result.replace(/<bind[^>]*\/>/gi, ' ');
    /** ----------------------------
     * 7. 占位符归一化
     * ---------------------------- */
    result = result
        .replace(/#\{[^}]+\}/g, '?')
        .replace(/\$\{[^}]+\}/g, '?');
    /** ----------------------------
     * 8. OGNL 表达式清理（test=""）
     * ---------------------------- */
    result = result.replace(/test="[^"]*"/gi, ' ');
    /** ----------------------------
     * 9. 多余空白压缩
     * ---------------------------- */
    result = result
        .replace(/\s+/g, ' ')
        .trim();
    return result;
};


/**
 * 同步 responseList
 */
export function syncResponseParams(
    oldList: ResponseParam[],
    sqlFields: string[]
): ResponseParam[] {
    const result: ResponseParam[] = [];

    // 旧数据映射
    const oldMap = new Map<string, ResponseParam>();

    oldList.forEach(item => {
        oldMap.set(item.name, item);
    });

    /** ----------------------------
     * 1. 按 SQL 顺序生成新列表
     * ---------------------------- */
    for (const field of sqlFields) {
        const old = oldMap.get(field);

        if (old) {
            // 已存在：原样保留
            result.push({
                ...old
            });

            // 标记已使用
            oldMap.delete(field);
        } else {
            // 新字段：补默认
            result.push({
                name: field,
                type: 'String',
                description: '',
                readOnly: false
            });
        }
    }

    /** ----------------------------
     * 2. 处理遗留字段（SQL已无）
     * ---------------------------- */
    oldMap.forEach(item => {
        if (item.readOnly) {
            // 只保留只读字段
            result.push({
                ...item
            });
        }
    });

    return result;
}