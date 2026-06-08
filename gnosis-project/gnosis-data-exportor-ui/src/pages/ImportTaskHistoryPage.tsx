import React, { useState, useEffect } from 'react';

const API_BASE = '/data-exportor';

interface ImportTask {
  id: string;
  taskName: string;
  tableName: string;
  fileName: string;
  importFormat: string;
  totalRows: number;
  successRows: number;
  failedRows: number;
  skippedRows: number;
  totalSheets: number;
  status: string;
  errorMessage: string;
  durationMs: number;
  createUserId: string;
  createTime: string;
  updateUserId: string;
  updateTime: string;
}

interface PageResponse {
  total: number;
  pageNum: number;
  pageSize: number;
  records: ImportTask[];
}

const ImportTaskHistoryPage: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<PageResponse | null>(null);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize] = useState(10);
  const [filterStatus, setFilterStatus] = useState('');
  const [filterFormat, setFilterFormat] = useState('');
  const [filterName, setFilterName] = useState('');
  const [detail, setDetail] = useState<ImportTask | null>(null);

  const fetchData = async () => {
    setLoading(true);
    try {
      const body: any = { pageNum, pageSize };
      if (filterStatus) body.status = filterStatus;
      if (filterFormat) body.format = filterFormat;
      if (filterName) body.taskName = filterName;

      const res = await fetch(`${API_BASE}/import/page`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });
      const json = await res.json();
      if (json.code === 200) {
        setData(json.data);
      }
    } catch (e: any) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const fetchDetail = async (id: string) => {
    try {
      const res = await fetch(`${API_BASE}/import/detail`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id }),
      });
      const json = await res.json();
      if (json.code === 200) {
        setDetail(json.data);
      }
    } catch (e: any) {
      console.error(e);
    }
  };

  useEffect(() => { fetchData(); }, [pageNum]);

  const formatDuration = (ms: number) => {
    if (!ms) return '-';
    if (ms < 1000) return `${ms} ms`;
    if (ms < 60000) return `${(ms / 1000).toFixed(1)} 秒`;
    return `${(ms / 60000).toFixed(1)} 分钟`;
  };

  const statusStyle = (status: string): React.CSSProperties => ({
    display: 'inline-block',
    padding: '2px 8px',
    borderRadius: 4,
    fontSize: 12,
    backgroundColor: status === 'SUCCESS' ? '#f6ffed' : status === 'FAILED' ? '#fff2f0' : '#e6f7ff',
    color: status === 'SUCCESS' ? '#389e0d' : status === 'FAILED' ? '#cf1322' : '#1890ff',
    border: `1px solid ${status === 'SUCCESS' ? '#b7eb8f' : status === 'FAILED' ? '#ffccc7' : '#91d5ff'}`,
  });

  const thStyle: React.CSSProperties = {
    padding: '10px 12px', textAlign: 'left', borderBottom: '2px solid #e8e8e8',
    backgroundColor: '#fafafa', fontWeight: 'bold', fontSize: 13, whiteSpace: 'nowrap',
  };
  const tdStyle: React.CSSProperties = {
    padding: '8px 12px', borderBottom: '1px solid #f0f0f0', fontSize: 13,
  };

  return (
    <div style={{ padding: 24, maxWidth: 1200, margin: '0 auto' }}>
      <h2>导入任务记录</h2>

      <div style={{ display: 'flex', gap: 12, marginBottom: 16, flexWrap: 'wrap', alignItems: 'center' }}>
        <input
          placeholder="任务名称"
          value={filterName}
          onChange={e => setFilterName(e.target.value)}
          style={{ padding: '6px 10px', border: '1px solid #d9d9d9', borderRadius: 4, width: 160 }}
        />
        <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)}
          style={{ padding: '6px 10px', border: '1px solid #d9d9d9', borderRadius: 4 }}>
          <option value="">全部状态</option>
          <option value="SUCCESS">成功</option>
          <option value="FAILED">失败</option>
          <option value="RUNNING">运行中</option>
        </select>
        <select value={filterFormat} onChange={e => setFilterFormat(e.target.value)}
          style={{ padding: '6px 10px', border: '1px solid #d9d9d9', borderRadius: 4 }}>
          <option value="">全部格式</option>
          <option value="EXCEL">Excel</option>
          <option value="ZIP">ZIP</option>
        </select>
        <button onClick={() => { setPageNum(1); fetchData(); }}
          style={{ padding: '6px 16px', backgroundColor: '#52c41a', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
          查询
        </button>
      </div>

      {loading && <div style={{ padding: 20, textAlign: 'center', color: '#888' }}>加载中...</div>}

      {data && (
        <>
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr>
                  <th style={thStyle}>任务名称</th>
                  <th style={thStyle}>目标表</th>
                  <th style={thStyle}>文件名</th>
                  <th style={thStyle}>格式</th>
                  <th style={thStyle}>状态</th>
                  <th style={thStyle}>总行数</th>
                  <th style={thStyle}>成功/失败</th>
                  <th style={thStyle}>耗时</th>
                  <th style={thStyle}>创建人</th>
                  <th style={thStyle}>创建时间</th>
                  <th style={thStyle}>操作</th>
                </tr>
              </thead>
              <tbody>
                {data.records.map(t => (
                  <tr key={t.id}>
                    <td style={tdStyle}>{t.taskName}</td>
                    <td style={tdStyle}>{t.tableName || '-'}</td>
                    <td style={tdStyle}>{t.fileName || '-'}</td>
                    <td style={tdStyle}>{t.importFormat || '-'}</td>
                    <td style={tdStyle}><span style={statusStyle(t.status)}>{t.status}</span></td>
                    <td style={tdStyle}>{t.totalRows != null ? t.totalRows.toLocaleString() : '-'}</td>
                    <td style={tdStyle}>
                      <span style={{ color: '#389e0d' }}>{t.successRows || 0}</span>
                      {' / '}
                      <span style={{ color: '#cf1322' }}>{t.failedRows || 0}</span>
                    </td>
                    <td style={tdStyle}>{formatDuration(t.durationMs)}</td>
                    <td style={tdStyle}>{t.createUserId || '-'}</td>
                    <td style={tdStyle}>{t.createTime || '-'}</td>
                    <td style={tdStyle}>
                      <button onClick={() => fetchDetail(t.id)}
                        style={{ padding: '2px 10px', backgroundColor: '#52c41a', color: '#fff', border: 'none', borderRadius: 3, cursor: 'pointer', fontSize: 12 }}>
                        详情
                      </button>
                    </td>
                  </tr>
                ))}
                {data.records.length === 0 && (
                  <tr><td colSpan={11} style={{ ...tdStyle, textAlign: 'center', color: '#888' }}>暂无数据</td></tr>
                )}
              </tbody>
            </table>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 8, marginTop: 16, alignItems: 'center' }}>
            <span style={{ fontSize: 13, color: '#666' }}>共 {data.total} 条</span>
            <button disabled={pageNum <= 1} onClick={() => setPageNum(p => p - 1)}
              style={{ padding: '4px 12px', border: '1px solid #d9d9d9', borderRadius: 4, cursor: pageNum <= 1 ? 'not-allowed' : 'pointer' }}>
              上一页
            </button>
            <span style={{ fontSize: 13 }}>{pageNum} / {Math.ceil(data.total / pageSize) || 1}</span>
            <button disabled={pageNum >= Math.ceil(data.total / pageSize)}
              onClick={() => setPageNum(p => p + 1)}
              style={{ padding: '4px 12px', border: '1px solid #d9d9d9', borderRadius: 4, cursor: pageNum >= Math.ceil(data.total / pageSize) ? 'not-allowed' : 'pointer' }}>
              下一页
            </button>
          </div>
        </>
      )}

      {detail && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.45)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 }}
          onClick={() => setDetail(null)}>
          <div style={{ backgroundColor: '#fff', borderRadius: 8, padding: 24, maxWidth: 700, width: '90%', maxHeight: '80vh', overflow: 'auto' }}
            onClick={e => e.stopPropagation()}>
            <h3 style={{ marginTop: 0 }}>导入任务详情</h3>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <tbody>
                {[
                  ['任务ID', detail.id],
                  ['任务名称', detail.taskName],
                  ['目标表', detail.tableName],
                  ['文件名', detail.fileName],
                  ['导入格式', detail.importFormat],
                  ['总处理行数', detail.totalRows != null ? detail.totalRows.toLocaleString() : '-'],
                  ['成功行数', detail.successRows != null ? detail.successRows.toLocaleString() : '-'],
                  ['失败行数', detail.failedRows != null ? detail.failedRows.toLocaleString() : '-'],
                  ['跳过行数', detail.skippedRows != null ? detail.skippedRows.toLocaleString() : '-'],
                  ['总条目数', detail.totalSheets != null ? String(detail.totalSheets) : '-'],
                  ['状态', detail.status],
                  ['错误信息', detail.errorMessage || '-'],
                  ['耗时', formatDuration(detail.durationMs)],
                  ['创建人', detail.createUserId],
                  ['创建时间', detail.createTime],
                  ['更新人', detail.updateUserId],
                  ['更新时间', detail.updateTime],
                ].map(([label, value]) => (
                  <tr key={label}>
                    <td style={{ padding: '6px 12px', fontWeight: 'bold', fontSize: 13, width: 100, borderBottom: '1px solid #f0f0f0' }}>{label}</td>
                    <td style={{ padding: '6px 12px', fontSize: 13, borderBottom: '1px solid #f0f0f0', wordBreak: 'break-all' }}>{value}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div style={{ textAlign: 'right', marginTop: 16 }}>
              <button onClick={() => setDetail(null)}
                style={{ padding: '6px 20px', border: '1px solid #d9d9d9', borderRadius: 4, cursor: 'pointer', backgroundColor: '#fff' }}>
                关闭
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ImportTaskHistoryPage;