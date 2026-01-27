/*
 * mybatis-sql-formatter MIT license
 */

import { format as  formatSql } from "sql-formatter";
/*******************************************************************************
 * @Author                : Robert Huang<56649783@qq.com>                      *
 * @CreatedDate           : 2026-01-20 21:34:38                                *
 * @LastEditors           : Robert Huang<56649783@qq.com>                      *
 * @LastEditDate          : 2026-01-20 21:36:14                                *
 * @CopyRight             : MerBleueAviation                                   *
 ******************************************************************************/

const TYPES = {
  ELEMENT: 'element',
  TEXT: 'text',
  CDATA: 'cdata',
  COMMENT: 'comment',
};

/**
 * SQL Operation Tags - these start a SQL context
 */
const SQL_ROOT_TAGS = ['select', 'insert', 'update', 'delete', 'sql'];



/**
 * Parse XML into a tree, preserving whitespace and structure
 */

function parseXmlRecursive(xml, startIndex) {
  const nodes = [];
  let i = startIndex;
  const len = xml.length;

  while (i < len) {
    const nextTag = xml.indexOf('<', i);

    // Text
    if (nextTag === -1) {
      const text = xml.substring(i);
      if (text) nodes.push({ type: TYPES.TEXT, content: text });
      i = len;
      break;
    }
    if (nextTag > i) {
      const text = xml.substring(i, nextTag);
      nodes.push({ type: TYPES.TEXT, content: text });
      i = nextTag;
    }

    // CDATA
    if (xml.substr(i, 9) === '<![CDATA[') {
      const end = xml.indexOf(']]>', i);
      if (end === -1) {
        nodes.push({ type: TYPES.TEXT, content: xml.substring(i) });
        i = len;
      } else {
        const content = xml.substring(i, end + 3);
        nodes.push({ type: TYPES.CDATA, content });
        i = end + 3;
      }
      continue;
    }

    // Comment
    if (xml.substr(i, 4) === '<!--') {
      const end = xml.indexOf('-->', i);
      if (end === -1) {
        nodes.push({ type: TYPES.TEXT, content: xml.substring(i) });
        i = len;
      } else {
        const content = xml.substring(i, end + 3);
        nodes.push({ type: TYPES.COMMENT, content });
        i = end + 3;
      }
      continue;
    }

    // Processing Instruction
    if (xml.substr(i, 2) === '<?') {
      const end = xml.indexOf('?>', i);
      if (end === -1) {
        nodes.push({ type: TYPES.TEXT, content: xml.substring(i) });
        i = len;
      } else {
        nodes.push({ type: TYPES.TEXT, content: xml.substring(i, end + 2) });
        i = end + 2;
      }
      continue;
    }

    // DOCTYPE
    if (xml.substr(i, 9).toUpperCase() === '<!DOCTYPE') {
       let end = xml.indexOf('>', i);
       // Check for internal subset []
       const openBracket = xml.indexOf('[', i);
       if (openBracket !== -1 && openBracket < end) {
           const closeBracket = xml.indexOf(']', openBracket);
           if (closeBracket !== -1) {
               end = xml.indexOf('>', closeBracket);
           }
       }

       if (end === -1) {
           nodes.push({ type: TYPES.TEXT, content: xml.substring(i) });
           i = len;
       } else {
           nodes.push({ type: TYPES.TEXT, content: xml.substring(i, end + 1) });
           i = end + 1;
       }
       continue;
    }

    // End Tag
    if (xml.substr(i, 2) === '</') {
      const end = xml.indexOf('>', i);
      if (end === -1) {
        // Error case
        nodes.push({ type: TYPES.TEXT, content: xml.substring(i) });
        i = len;
      } else {
        const tagContent = xml.substring(i + 2, end).trim();
        return { nodes, nextIndex: end + 1, endTag: tagContent };
      }
    }

    // Start Tag
    const end = findTagEnd(xml, i);
    if (end === -1) {
      nodes.push({ type: TYPES.TEXT, content: xml.substring(i) });
      i = len;
      break;
    }

    const tagFull = xml.substring(i + 1, end);
    const isSelfClosing = tagFull.trim().endsWith('/');
    const cleanTag = isSelfClosing ? tagFull.replace(/\/$/, '') : tagFull;

    // Better name extraction
    const match = cleanTag.match(/^\s*([a-zA-Z_:][-\w:.]*)/);
    const name = match ? match[1] : '';

    const openTagRaw = xml.substring(i, end + 1);

    if (isSelfClosing) {
      nodes.push({
        type: TYPES.ELEMENT,
        name: name,
        openTagRaw,
        children: [],
        selfClosing: true,
      });
      i = end + 1;
    } else {
      const result = parseXmlRecursive(xml, end + 1);

      nodes.push({
        type: TYPES.ELEMENT,
        name: name,
        openTagRaw,
        closeTagRaw: result.endTag ? `</${result.endTag}>` : '',
        children: result.nodes,
        selfClosing: false,
      });
      i = result.nextIndex;
    }
  }
  return { nodes, nextIndex: i };
}

