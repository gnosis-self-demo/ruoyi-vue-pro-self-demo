import React from 'react'
import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom'
import DataExportPage from './pages/DataExportPage'
import DataImportPage from './pages/DataImportPage'
import ExportTaskHistoryPage from './pages/ExportTaskHistoryPage'
import ImportTaskHistoryPage from './pages/ImportTaskHistoryPage'

const App: React.FC = () => {
  const navStyle = {
    display: 'flex',
    gap: 0,
    backgroundColor: '#001529',
    padding: '0 24px',
    margin: 0,
  }

  const linkStyle = ({ isActive }: { isActive: boolean }) => ({
    display: 'block',
    padding: '14px 20px',
    color: isActive ? '#fff' : 'rgba(255,255,255,0.65)',
    backgroundColor: isActive ? '#1890ff' : 'transparent',
    textDecoration: 'none',
    fontSize: 15,
    fontWeight: isActive ? 'bold' : 'normal',
  } as React.CSSProperties)

  return (
    <BrowserRouter>
      <div style={{ minHeight: '100vh', backgroundColor: '#f0f2f5' }}>
        <nav style={navStyle}>
          <NavLink to="/export" style={linkStyle}>数据导出</NavLink>
          <NavLink to="/import" style={linkStyle}>数据导入</NavLink>
          <NavLink to="/export-history" style={linkStyle}>导出记录</NavLink>
          <NavLink to="/import-history" style={linkStyle}>导入记录</NavLink>
        </nav>
        <div style={{ padding: 0 }}>
          <Routes>
            <Route path="/export" element={<DataExportPage />} />
            <Route path="/import" element={<DataImportPage />} />
            <Route path="/export-history" element={<ExportTaskHistoryPage />} />
            <Route path="/import-history" element={<ImportTaskHistoryPage />} />
            <Route path="/" element={<DataExportPage />} />
          </Routes>
        </div>
      </div>
    </BrowserRouter>
  )
}

export default App