import { useTranslation } from 'react-i18next';
import { formatPrice } from '../utils/price.js';
import { resolveMenuItemText } from '../utils/localizedMenu.js';

export function MenuItemCard({ item, sectionId }) {
  const { t, i18n } = useTranslation();

  const { name, subtitle, description, notes } = resolveMenuItemText(item, { t, i18n });
  const displayedPrice = formatPrice(item.price, { sectionId });

  return (
    <article className="menu-item-editorial">
      {subtitle && (
        <p className="menu-item-editorial__subtitle">{subtitle}</p>
      )}
      
      <div className="menu-item-editorial__header">
        <h4>{name}</h4>
        <span className="menu-item-editorial__price">{displayedPrice}</span>
      </div>

      {description && (
        <p className="menu-item-editorial__description">{description}</p>
      )}

      {notes && notes.length > 0 && (
        <ul className="note-list-editorial" aria-label={`Note di ${name}`}>
          {notes.map((note) => (
            <li key={note}>{note}</li>
          ))}
        </ul>
      )}
    </article>
  );
}
