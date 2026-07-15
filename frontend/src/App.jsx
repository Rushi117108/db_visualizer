import React, { useState, useCallback, useEffect } from 'react'
import {
  ReactFlow,
  Background,
  Controls,
  MiniMap,
  useNodesState,
  useEdgesState,
} from '@xyflow/react'
import '@xyflow/react/dist/style.css'

import FileUpload from './components/FileUpload.jsx'
import StatsBar from './components/StatsBar.jsx'
import TableNode from './components/TableNode.jsx'
import { useSchemaLayout } from './hooks/useSchemaLayout.js'

const nodeTypes = { tableNode: TableNode }
const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

export default function App() {
  const [schema, setSchema] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [view, setView] = useState('upload')

  const { nodes: layoutNodes, edges: layoutEdges } = useSchemaLayout(schema)
  const [nodes, setNodes, onNodesChange] = useNodesState([])
  const [edges, setEdges, onEdgesChange] = useEdgesState([])

  useEffect(() => {
    if (layoutNodes.length > 0) {
      setNodes(layoutNodes)
      setEdges(layoutEdges)
    }
  }, [layoutNodes, layoutEdges])

  const handleUpload = useCallback(async (file) => {
    setLoading(true)
    setError(null)
    try {
      const formData = new FormData()
      formData.append('file', file)
      const res = await fetch(`${API_BASE}/upload`, { method: 'POST', body: formData })
      if (!res.ok) {
        const err = await res.json().catch(() => ({ error: 'Server error' }))
        throw new Error(err.error || `HTTP ${res.status}`)
      }
      const data = await res.json()
      if (!data.tables || data.tables.length === 0)
        throw new Error('No tables found in the SQL file.')
      setSchema(data)
      setView('graph')
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [])

  return (
    <div style={{ width: '100vw', height: '100vh', display: 'flex', flexDirection: 'column', background: '#f8fafc', overflow: 'hidden' }}>

      {/* Header */}
      <div style={{
        height: '56px', flexShrink: 0,
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '0 24px',
        background: '#fff',
        borderBottom: '1px solid #e2e8f0',
        boxShadow: '0 1px 3px rgba(0,0,0,0.06)',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <span style={{ fontSize: '22px' }}>🔭</span>
          <div>
            <div style={{ fontSize: '16px', fontWeight: 700, color: '#0f172a' }}>DB Visualizer</div>
            <div style={{ fontSize: '11px', color: '#94a3b8' }}>SQL Schema Explorer</div>
          </div>
        </div>
        {schema && (
          <button
            onClick={() => setView(view === 'graph' ? 'upload' : 'graph')}
            style={{
              padding: '7px 16px', borderRadius: '8px',
              border: '1px solid #e2e8f0', background: '#f8fafc',
              color: '#475569', fontSize: '13px', cursor: 'pointer',
              fontWeight: 500,
            }}
          >
            {view === 'graph' ? '⬆ Upload New' : '← Back to Graph'}
          </button>
        )}
      </div>

      {/* Stats bar */}
      {schema && view === 'graph' && (
        <div style={{
          height: '56px', flexShrink: 0,
          padding: '0 24px', display: 'flex', alignItems: 'center',
          background: '#fff', borderBottom: '1px solid #e2e8f0',
        }}>
          <StatsBar schema={schema} />
        </div>
      )}

      {/* Main */}
      <div style={{ flex: 1, minHeight: 0, position: 'relative' }}>

        {/* Upload view */}
        {view === 'upload' && (
          <div style={{
            position: 'absolute', inset: 0,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            padding: '40px', overflowY: 'auto', background: '#f8fafc',
          }}>
            <div style={{ width: '100%', maxWidth: '520px' }}>
              <div style={{ textAlign: 'center', marginBottom: '32px' }}>
                <div style={{ fontSize: '48px', marginBottom: '12px' }}>🗄️</div>
                <h1 style={{ fontSize: '26px', fontWeight: 700, color: '#0f172a', marginBottom: '8px' }}>
                  Visualize your DB schema
                </h1>
                <p style={{ fontSize: '14px', color: '#64748b' }}>
                  Upload a SQL file and instantly see your tables, columns, and relationships as an interactive graph.
                </p>
              </div>

              <FileUpload onUpload={handleUpload} loading={loading} />

              {error && (
                <div style={{
                  marginTop: '16px', padding: '12px 16px',
                  background: '#fef2f2', border: '1px solid #fecaca',
                  borderRadius: '8px', color: '#dc2626', fontSize: '13px',
                }}>
                  ❌ {error}
                </div>
              )}

              <div style={{ marginTop: '24px', padding: '16px', background: '#fff', borderRadius: '10px', border: '1px solid #e2e8f0' }}>
                <div style={{ fontSize: '12px', color: '#94a3b8', marginBottom: '8px', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.5px' }}>Supports</div>
                <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                  {['MySQL', 'PostgreSQL', 'SQLite', 'Oracle SQL'].map(d => (
                    <span key={d} style={{ padding: '4px 10px', background: '#f1f5f9', borderRadius: '5px', fontSize: '12px', color: '#475569', fontWeight: 500 }}>{d}</span>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Graph view */}
        {view === 'graph' && (
          <div style={{ width: '100%', height: '100%' }}>
            <ReactFlow
              nodes={nodes}
              edges={edges}
              onNodesChange={onNodesChange}
              onEdgesChange={onEdgesChange}
              nodeTypes={nodeTypes}
              fitView
              fitViewOptions={{ padding: 0.15 }}
              minZoom={0.1}
              maxZoom={2}
              style={{ width: '100%', height: '100%', background: '#f1f5f9' }}
            >
              <Background color="#cbd5e1" gap={24} size={1} variant="dots" />
              <Controls style={{ background: '#fff', border: '1px solid #e2e8f0', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }} />
              <MiniMap
                nodeColor={(n) => {
                  const name = n.data?.name?.toLowerCase() || ''
                  if (name.includes('user')) return '#3b82f6'
                  if (name.includes('order')) return '#f59e0b'
                  if (name.includes('payment')) return '#10b981'
                  if (name.includes('product')) return '#8b5cf6'
                  if (name.includes('categor')) return '#ec4899'
                  return '#6366f1'
                }}
                maskColor="rgba(241,245,249,0.7)"
                style={{ background: '#fff', border: '1px solid #e2e8f0', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}
              />
            </ReactFlow>
          </div>
        )}
      </div>

      {/* Legend */}
      {schema && view === 'graph' && (
        <div style={{
          height: '36px', flexShrink: 0,
          display: 'flex', alignItems: 'center', gap: '20px',
          padding: '0 24px',
          background: '#fff', borderTop: '1px solid #e2e8f0',
          fontSize: '12px', color: '#64748b',
        }}>
          <span>🟡 <b style={{ color: '#92400e' }}>PK</b> Primary Key</span>
          <span>🔵 <b style={{ color: '#1d4ed8' }}>FK</b> Foreign Key</span>
          <span><span style={{ color: '#ef4444' }}>*</span> Not Null</span>
          <span style={{ marginLeft: 'auto' }}>Drag to rearrange · Scroll to zoom</span>
        </div>
      )}
    </div>
  )
}