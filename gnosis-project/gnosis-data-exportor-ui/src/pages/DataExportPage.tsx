import React, { useState } from 'react';

const API_BASE = '/data-exportor';

interface ExportRequest {
  sql: string;
  sheetNamePrefix?: string;
  columnNames?: string[];
}

const DataExportPage: React.FC = () => {
  const [sql, setSql] = useState('');
  const [sheetNamePrefix, setSheetNamePrefix] = useState('');
  const [columnNames, setColumnNames] = useState('');
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState({ processed: 0, total: 0, percent: 0 });
  const [message, setMessage] = useState('');

  const handleExport = async () => {
    if (!sql.trim()) {
      setMessage('请输入SQL查询语句');
      return;
    }

    setLoading(true);
    setMessage('正在导出...');
    setProgress({ processed: 0, total: 0, percent: 0 });

    try {
      const request: ExportRequest = {
        sql: sql.trim(),
        sheetNamePrefix: sheetNamePrefix.trim() || undefined,
        columnNames: columnNames.trim() ? columnNames.split(',').map(s => s.trim()) : undefined,
      };

      const response = await fetch(`${API_BASE}/export/execute`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || '导出失败');
      }

      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `export_data_${Date.now()}.xlsx`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);

      setMessage('导出完成');
      setProgress({ processed: 100, total: 100, percent: 100 });
    } catch (err: any) {
      setMessage(`导出失败: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: 24, maxWidth: 900, margin: '0 auto' }}>
      <h2>数据导出</h2>

      <div style={{ marginBottom: 16 }}>
        <label style={{ display: 'block', marginBottom: 8, fontWeight: 'bold' }}>
          SQL查询语句 <span style={{ color: 'red' }}>*</span>
        </label>
        <textarea
          value={sql}
          onChange={e => setSql(e.target.value)}
          placeholder="请输入SQL查询语句，建议包含 ORDER BY 子句&#10;示例：SELECT * FROM orders ORDER BY id"
          rows={6}
          style={{
            width: '100%',
            padding: 10,
            border: '1px solid #d9d9d9',
            borderRadius: 4,
            fontFamily: 'monospace',
            fontSize: 14,
          }}
        />
      </div>

      <div style={{ marginBottom: 16 }}>
        <label style={{ display: 'block', marginBottom: 8, fontWeight: 'bold' }}>
          Sheet名称前缀
        </label>
        <input
          value={sheetNamePrefix}
          onChange={e => setSheetNamePrefix(e.target.value)}
          placeholder="默认 Sheet，多Sheet时自动追加 _1, _2..."
          style={{
            width: '100%',
            padding: '8px 10px',
            border: '1px solid #d9d9d9',
            borderRadius: 4,
            fontSize: 14,
          }}
        />
      </div>

      <div style={{ marginBottom: 16 }}>
        <label style={{ display: 'block', marginBottom: 8, fontWeight: 'bold' }}>
          自定义列名（逗号分隔）
        </label>
        <input
          value={columnNames}
          onChange={e => setColumnNames(e.target.value)}
          placeholder="不填则自动从查询结果获取列名"
          style={{
            width: '100%',
            padding: '8px 10px',
            border: '1px solid #d9d9d9',
            borderRadius: 4,
            fontSize: 14,
          }}
        />
      </div>

      <div style={{ marginBottom: 16 }}>
        <button
          onClick={handleExport}
          disabled={loading}
          style={{
            padding: '10px 24px',
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

      {loading && (
        <div style={{ marginBottom: 16 }}>
          <div style={{ height: 8, backgroundColor: '#f0f0f0', borderRadius: 4, overflow: 'hidden' }}>
            <div
              style={{
                height: '100%',
                width: `${progress.percent}%`,
                backgroundColor: '#1890ff',
                transition: 'width 0.3s',
              }}
            />
          </div>
          <div style={{ marginTop: 4, color: '#666', fontSize: 13 }}>
            {progress.processed > 0 && `${progress.processed} / ${progress.total} 行`}
          </div>
        </div>
      )}

      {message && (
        <div
          style={{
            padding: '10px 16px',
            backgroundColor: message.includes('失败') ? '#fff2f0' : '#f6ffed',
            border: `1px solid ${message.includes('失败') ? '#ffccc7' : '#b7eb8f'}`,
            borderRadius: 4,
            color: message.includes('失败') ? '#cf1322' : '#389e0d',
          }}
        >
          {message}
        </div>
      )}
    </div>
  );
};

export default DataExportPage;