import React, { useState, useMemo } from 'react';

const API_BASE = '/data-exportor';

/**
 * 从单条 SQL 中提取表名（FROM 后的第一个标识符）
 */
function extractTableName(sql: string): string {
  // 去除尾部分号
  let s = sql.trim().replace(/;+$/, '').trim();

  // 找到 FROM 关键字位置（不区分大小写，确保是独立单词）
  const fromMatch = /\bFROM\s+/i.exec(s);
  if (!fromMatch) return 'Sheet';

  const afterFrom = s.substring(fromMatch.index + fromMatch[0].length);

  // 匹配表名：schema.table 或 table 或 "table"（引号内）
  const identMatch = /^["\[]?([a-zA-Z_][\w]*|[a-zA-Z_][\w]*\.[a-zA-Z_][\w]*)["\]]?/.exec(afterFrom);
  if (!identMatch) return 'Sheet';

  // 排除 SQL 关键字
  const kw = identMatch[1].toUpperCase();
  const keywords = new Set([
    'SELECT', 'WHERE', 'JOIN', 'INNER', 'LEFT', 'RIGHT', 'OUTER', 'CROSS',
    'ORDER', 'GROUP', 'HAVING', 'LIMIT', 'OFFSET', 'UNION', 'ON', 'AS',
    'AND', 'OR', 'NOT', 'IN', 'EXISTS', 'BETWEEN', 'LIKE', 'IS', 'NULL',
  ]);
  if (keywords.has(kw)) return 'Sheet';

  return identMatch[1];
}

/**
 * 解析多条 SQL（分号分隔）
 */
function parseSqlEntries(raw: string): { sql: string; sheetName: string }[] {
  const rawSqls = raw
    .split(';')
    .map(s => s.trim())
    .filter(s => s.length > 0);

  // 去重表名
  const usedNames = new Map<string, number>();

  return rawSqls.map((sql) => {
    let base = extractTableName(sql);
    if (base === 'Sheet') {
      base = 'Sheet';
    }
    const count = usedNames.get(base) || 0;
    usedNames.set(base, count + 1);
    const sheetName = count === 0 ? base : `${base}_${count + 1}`;
    return { sql, sheetName };
  });
}

const DataExportPage: React.FC = () => {
  const [rawSql, setRawSql] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const parsed = useMemo(() => parseSqlEntries(rawSql), [rawSql]);

  const handleExport = async () => {
    if (parsed.length === 0) {
      setMessage('请输入 SQL 查询语句');
      return;
    }

    setLoading(true);
    setMessage('正在导出...');

    try {
      const sqlEntries = parsed.map(e => ({
        sql: e.sql,
        sheetName: e.sheetName,
      }));

      const response = await fetch(`${API_BASE}/export/execute`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sqlEntries }),
      });

      if (!response.ok) {
        const contentType = response.headers.get('content-type') || '';
        if (contentType.includes('application/json')) {
          const err = await response.json();
          throw new Error(err.msg || err.message || '导出失败');
        }
        throw new Error('导出失败');
      }

      const blob = await response.blob();
      if (blob.size === 0) {
        const text = await response.text();
        try {
          const err = JSON.parse(text);
          throw new Error(err.msg || err.message || '导出失败');
        } catch {
          throw new Error(text || '导出返回空文件');
        }
      }

      setMessage('导出完成');
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `export_data_${Date.now()}.xlsx`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    } catch (err: any) {
      setMessage(`导出失败: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: 24, maxWidth: 960, margin: '0 auto' }}>
      <h2>数据导出</h2>
      <p style={{ color: '#888', marginBottom: 16, fontSize: 13 }}>
        支持多条SQL，用分号（;）分隔。Sheet名称自动从 FROM 子句的表名提取。
      </p>

      <div style={{ marginBottom: 16 }}>
        <label style={{ display: 'block', marginBottom: 8, fontWeight: 'bold' }}>
          SQL 查询语句 <span style={{ color: 'red' }}>*</span>
        </label>
        <textarea
          value={rawSql}
          onChange={e => setRawSql(e.target.value)}
          placeholder={'SELECT * FROM openplat_api_config ORDER BY id;\nSELECT * FROM openplat_system ORDER BY id;'}
          rows={8}
          style={{
            width: '100%',
            padding: 10,
            border: '1px solid #d9d9d9',
            borderRadius: 4,
            fontFamily: 'Consolas, Monaco, monospace',
            fontSize: 14,
            lineHeight: 1.6,
            boxSizing: 'border-box',
            resize: 'vertical',
          }}
        />
      </div>

      {parsed.length > 0 && (
        <div
          style={{
            marginBottom: 16,
            border: '1px solid #e8e8e8',
            borderRadius: 4,
            overflow: 'hidden',
          }}
        >
          <div
            style={{
              backgroundColor: '#fafafa',
              padding: '8px 12px',
              fontSize: 13,
              fontWeight: 'bold',
              borderBottom: '1px solid #e8e8e8',
              color: '#555',
            }}
          >
            识别结果（{parsed.length} 条SQL，将生成 {parsed.length} 个Sheet）
          </div>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 13 }}>
            <thead>
              <tr style={{ backgroundColor: '#f5f5f5' }}>
                <th style={{ padding: '6px 12px', textAlign: 'left', width: 40 }}>#</th>
                <th style={{ padding: '6px 12px', textAlign: 'left' }}>SQL（截断）</th>
                <th style={{ padding: '6px 12px', textAlign: 'left', width: 180 }}>Sheet名称</th>
              </tr>
            </thead>
            <tbody>
              {parsed.map((entry, idx) => (
                <tr key={idx} style={{ borderBottom: '1px solid #f0f0f0' }}>
                  <td style={{ padding: '6px 12px', color: '#888' }}>{idx + 1}</td>
                  <td style={{ padding: '6px 12px', fontFamily: 'monospace', maxWidth: 0, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                    {entry.sql.length > 60 ? entry.sql.substring(0, 57) + '...' : entry.sql}
                  </td>
                  <td style={{ padding: '6px 12px', color: '#1890ff', fontWeight: 'bold' }}>
                    {entry.sheetName}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div style={{ marginBottom: 16 }}>
        <button
          onClick={handleExport}
          disabled={loading || parsed.length === 0}
          style={{
            padding: '10px 28px',
            backgroundColor: loading || parsed.length === 0 ? '#b0b0b0' : '#1890ff',
            color: '#fff',
            border: 'none',
            borderRadius: 4,
            fontSize: 15,
            cursor: loading || parsed.length === 0 ? 'not-allowed' : 'pointer',
          }}
        >
          {loading ? '导出中...' : '执行导出'}
        </button>
      </div>

      {message && (
        <div
          style={{
            padding: '10px 16px',
            backgroundColor: message.startsWith('导出失败') || message.includes('失败')
              ? '#fff2f0'
              : '#f6ffed',
            border: `1px solid ${
              message.startsWith('导出失败') || message.includes('失败') ? '#ffccc7' : '#b7eb8f'
            }`,
            borderRadius: 4,
            color: message.startsWith('导出失败') || message.includes('失败') ? '#cf1322' : '#389e0d',
            whiteSpace: 'pre-wrap',
          }}
        >
          {message}
        </div>
      )}
    </div>
  );
};

export default DataExportPage;
