import React, { useState, useMemo } from 'react';

const API_BASE = '/data-exportor';

function extractTableName(sql: string): string {
  let s = sql.trim().replace(/;+$/, '').trim();
  const fromMatch = /\bFROM\s+/i.exec(s);
  if (!fromMatch) return 'Sheet';
  const afterFrom = s.substring(fromMatch.index + fromMatch[0].length);
  const identMatch = /^["\[]?([a-zA-Z_][\w]*|[a-zA-Z_][\w]*\.[a-zA-Z_][\w]*)["\]]?/.exec(afterFrom);
  if (!identMatch) return 'Sheet';
  const kw = identMatch[1].toUpperCase();
  const keywords = new Set([
    'SELECT', 'WHERE', 'JOIN', 'INNER', 'LEFT', 'RIGHT', 'OUTER', 'CROSS',
    'ORDER', 'GROUP', 'HAVING', 'LIMIT', 'OFFSET', 'UNION', 'ON', 'AS',
    'AND', 'OR', 'NOT', 'IN', 'EXISTS', 'BETWEEN', 'LIKE', 'IS', 'NULL',
  ]);
  if (keywords.has(kw)) return 'Sheet';
  return identMatch[1];
}

function parseSqlEntries(raw: string): { sql: string; sheetName: string }[] {
  const rawSqls = raw.split(';').map(s => s.trim()).filter(s => s.length > 0);
  const usedNames = new Map<string, number>();
  return rawSqls.map((sql) => {
    let base = extractTableName(sql);
    const count = usedNames.get(base) || 0;
    usedNames.set(base, count + 1);
    const sheetName = count === 0 ? base : `${base}_${count + 1}`;
    return { sql, sheetName };
  });
}

