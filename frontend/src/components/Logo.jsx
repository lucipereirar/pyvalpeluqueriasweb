import './Logo.css'

// Logo de Pyval Peluquerías: mariposa de línea + "PYVAL" (serif) +
// "Peluquerías" (script). Recreado como SVG + fuentes para que sea
// nítido, escalable y recoloreable (hereda currentColor).
// variant: 'nav' (compacto horizontal) | 'hero' (grande centrado).
export default function Logo({ variant = 'nav' }) {
  return (
    <span className={`logo logo--${variant}`} aria-label="Pyval Peluquerías">
      <Mariposa />
      <span className="logo__texto">
        <span className="logo__pyval">PYVAL</span>
        <span className="logo__pelu">Peluquerías</span>
      </span>
    </span>
  )
}

function Mariposa() {
  // Mariposa de línea vista de costado, con las alas abiertas hacia la
  // derecha y las antenas curvándose hacia la izquierda.
  return (
    <svg
      className="logo__mariposa"
      viewBox="0 0 210 180"
      fill="none"
      stroke="currentColor"
      strokeWidth="2.2"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      {/* antenas */}
      <path d="M96 104 C 74 86, 52 80, 34 84" />
      <path d="M96 104 C 88 80, 80 60, 86 40" />
      <circle cx="33" cy="84" r="2.6" fill="currentColor" stroke="none" />
      <circle cx="86" cy="39" r="2.6" fill="currentColor" stroke="none" />
      {/* cuerpo / abdomen */}
      <path d="M95 102 C 99 116, 101 126, 98 138" />
      {/* ala superior (grande, hacia arriba-derecha, punta marcada) */}
      <path d="M96 100 C 112 42, 158 14, 194 26 C 208 54, 158 86, 104 104" />
      {/* ala inferior (redondeada, hacia abajo-derecha) */}
      <path d="M104 106 C 142 108, 180 124, 178 152 C 160 172, 118 150, 96 104" />
      {/* nervadura sutil del ala superior */}
      <path d="M106 100 C 136 74, 162 52, 186 40" opacity="0.4" />
    </svg>
  )
}
