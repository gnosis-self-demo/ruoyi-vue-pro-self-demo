const API_BASE = '/data-exportor';

export interface ExportRequest {
  sql: string;
  sheetNamePrefix?: string;
  columnNames?: string[];
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

  return response.blob();
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