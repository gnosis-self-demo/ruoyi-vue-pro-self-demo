const API_BASE = '/data-exportor';

export interface SqlEntry {
  sql: string;
  sheetName: string;
}

export interface ExportRequest {
  /** 多条SQL（推荐），每条对应一个Sheet */
  sqlEntries: SqlEntry[];
}

export async function executeExport(request: ExportRequest): Promise<Blob> {
  const response = await fetch(`${API_BASE}/export/execute`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({ message: '导出请求失败' }));
    throw new Error(errorData.message || '导出失败');
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

  return blob;
}

export function downloadBlob(blob: Blob, filename: string): void {
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  window.URL.revokeObjectURL(url);
}
