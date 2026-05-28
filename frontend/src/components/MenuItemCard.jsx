export function MenuItemCard({ item }) {
  return (
    <article className="menu-item-editorial">
      {item.subtitle && (
        <p className="menu-item-editorial__subtitle">{item.subtitle}</p>
      )}
      
      <div className="menu-item-editorial__header">
        <h4>{item.name}</h4>
        <span className="menu-item-editorial__price">{item.price}</span>
      </div>

      {item.description && (
        <p className="menu-item-editorial__description">{item.description}</p>
      )}

      {item.notes && item.notes.length > 0 && (
        <ul className="note-list-editorial" aria-label={`Note di ${item.name}`}>
          {item.notes.map((note) => (
            <li key={note}>{note}</li>
          ))}
        </ul>
      )}
    </article>
  );
}