function findTagEnd(xml, start) {
  let inQuote = null;
  const len = xml.length;
  for (let i = start; i < len; i++) {
    const char = xml[i];
    if (inQuote) {
      if (char === inQuote) {
        inQuote = null;
      }
    } else {
      if (char === '"' || char === "'") {
        inQuote = char;
      } else if (char === '>') {
        return i;
      }
    }
  }
  return -1;
}

/**
 * Format the XML string
 */
export function format(xmlText) {
  const { nodes } = parseXmlRecursive(xmlText, 0);

  // Heuristic: If we have a <mapper> tag, or standard XML declaration, or SQL root tags, treat as XML.
  const hasRootTags = nodes.some(n =>
    n.type === TYPES.ELEMENT &&
    (n.name.toLowerCase() === 'mapper' || SQL_ROOT_TAGS.includes(n.name.toLowerCase()))
  );

  if (hasRootTags || xmlText.trim().startsWith('<?xml')) {
      return formatXmlNodes(nodes, 0).trim();
  } else {
      // No root tags found, treat the whole content as a SQL block (e.g. selection formatting)
      return formatSqlBlock(nodes).trim();
  }
}

function formatXmlNodes(nodes, level) {
  const indent = '  '.repeat(level);
  let result = '';

  nodes.forEach(node => {
    if (node.type === TYPES.TEXT) {
      const trimmed = node.content.trim();
      if (trimmed) {
        result += indent + trimmed + '\n';
      }
    } else if (node.type === TYPES.CDATA) {
        result += indent + node.content + '\n';
    } else if (node.type === TYPES.COMMENT) {
        result += indent + node.content + '\n';
    } else if (node.type === TYPES.ELEMENT) {
      if (SQL_ROOT_TAGS.includes(node.name.toLowerCase())) {
        const innerSql = formatSqlBlock(node.children);

        // Indent the SQL content
        const sqlIndent = '  '.repeat(level + 1);

        let indentedContent = '';
        if (innerSql) {
             indentedContent = innerSql.split('\n').map(line => {
                 if (!line.trim()) return '';
                 return sqlIndent + line;
             }).join('\n');
        }

        if (node.selfClosing) {
            result += `${indent}${node.openTagRaw}\n`;
        } else {
            result += `${indent}${node.openTagRaw}\n${indentedContent}\n${indent}${node.closeTagRaw}\n`;
        }
      } else {
        // Standard XML Element
        if (node.selfClosing) {
            result += `${indent}${node.openTagRaw}\n`;
        } else {
            const childrenResult = formatXmlNodes(node.children, level + 1);
            result += `${indent}${node.openTagRaw}\n${childrenResult}${indent}${node.closeTagRaw}\n`;
        }
      }
    }
  });
  return result;
}

