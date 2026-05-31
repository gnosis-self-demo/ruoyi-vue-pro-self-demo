import React, { useState } from 'react';

const API_BASE = '/data-exportor';

interface ImportResult {
  totalRows: number;
  successRows: number;
  failedRows: number;
  skippedRows: number;
  totalSheets: number;
  durationMs: number;
  errors: ErrorRecord[];
}

interface ErrorRecord {
  sheetIndex: number;
  rowNumber: number;
  rowData: string;
  errorMessage: string;
}

const DataImportPage: React.FC = () => {
  const [tableName, setTableName] = useState('');
  const [batchSize, setBatchSize] = useState<number | ''>('');
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState({ processed: 0, total: 0, percent: 0 });
  const [message, setMessage] = useState('');
  const [result, setResult] = useState<ImportResult | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (selectedFile) {
      setFile(selectedFile);
      setResult(null);
      setMessage('');
    }
  };

  const handleImport = async () => {
    if (!tableName.trim()) {
      setMessage('请输入目标表名');
      return;
    }
    if (!file) {
      setMessage('请选择要导入的Excel文件');
      return;
    }

    setLoading(true);
    setMessage('正在导入...');
    setResult(null);
    setProgress({ processed: 0, total: 0, percent: 0 });

    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('tableName', tableName.trim());
      if (batchSize !== '' && batchSize > 0) {
        formData.append('batchSize', String(batchSize));
      }

      const response = await fetch(`${API_BASE}/import/execute`, {
        method: 'POST',
        body: formData,
      });

      const data = await response.json();

      if (data.code !== 200) {
        throw new Error(data.message || '导入失败');
      }

      const importResult: ImportResult = data.data;
      setResult(importResult);
      setMessage('导入完成');
      setProgress({ processed: 100, total: 100, percent: 100 });
    } catch (err: any) {
      setMessage(`导入失败: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  const formatDuration = (ms: number) => {
    if (ms < 1000) return `${ms} ms`;
    if (ms < 60000) return `${(ms / 1000).toFixed(1)} 秒`;
    return `${(ms / 60000).toFixed(1)} 分钟`;
  };

  return (
    <div style={{ padding: 24, maxWidth: 900, margin: '0 auto' }}>
      <h2>数据导入</h2>

      <div style={{ marginBottom: 16 }}>
        <label style={{ display: 'block', marginBottom: 8, fontWeight: 'bold' }}>
          目标表名 <span style={{ color: 'red' }}>*</span>
        </label>
        <input
          value={tableName}
          onChange={e => setTableName(e.target.value)}
          placeholder="请输入数据库目标表名"
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
          批次大小
        </label>
        <input
          type="number"
          value={batchSize}
          onChange={e => setBatchSize(e.target.value === '' ? '' : Number(e.target.value))}
          placeholder="默认自动计算（5000）"
          min={100}
          max={50000}
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
          选择Excel文件 <span style={{ color: 'red' }}>*</span>
        </label>
        <input
          type="file"
          accept=".xlsx,.xls"
          onChange={handleFileChange}
          style={{ fontSize: 14 }}
        />
        {file && (
          <div style={{ marginTop: 8, color: '#666', fontSize: 13 }}>
            已选择: {file.name} ({(file.size / 1024).toFixed(1)} KB)
          </div>
        )}
      </div>

      <div style={{ marginBottom: 16 }}>
        <button
          onClick={handleImport}
          disabled={loading}
          style={{
            padding: '10px 24px',
            backgroundColor: loading ? '#b0b0b0' : '#52c41a',
            color: '#fff',
            border: 'none',
            borderRadius: 4,
            fontSize: 15,
            cursor: loading ? 'not-allowed' : 'pointer',
            marginRight: 12,
          }}
        >
          {loading ? '导入中...' : '执行导入'}
        </button>
      </div>

      {loading && (
        <div style={{ marginBottom: 16 }}>
          <div style={{ height: 8, backgroundColor: '#f0f0f0', borderRadius: 4, overflow: 'hidden' }}>
            <div
              style={{
                height: '100%',
                width: `${progress.percent}%`,
                backgroundColor: '#52c41a',
                transition: 'width 0.3s',
              }}
            />
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
            marginBottom: 16,
          }}
        >
          {message}
        </div>
      )}

      {result && (
        <div
          style={{
            border: '1px solid #d9d9d9',
            borderRadius: 4,
            padding: 16,
            backgroundColor: '#fafafa',
          }}
        >
          <h4 style={{ marginTop: 0 }}>导入结果</h4>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <tbody>
              <tr>
                <td style={{ padding: '4px 8px', fontWeight: 'bold' }}>总处理行数</td>
                <td style={{ padding: '4px 8px' }}>{result.totalRows}</td>
                <td style={{ padding: '4px 8px', fontWeight: 'bold' }}>成功导入</td>
                <td style={{ padding: '4px 8px', color: '#389e0d' }}>{result.successRows}</td>
              </tr>
              <tr>
                <td style={{ padding: '4px 8px', fontWeight: 'bold' }}>失败行数</td>
                <td style={{ padding: '4px 8px', color: '#cf1322' }}>{result.failedRows}</td>
                <td style={{ padding: '4px 8px', fontWeight: 'bold' }}>跳过行数</td>
                <td style={{ padding: '4px 8px', color: '#faad14' }}>{result.skippedRows}</td>
              </tr>
              <tr>
                <td style={{ padding: '4px 8px', fontWeight: 'bold' }}>总Sheet数</td>
                <td style={{ padding: '4px 8px' }}>{result.totalSheets}</td>
                <td style={{ padding: '4px 8px', fontWeight: 'bold' }}>耗时</td>
                <td style={{ padding: '4px 8px' }}>{formatDuration(result.durationMs)}</td>
              </tr>
            </tbody>
          </table>

          {result.errors && result.errors.length > 0 && (
            <div style={{ marginTop: 16 }}>
              <h4>错误详情 ({result.errors.length} 条)</h4>
              <div style={{ maxHeight: 300, overflow: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 13 }}>
                  <thead>
                    <tr style={{ backgroundColor: '#f5f5f5' }}>
                      <th style={{ padding: '6px 8px', border: '1px solid #e8e8e8', textAlign: 'left' }}>
                        Sheet
                      </th>
                      <th style={{ padding: '6px 8px', border: '1px solid #e8e8e8', textAlign: 'left' }}>
                        行号
                      </th>
                      <th style={{ padding: '6px 8px', border: '1px solid #e8e8e8', textAlign: 'left' }}>
                        错误信息
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {result.errors.map((err, idx) => (
                      <tr key={idx}>
                        <td style={{ padding: '4px 8px', border: '1px solid #e8e8e8' }}>
                          {err.sheetIndex + 1}
                        </td>
                        <td style={{ padding: '4px 8px', border: '1px solid #e8e8e8' }}>
                          {err.rowNumber}
                        </td>
                        <td style={{ padding: '4px 8px', border: '1px solid #e8e8e8', color: '#cf1322' }}>
                          {err.errorMessage}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default DataImportPage;