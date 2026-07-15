import React from 'react'
import { Handle, Position } from '@xyflow/react'

const TABLE_THEMES = {
  user:    { header: '#2563eb', light: '#eff6ff', accent: '#3b82f6', icon: '👤' },
  account: { header: '#2563eb', light: '#eff6ff', accent: '#3b82f6', icon: '👤' },
  order:   { header: '#d97706', light: '#fffbeb', accent: '#f59e0b', icon: '🛒' },
  payment: { header: '#059669', light: '#ecfdf5', accent: '#10b981', icon: '💳' },
  product: { header: '#7c3aed', light: '#f5f3ff', accent: '#8b5cf6', icon: '📦' },
  categor: { header: '#db2777', light: '#fdf2f8', accent: '#ec4899', icon: '🏷️' },
  review:  { header: '#ea580c', light: '#fff7ed', accent: '#f97316', icon: '⭐' },
  address: { header: '#0891b2', light: '#ecfeff', accent: '#06b6d4', icon: '📍' },
  coupon:  { header: '#65a30d', light: '#f7fee7', accent: '#84cc16', icon: '🎟️' },
  role:    { header: '#dc2626', light: '#fef2f2', accent: '#ef4444', icon: '🔐' },
  log:     { header: '#475569', light: '#f8fafc', accent: '#64748b', icon: '📋' },
  default: { header: '#4f46e5', light: '#eef2ff', accent: '#6366f1', icon: '🗄️' },
}

function getTheme(tableName) {
  const lower = tableName.toLowerCase()
  for (const [key, theme] of Object.entries(TABLE_THEMES)) {
    if (lower.includes(key)) return theme
  }
  return TABLE_THEMES.default
}

const TYPE_COLORS = {
  INT: '#2563eb', INTEGER: '#2563eb', BIGINT: '#2563eb', SMALLINT: '#2563eb', TINYINT: '#2563eb',
  VARCHAR: '#059669', TEXT: '#059669', CHAR: '#059669', LONGTEXT: '#059669',
  BOOLEAN: '#db2777', BOOL: '#db2777',
  DECIMAL: '#d97706', NUMERIC: '#d97706', FLOAT: '#d97706', DOUBLE: '#d97706',
  DATE: '#7c3aed', DATETIME: '#7c3aed', TIMESTAMP: '#7c3aed', TIME: '#7c3aed',
}

function getTypeColor(type) {
  const base = type?.split('(')[0]?.toUpperCase()
  return TYPE_COLORS[base] || '#64748b'
}

export default function TableNode({ data, selected }) {
  const theme = getTheme(data.name)

  return (
    <div style={{
      background: '#ffffff',
      border: `2px solid ${selected ? theme.header : '#e2e8f0'}`,
      borderRadius: '12px',
      minWidth: '240px',
      maxWidth: '300px',
      boxShadow: selected
        ? `0 0 0 3px ${theme.accent}33, 0 8px 30px rgba(0,0,0,0.12)`
        : '0 2px 12px rgba(0,0,0,0.08)',
      transition: 'all 0.2s ease',
      overflow: 'hidden',
      fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
    }}>
      <Handle
        type="target"
        position={Position.Left}
        style={{ background: theme.header, width: 10, height: 10, border: '2px solid #fff' }}
      />

      {/* Colored header bar */}
      <div style={{
        background: theme.header,
        padding: '10px 14px',
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
      }}>
        <span style={{ fontSize: '16px' }}>{theme.icon}</span>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{
            fontWeight: 700, fontSize: '13px', color: '#fff',
            letterSpacing: '0.2px',
            whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis',
          }}>
            {data.name}
          </div>
          <div style={{ fontSize: '10px', color: 'rgba(255,255,255,0.75)', marginTop: '1px' }}>
            {data.columnCount} cols · {data.pkCount} PK · {data.fkCount} FK
          </div>
        </div>
      </div>

      {/* Columns */}
      <div style={{ background: '#fff' }}>
        {data.columns?.map((col, i) => (
          <div key={i} style={{
            display: 'flex',
            alignItems: 'center',
            padding: '5px 12px',
            gap: '7px',
            background: col.primaryKey
              ? '#fffbeb'
              : col.foreignKey
              ? theme.light
              : '#fff',
            borderBottom: i < data.columns.length - 1 ? '1px solid #f1f5f9' : 'none',
          }}>
            {/* PK / FK badge */}
            <span style={{
              fontSize: '9px', fontWeight: 800,
              padding: '1px 5px', borderRadius: '4px',
              minWidth: '22px', textAlign: 'center', flexShrink: 0,
              background: col.primaryKey ? '#fbbf24' : col.foreignKey ? theme.header : 'transparent',
              color: col.primaryKey ? '#78350f' : col.foreignKey ? '#fff' : 'transparent',
            }}>
              {col.primaryKey ? 'PK' : col.foreignKey ? 'FK' : '  '}
            </span>

            {/* Column name */}
            <span style={{
              flex: 1, fontSize: '12px',
              color: col.primaryKey ? '#92400e' : col.foreignKey ? theme.header : '#334155',
              fontWeight: col.primaryKey || col.foreignKey ? 600 : 400,
              whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis',
            }}>
              {col.name}
              {!col.nullable && (
                <span style={{ color: '#ef4444', marginLeft: '2px', fontSize: '10px' }}>*</span>
              )}
            </span>

            {/* Type */}
            <span style={{
              fontSize: '10px', color: getTypeColor(col.type),
              fontFamily: 'monospace', fontWeight: 500, flexShrink: 0,
            }}>
              {col.type}
            </span>
          </div>
        ))}
      </div>

      <Handle
        type="source"
        position={Position.Right}
        style={{ background: theme.header, width: 10, height: 10, border: '2px solid #fff' }}
      />
    </div>
  )
}