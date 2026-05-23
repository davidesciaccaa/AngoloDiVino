export function MenuItemCard({ item }) {
  return (
    <article className="menu-item-card">
      <div className="menu-item-card__topline">
        <p>{item.subtitle}</p>
        <strong>{item.price}</strong>
      </div>
      <h4>{item.name}</h4>
      <p className="menu-item-card__description">{item.description}</p>
      <ul className="note-list" aria-label={`Note di ${item.name}`}>
        {item.notes.map((note) => (
          <li key={note}>{note}</li>
        ))}
      </ul>
    </article>
  );
}
