import React, { useCallback, useState } from 'react'

export default function FileUpload({ onUpload, loading }) {
  const [dragging, setDragging] = useState(false)
  const [fileName, setFileName] = useState(null)

  const handleFile = useCallback(async (file) => {
    if (!file) return
    if (!file.name.endsWith('.sql')) { alert('Please upload a .sql file'); return }
    setFileName(file.name)
    onUpload(file)
  }, [onUpload])

  const onDrop = useCallback((e) => {
    e.preventDefault(); setDragging(false)
    handleFile(e.dataTransfer.files[0])
  }, [handleFile])

  return (
    <div
      onDragOver={(e) => { e.preventDefault(); setDragging(true) }}
      onDragLeave={() => setDragging(false)}
      onDrop={onDrop}
      onClick={() => document.getElementById('sql-file-input').click()}
      style={{
        border: `2px dashed ${dragging ? '#6366f1' : '#cbd5e1'}`,
        borderRadius: '12px', padding: '40px 32px', textAlign: 'center',
        background: dragging ? '#eef2ff' : '#fff',
        transition: 'all 0.2s ease', cursor: 'pointer',
      }}
    >
      <input id="sql-file-input" type="file" accept=".sql" style={{ display: 'none' }} onChange={(e) => handleFile(e.target.files[0])} />
      <div style={{ fontSize: '40px', marginBottom: '12px' }}>
        {loading ? '⏳' : dragging ? '📂' : '🗃️'}
      </div>
      <div style={{ fontSize: '16px', fontWeight: 600, color: '#0f172a', marginBottom: '6px' }}>
        {loading ? 'Parsing your schema...' : fileName ? `Loaded: ${fileName}` : 'Drop your .sql file here'}
      </div>
      <div style={{ fontSize: '13px', color: '#94a3b8', marginBottom: '18px' }}>
        {loading ? 'Building the graph...' : 'or click to browse · MySQL, PostgreSQL, SQLite supported'}
      </div>
      {!loading && (
        <div style={{
          display: 'inline-block', padding: '9px 24px',
          background: '#4f46e5', borderRadius: '8px',
          fontSize: '13px', fontWeight: 600, color: '#fff',
        }}>
          Choose File
        </div>
      )}
    </div>
  )
}