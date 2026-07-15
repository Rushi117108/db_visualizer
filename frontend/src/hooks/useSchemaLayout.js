import { useMemo } from 'react'
import { MarkerType } from '@xyflow/react'

const NODE_WIDTH = 260
const NODE_HEIGHT_BASE = 90
const ROW_HEIGHT = 26
const H_GAP = 100
const V_GAP = 60

// Distinct edge colors so overlapping lines are easier to trace
const EDGE_COLORS = [
  '#38bdf8', '#34d399', '#f472b6', '#fb923c',
  '#a78bfa', '#fbbf24', '#22d3ee', '#a3e635',
  '#f87171', '#818cf8', '#4ade80', '#e879f9',
]

function autoLayout(tables) {
  const cols = Math.ceil(Math.sqrt(tables.length)) || 1
  return tables.map((table, i) => {
    const col = i % cols
    const row = Math.floor(i / cols)
    const nodeHeight = NODE_HEIGHT_BASE + (table.columns?.length || 0) * ROW_HEIGHT
    return {
      x: col * (NODE_WIDTH + H_GAP),
      y: row * (nodeHeight + V_GAP),
    }
  })
}

export function useSchemaLayout(schema) {
  return useMemo(() => {
    if (!schema || !schema.tables || schema.tables.length === 0) {
      return { nodes: [], edges: [] }
    }

    const positions = autoLayout(schema.tables)

    const nodes = schema.tables.map((table, i) => ({
      id: table.id,
      type: 'tableNode',
      position: positions[i],
      data: {
        name: table.name,
        columns: table.columns || [],
        pkCount: table.pkCount || 0,
        fkCount: table.fkCount || 0,
        columnCount: table.columnCount || 0,
      },
    }))

    const edges = (schema.relationships || []).map((rel, i) => {
      const color = EDGE_COLORS[i % EDGE_COLORS.length]
      return {
        id: rel.id,
        source: rel.source,
        target: rel.target,
        label: `${rel.sourceColumn} → ${rel.targetColumn}`,
        type: 'smoothstep',
        animated: false,
        style: { stroke: color, strokeWidth: 2 },
        labelStyle: { fill: color, fontSize: 10, fontWeight: 600 },
        labelBgStyle: { fill: '#0a0d14', fillOpacity: 0.9 },
        labelBgPadding: [4, 4],
        labelBgBorderRadius: 4,
        markerEnd: {
          type: MarkerType.ArrowClosed,
          color: color,
          width: 16,
          height: 16,
        },
      }
    })

    return { nodes, edges }
  }, [schema])
}