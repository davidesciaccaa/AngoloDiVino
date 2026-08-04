import { normalizePrice } from './price.js';

function requiredString(value, field) {
  if (typeof value !== 'string') {
    throw new TypeError(`Campo menu non valido: ${field}`);
  }
  return value;
}

function normalizeTranslations(value) {
  if (value == null) return {};
  if (typeof value !== 'object' || Array.isArray(value)) {
    throw new TypeError('Traduzioni menu non valide.');
  }
  return Object.fromEntries(['en', 'de'].filter((language) => value[language] != null).map((language) => {
    const translated = value[language];
    if (typeof translated !== 'object' || !Array.isArray(translated.notes ?? [])) {
      throw new TypeError(`Traduzione ${language} non valida.`);
    }
    return [language, {
      name: requiredString(translated.name ?? '', `translations.${language}.name`),
      subtitle: requiredString(translated.subtitle ?? '', `translations.${language}.subtitle`),
      description: requiredString(translated.description ?? '', `translations.${language}.description`),
      notes: (translated.notes ?? []).map((note) => requiredString(note, `translations.${language}.notes`))
    }];
  }));
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
          price: normalizePrice(item.price, { sectionId }),
          translations: normalizeTranslations(item.translations)
        };
      })
    };
  });
}