const DataExportPage: React.FC = () => {
  const [rawSql, setRawSql] = useState('');
  const [jdbcUrl, setJdbcUrl] = useState('jdbc:opengauss://localserver.gnosis:5432/gnosis_sample?prepareThreshold=0');
  const [username, setUsername] = useState('gaussdb');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const parsed = useMemo(() => parseSqlEntries(rawSql), [rawSql]);

  const handleExport = async () => {
    if (!jdbcUrl.trim()) { setMessage('请输入 JDBC地址'); return; }
    if (!username.trim()) { setMessage('请输入数据库用户名'); return; }
    if (parsed.length === 0) { setMessage('请输入SQL查询语句'); return; }

    setLoading(true);
    setMessage('正在导出...');

    try {
      const response = await fetch(`${API_BASE}/export/execute`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          jdbcUrl: jdbcUrl.trim(),
          username: username.trim(),
          password: password,
          sqlEntries: parsed.map(e => ({ sql: e.sql, sheetName: e.sheetName })),
        }),
      });

      if (!response.ok) {
        const ct = response.headers.get('content-type') || '';
        if (ct.includes('application/json')) {
          const err = await response.json();
          throw new Error(err.msg || err.message || '导出失败');
        }
        throw new Error('导出失败');
      }

      const blob = await response.blob();
      if (blob.size === 0) {
        const text = await response.text();
        try { const err = JSON.parse(text); throw new Error(err.msg || err.message || '导出失败'); }
        catch { throw new Error(text || '导出返回空文件'); }
      }

      setMessage('导出完成');
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `export_data_${Date.now()}.csv`;
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

  const inputStyle: React.CSSProperties = {
    width: '100%', padding: '8px 10px', border: '1px solid #d9d9d9',
    borderRadius: 4, fontSize: 14, boxSizing: 'border-box',
  };

  return (
    <div style={{ padding: 24, maxWidth: 960, margin: '0 auto' }}>
      <h2>数据导出</h2>
      <p style={{ color: '#888', marginBottom: 16, fontSize: 13 }}>
        支持多条SQL（分号分隔）。导出为CSV格式，逗号/引号/换行自动转义。
      </p>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 12, marginBottom: 16 }}>
        <div>
          <label style={{ display: 'block', marginBottom: 4, fontWeight: 'bold', fontSize: 13 }}>
            JDBC地址 <span style={{ color: 'red' }}>*</span>
          </label>
          <input value={jdbcUrl} onChange={e => setJdbcUrl(e.target.value)}
            placeholder="jdbc:opengauss://host:port/db" style={inputStyle} />
        </div>
        <div>
          <label style={{ display: 'block', marginBottom: 4, fontWeight: 'bold', fontSize: 13 }}>
            用户名 <span style={{ color: 'red' }}>*</span>
          </label>
          <input value={username} onChange={e => setUsername(e.target.value)}
            placeholder="数据库用户名" style={inputStyle} />
        </div>
        <div>
          <label style={{ display: 'block', marginBottom: 4, fontWeight: 'bold', fontSize: 13 }}>
            密码
          </label>
          <input value={password} onChange={e => setPassword(e.target.value)}
            type="password" placeholder="数据库密码" style={inputStyle} />
        </div>
      </div>

      <div style={{ marginBottom: 16 }}>
        <label style={{ display: 'block', marginBottom: 8, fontWeight: 'bold' }}>
          SQL 查询语句 <span style={{ color: 'red' }}>*</span>
        </label>
        <textarea
          value={rawSql}
          onChange={e => setRawSql(e.target.value)}
          placeholder={'SELECT * FROM openplat_api_config ORDER BY id;\nSELECT * FROM openplat_system ORDER BY id;'}
          rows={8}
          style={{ ...inputStyle, fontFamily: 'Consolas, Monaco, monospace', lineHeight: 1.6, resize: 'vertical' }}
        />
      </div>

      {parsed.length > 0 && (
        <div style={{ marginBottom: 16, border: '1px solid #e8e8e8', borderRadius: 4, overflow: 'hidden' }}>
          <div style={{ backgroundColor: '#fafafa', padding: '8px 12px', fontSize: 13, fontWeight: 'bold',
            borderBottom: '1px solid #e8e8e8', color: '#555' }}>
            {parsed.length} 条SQL
          </div>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 13 }}>
            <thead>
              <tr style={{ backgroundColor: '#f5f5f5' }}>
                <th style={{ padding: '6px 12px', textAlign: 'left', width: 40 }}>#</th>
                <th style={{ padding: '6px 12px', textAlign: 'left' }}>SQL（截断）</th>
              </tr>
            </thead>
            <tbody>
              {parsed.map((entry, idx) => (
                <tr key={idx} style={{ borderBottom: '1px solid #f0f0f0' }}>
                  <td style={{ padding: '6px 12px', color: '#888' }}>{idx + 1}</td>
                  <td style={{ padding: '6px 12px', fontFamily: 'monospace', maxWidth: 0,
                    overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                    {entry.sql.length > 80 ? entry.sql.substring(0, 77) + '...' : entry.sql}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div style={{ marginBottom: 16 }}>
        <button onClick={handleExport} disabled={loading || parsed.length === 0}
          style={{ padding: '10px 28px', backgroundColor: loading || parsed.length === 0 ? '#b0b0b0' : '#1890ff',
            color: '#fff', border: 'none', borderRadius: 4, fontSize: 15,
            cursor: loading || parsed.length === 0 ? 'not-allowed' : 'pointer' }}>
          {loading ? '导出中...' : '导出CSV'}
        </button>
      </div>

      {message && (
        <div style={{ padding: '10px 16px',
          backgroundColor: message.startsWith('导出失败') || message.includes('失败') ? '#fff2f0' : '#f6ffed',
          border: `1px solid ${message.startsWith('导出失败') || message.includes('失败') ? '#ffccc7' : '#b7eb8f'}`,
          borderRadius: 4, color: message.startsWith('导出失败') || message.includes('失败') ? '#cf1322' : '#389e0d',
          whiteSpace: 'pre-wrap' }}>
          {message}
        </div>
      )}
    </div>
  );
};

export default DataExportPage;
