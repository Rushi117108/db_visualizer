import React from 'react'

export default function StatsBar({ schema }) {
  if (!schema) return null

  const stats = [
    { label: 'Tables', value: schema.totalTables, icon: '🗄️', color: '#4f46e5', bg: '#eef2ff' },
    { label: 'Relationships', value: schema.totalRelationships, icon: '🔗', color: '#059669', bg: '#ecfdf5' },
    { label: 'Dialect', value: schema.dialect, icon: '💬', color: '#d97706', bg: '#fffbeb' },
  ]

  return (
    <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
      {stats.map((s, i) => (
        <div key={i} style={{
          background: s.bg,
          border: `1px solid ${s.color}22`,
          borderRadius: '8px',
          padding: '6px 14px',
          display: 'flex', alignItems: 'center', gap: '8px',
        }}>
          <span style={{ fontSize: '15px' }}>{s.icon}</span>
          <div>
            <div style={{ fontSize: '16px', fontWeight: 700, color: s.color, lineHeight: 1.2 }}>{s.value}</div>
            <div style={{ fontSize: '10px', color: '#94a3b8', textTransform: 'uppercase', letterSpacing: '0.5px' }}>{s.label}</div>
          </div>
        </div>
      ))}
      {schema.parseWarning && (
        <div style={{
          background: '#fffbeb', border: '1px solid #fde68a',
          borderRadius: '8px', padding: '6px 14px',
          fontSize: '12px', color: '#92400e',
        }}>
          ⚠️ {schema.parseWarning}
        </div>
      )}
    </div>
  )
}