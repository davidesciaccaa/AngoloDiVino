import { normalizePrice } from './price.js';

function requiredString(value, field) {
  if (typeof value !== 'string') {
    throw new TypeError(`Campo menu non valido: ${field}`);
  }
  return value;
}

export function normalizeMenuSections(payload) {
  if (!Array.isArray(payload)) {
    throw new TypeError('La risposta del menu deve essere un array.');
  }
  return payload.map((section) => {
    if (!section || !Array.isArray(section.items)) {
      throw new TypeError('Sezione menu non valida.');
    }
    const sectionId = requiredString(section.id, 'section.id');
    return {
      id: sectionId,
      title: requiredString(section.title, 'section.title'),
      description: requiredString(section.description ?? '', 'section.description'),
      items: section.items.map((item) => {
        if (!item || !Array.isArray(item.notes)) {
          throw new TypeError('Piatto menu non valido.');
        }
        return {
          id: requiredString(item.id, 'item.id'),
          name: requiredString(item.name, 'item.name'),
          subtitle: requiredString(item.subtitle ?? '', 'item.subtitle'),
          description: requiredString(item.description ?? '', 'item.description'),
          notes: item.notes.map((note) => requiredString(note, 'item.notes')),
          price: normalizePrice(item.price, { sectionId })
        };
      })
    };
  });
}
