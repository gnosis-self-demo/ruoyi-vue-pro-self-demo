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

/** 读取文件并转为base64 */
function fileToBase64(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      const result = reader.result as string;
      // 去掉 data:...;base64, 前缀
      const base64 = result.substring(result.indexOf(',') + 1);
      resolve(base64);
    };
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
}

const DataImportPage: React.FC = () => {
  const [jdbcUrl, setJdbcUrl] = useState('jdbc:opengauss://localserver.gnosis:5432/gnosis_sample?prepareThreshold=0');
  const [username, setUsername] = useState('gaussdb');
  const [password, setPassword] = useState('');
  const [tableName, setTableName] = useState('');
  const [batchSize, setBatchSize] = useState<number | ''>('');
  const [columnMapping, setColumnMapping] = useState('');
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [result, setResult] = useState<ImportResult | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (selectedFile) { setFile(selectedFile); setResult(null); setMessage(''); }
  };

  const parseColumnMapping = (text: string): Record<string, string> | undefined => {
    const lines = text.trim().split('\n').filter(l => l.trim());
    if (lines.length === 0) return undefined;
    const map: Record<string, string> = {};
    for (const line of lines) {
      const parts = line.split(/[=:]/).map(s => s.trim());
      if (parts.length >= 2 && parts[0] && parts[1]) map[parts[0]] = parts[1];
    }
    return Object.keys(map).length > 0 ? map : undefined;
  };

  const handleImport = async () => {
    if (!jdbcUrl.trim()) { setMessage('请输入 JDBC地址'); return; }
    if (!username.trim()) { setMessage('请输入数据库用户名'); return; }
    if (!tableName.trim()) { setMessage('请输入目标表名'); return; }
    if (!file) { setMessage('请选择要导入的Excel文件'); return; }

    setLoading(true);
    setMessage('正在导入...');
    setResult(null);

    try {
      const fileData = await fileToBase64(file);
      const body: any = {
        jdbcUrl: jdbcUrl.trim(),
        username: username.trim(),
        password: password,
        tableName: tableName.trim(),
        fileName: file.name,
        fileData: fileData,
      };
      if (batchSize !== '' && batchSize > 0) body.batchSize = batchSize;
      const mapping = parseColumnMapping(columnMapping);
      if (mapping) body.columnMapping = mapping;

      const response = await fetch(`${API_BASE}/import/execute`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });
      const data = await response.json();

      if (data.code !== 200) throw new Error(data.msg || data.message || '导入失败');

      const importResult: ImportResult = data.data;
      setResult(importResult);
      setMessage('导入完成');
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

  const inputStyle: React.CSSProperties = {
    width: '100%', padding: '8px 10px', border: '1px solid #d9d9d9',
    borderRadius: 4, fontSize: 14, boxSizing: 'border-box',
  };

  return (
    <div style={{ padding: 24, maxWidth: 960, margin: '0 auto' }}>
      <h2>数据导入</h2>

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
          目标表名 <span style={{ color: 'red' }}>*</span>
        </label>
        <input value={tableName} onChange={e => setTableName(e.target.value)}
          placeholder="请输入数据库目标表名" style={inputStyle} />
      </div>

      <div style={{ marginBottom: 16 }}>
        <label style={{ display: 'block', marginBottom: 8, fontWeight: 'bold' }}>批次大小</label>
        <input type="number" value={batchSize}
          onChange={e => setBatchSize(e.target.value === '' ? '' : Number(e.target.value))}
          placeholder="默认自动计算（5000）" min={100} max={50000} style={inputStyle} />
      </div>

      <div style={{ marginBottom: 16 }}>
        <label style={{ display: 'block', marginBottom: 8, fontWeight: 'bold' }}>
          列映射（可选，每行一条 Excel列名=数据库字段名）
        </label>
        <textarea value={columnMapping} onChange={e => setColumnMapping(e.target.value)}
          placeholder={'订单号=order_no\n金额=amount'} rows={3}
          style={{ ...inputStyle, fontFamily: 'monospace', resize: 'vertical' }} />
      </div>

      <div style={{ marginBottom: 16 }}>
        <label style={{ display: 'block', marginBottom: 8, fontWeight: 'bold' }}>
          选择Excel文件 <span style={{ color: 'red' }}>*</span>
        </label>
        <input type="file" accept=".xlsx,.xls" onChange={handleFileChange} style={{ fontSize: 14 }} />
        {file && <div style={{ marginTop: 8, color: '#666', fontSize: 13 }}>
          已选择: {file.name} ({(file.size / 1024).toFixed(1)} KB)
        </div>}
      </div>

      <div style={{ marginBottom: 16 }}>
        <button onClick={handleImport} disabled={loading}
          style={{ padding: '10px 24px', backgroundColor: loading ? '#b0b0b0' : '#52c41a',
            color: '#fff', border: 'none', borderRadius: 4, fontSize: 15,
            cursor: loading ? 'not-allowed' : 'pointer', marginRight: 12 }}>
          {loading ? '导入中...' : '执行导入'}
        </button>
      </div>

      {message && (
        <div style={{ padding: '10px 16px',
          backgroundColor: message.includes('失败') ? '#fff2f0' : '#f6ffed',
          border: `1px solid ${message.includes('失败') ? '#ffccc7' : '#b7eb8f'}`,
          borderRadius: 4, color: message.includes('失败') ? '#cf1322' : '#389e0d', marginBottom: 16 }}>
          {message}
        </div>
      )}

      {result && (
        <div style={{ border: '1px solid #d9d9d9', borderRadius: 4, padding: 16, backgroundColor: '#fafafa' }}>
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
                      <th style={{ padding: '6px 8px', border: '1px solid #e8e8e8', textAlign: 'left' }}>Sheet</th>
                      <th style={{ padding: '6px 8px', border: '1px solid #e8e8e8', textAlign: 'left' }}>行号</th>
                      <th style={{ padding: '6px 8px', border: '1px solid #e8e8e8', textAlign: 'left' }}>错误信息</th>
                    </tr>
                  </thead>
                  <tbody>
                    {result.errors.map((err, idx) => (
                      <tr key={idx}>
                        <td style={{ padding: '4px 8px', border: '1px solid #e8e8e8' }}>{err.sheetIndex + 1}</td>
                        <td style={{ padding: '4px 8px', border: '1px solid #e8e8e8' }}>{err.rowNumber}</td>
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