function formatSqlBlock(nodes) {
  // 1. Flatten children to a string with placeholders
  const placeholders = {};
  let placeholderIndex = 0;

  const sqlText = nodes.map(n => {
    if (n.type === TYPES.TEXT) return n.content;
    if (n.type === TYPES.CDATA) {
        const key = `_CDATA_${placeholderIndex++}_`;
        placeholders[key] = n.content;
        return key;
    }
    if (n.type === TYPES.COMMENT) {
        const key = `_XML_COMMENT_${placeholderIndex++}_`;
        placeholders[key] = n.content;
        return `/*${key}*/`;
    }
    if (n.type === TYPES.ELEMENT) {
      const key = `_MYBATIS_TAG_${placeholderIndex++}_`;
      placeholders[key] = n;
      return key;
    }
    return '';
  }).join('');

  // 2. Format the SQL
  let formattedSql = '';
  try {
    formattedSql = formatSql(sqlText, {
      language: 'sql',
      paramTypes: {
        custom: [
          { regex: String.raw`#\{[\s\S]*?\}` },
          { regex: String.raw`\$\{[\s\S]*?\}` }
        ]
      },
      tabWidth: 2,
      keywordCase: 'upper',
    });
  } catch (e) {
    console.error('SQL Formatting failed', e);
    formattedSql = sqlText;
  }

  // 3. Restore placeholders
  return restorePlaceholders(formattedSql, placeholders).trim();
}

/**
 * Improved restoration with indentation support
 */
function restorePlaceholders(formattedSql, placeholders, tabSize = 2) {
    const lines = formattedSql.split('\n');
    const resultLines = [];

    for (let line of lines) {
        if (!line.includes('_MYBATIS_TAG_') && !line.includes('_CDATA_') && !line.includes('_XML_COMMENT_')) {
            resultLines.push(line);
            continue;
        }

        let currentLine = line;

        const re = /(_MYBATIS_TAG_\d+_)|(_CDATA_\d+_)|(\/\*_XML_COMMENT_\d+_\*\/)/g;

        currentLine = currentLine.replace(re, (match, tagKey, cdataKey, commentKey) => {
             if (cdataKey) return placeholders[cdataKey];
             if (commentKey) {
                 const inner = commentKey.replace(/^\/\*|\*\/$/g, '');
                 return placeholders[inner];
             }

             if (tagKey) {
                 const node = placeholders[tagKey];
                 // Calculate indentation
                 const matchIndex = line.indexOf(match);
                 const prefix = line.substring(0, matchIndex);
                 const indentMatch = prefix.match(/^\s*/);
                 const baseIndent = indentMatch ? indentMatch[0] : '';

                 // Check if we need to force a newline for block-level tags
                 const BLOCK_TAGS = ['include', 'trim', 'where', 'set', 'foreach', 'if', 'choose', 'when', 'otherwise', 'bind'];
                 const isBlockTag = BLOCK_TAGS.includes(node.name.toLowerCase());
                 const hasPrecedingContent = prefix.trim().length > 0;

                 let prefixNewline = '';
                 if (isBlockTag && hasPrecedingContent) {
                     prefixNewline = '\n' + baseIndent;
                 }

                 const innerContent = formatSqlBlock(node.children);

                 if (!innerContent.trim()) {
                     if (node.selfClosing) return `${prefixNewline}${node.openTagRaw}`;
                     return `${prefixNewline}${node.openTagRaw}${node.closeTagRaw}`;
                 }

                 const innerIndent = baseIndent + ' '.repeat(tabSize);
                 const indentedInner = innerContent.split('\n').map((l) => {
                     if (!l.trim()) return l;
                     return innerIndent + l;
                 }).join('\n');

                 if (node.selfClosing) return `${prefixNewline}${node.openTagRaw}`;

                 return `${prefixNewline}${node.openTagRaw}\n${indentedInner}\n${baseIndent}${node.closeTagRaw}`;
             }
             return match;
        });

        resultLines.push(currentLine);
    }

    return resultLines.join('\n');
}