import React, { useState } from 'react';

const API_BASE = '/data-exportor';

interface SqlEntry {
  id: string;
  sql: string;
  sheetName: string;
}

const newEntry = (): SqlEntry => ({
  id: String(Date.now()) + Math.random().toString(36).slice(2),
  sql: '',
  sheetName: '',
});

const DataExportPage: React.FC = () => {
  const [entries, setEntries] = useState<SqlEntry[]>([newEntry()]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const updateEntry = (id: string, field: 'sql' | 'sheetName', value: string) => {
    setEntries(prev => prev.map(e => (e.id === id ? { ...e, [field]: value } : e)));
  };

  const addEntry = () => setEntries(prev => [...prev, newEntry()]);
  const removeEntry = (id: string) => {
    if (entries.length <= 1) return;
    setEntries(prev => prev.filter(e => e.id !== id));
  };

  const handleExport = async () => {
    const valid = entries.filter(e => e.sql.trim());
    if (valid.length === 0) {
      setMessage('请至少填写一条SQL查询语句');
      return;
    }

    const emptySheet = valid.find(e => !e.sheetName.trim());
    if (emptySheet) {
      setMessage('每条SQL必须填写 Sheet名称');
      return;
    }

    setLoading(true);
    setMessage('正在导出...');

    try {
      const sqlEntries = valid.map(e => ({
        sql: e.sql.trim(),
        sheetName: e.sheetName.trim(),
      }));

      const response = await fetch(`${API_BASE}/export/execute`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sqlEntries }),
      });

      if (!response.ok) {
        // May return JSON error even with 200 status
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

      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `export_data_${Date.now()}.xlsx`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);

      setMessage('导出完成');
    } catch (err: any) {
      setMessage(`导出失败: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: 24, maxWidth: 960, margin: '0 auto' }}>
      <h2>数据导出</h2>
      <p style={{ color: '#888', marginBottom: 20, fontSize: 13 }}>
        每个 SQL 对应一个 Sheet，多条 SQL 使用 sqlEntries 数组发送。
        表结构和列名从数据库自动获取，Sheet名溢出时自动加 _1, _2 后缀。
      </p>

      {entries.map((entry, idx) => (
        <div
          key={entry.id}
          style={{
            marginBottom: 20,
            padding: 16,
            border: '1px solid #e8e8e8',
            borderRadius: 6,
            backgroundColor: '#fafafa',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', marginBottom: 10 }}>
            <strong style={{ marginRight: 8 }}>
              SQL #{idx + 1}
            </strong>
            {entries.length > 1 && (
              <button
                onClick={() => removeEntry(entry.id)}
                style={{
                  color: '#ff4d4f',
                  background: 'none',
                  border: '1px solid #ff4d4f',
                  borderRadius: 4,
                  cursor: 'pointer',
                  fontSize: 13,
                  padding: '2px 10px',
                }}
              >
                删除
              </button>
            )}
          </div>

          <div style={{ marginBottom: 10 }}>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 'bold', fontSize: 14 }}>
              SQL 查询语句 <span style={{ color: 'red' }}>*</span>
            </label>
            <textarea
              value={entry.sql}
              onChange={e => updateEntry(entry.id, 'sql', e.target.value)}
              placeholder="SELECT * FROM orders ORDER BY id"
              rows={4}
              style={{
                width: '100%',
                padding: 8,
                border: '1px solid #d9d9d9',
                borderRadius: 4,
                fontFamily: 'monospace',
                fontSize: 13,
                boxSizing: 'border-box',
              }}
            />
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 'bold', fontSize: 14 }}>
              Sheet 名称 <span style={{ color: 'red' }}>*</span>
            </label>
            <input
              value={entry.sheetName}
              onChange={e => updateEntry(entry.id, 'sheetName', e.target.value)}
              placeholder="如: orders, api_config"
              style={{
                width: '100%',
                padding: '6px 10px',
                border: '1px solid #d9d9d9',
                borderRadius: 4,
                fontSize: 13,
                boxSizing: 'border-box',
              }}
            />
          </div>
        </div>
      ))}

      <div style={{ marginBottom: 16 }}>
        <button
          onClick={addEntry}
          disabled={loading}
          style={{
            padding: '6px 18px',
            backgroundColor: '#f5f5f5',
            border: '1px dashed #d9d9d9',
            borderRadius: 4,
            fontSize: 14,
            cursor: loading ? 'not-allowed' : 'pointer',
          }}
        >
          + 添加 SQL
        </button>
      </div>

      <div style={{ marginBottom: 16 }}>
        <button
          onClick={handleExport}
          disabled={loading}
          style={{
            padding: '10px 28px',
            backgroundColor: loading ? '#b0b0b0' : '#1890ff',
            color: '#fff',
            border: 'none',
            borderRadius: 4,
            fontSize: 15,
            cursor: loading ? 'not-allowed' : 'pointer',
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
              ? '#fff2f0' : '#f6ffed',
            border: `1px solid ${
              message.startsWith('导出失败') || message.includes('失败')
                ? '#ffccc7' : '#b7eb8f'
            }`,
            borderRadius: 4,
            color: message.startsWith('导出失败') || message.includes('失败')
              ? '#cf1322' : '#389e0d',
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
