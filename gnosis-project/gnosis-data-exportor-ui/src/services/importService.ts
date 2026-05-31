const API_BASE = '/data-exportor';

export interface ImportRequest {
  tableName: string;
  batchSize?: number;
}

export interface ImportResult {
  totalRows: number;
  successRows: number;
  failedRows: number;
  skippedRows: number;
  totalSheets: number;
  durationMs: number;
  errors: ErrorRecord[];
}

export interface ErrorRecord {
  sheetIndex: number;
  rowNumber: number;
  rowData: string;
  errorMessage: string;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export async function executeImport(file: File, request: ImportRequest): Promise<ImportResult> {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('tableName', request.tableName);
  if (request.batchSize && request.batchSize > 0) {
    formData.append('batchSize', String(request.batchSize));
  }

  const response = await fetch(`${API_BASE}/import/execute`, {
    method: 'POST',
    body: formData,
  });

  const result: ApiResponse<ImportResult> = await response.json();

  if (result.code !== 200) {
    throw new Error(result.message || '导入失败');
  }

  return result.data;
}