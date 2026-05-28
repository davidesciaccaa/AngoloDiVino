export function MenuItemCard({ item }) {
  return (
    <article className="menu-item-compact">
      <div className="menu-item-compact__main">
        <h4>{item.name}</h4>
        <div className="menu-item-compact__dots"></div>
        <span className="menu-item-compact__price">{item.price}</span>
      </div>

      {(item.description || item.subtitle) && (
        <p className="menu-item-compact__description">
          {item.subtitle ? <strong>{item.subtitle}. </strong> : ''}
          {item.description}
        </p>
      )}

      {item.notes && item.notes.length > 0 && (
        <ul className="note-list-minimal" aria-label={`Note di ${item.name}`}>
          {item.notes.map((note) => (
            <li key={note}>{note}</li>
          ))}
        </ul>
      )}
    </article>
  );
}
