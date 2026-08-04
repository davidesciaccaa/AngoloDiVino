import { clonePrice, parsePriceDraft, priceToDraftInput, toPricePayload } from '../utils/price.js';

export function createEmptyMenuItemDraft(sectionId = '') {
  return {
    sectionId,
    name: '',
    subtitle: '',
    description: '',
    notes: [],
    notesInput: '',
    price: null,
    priceInput: '',
    translations: emptyTranslations(),
    autoTranslate: true
  };
}

export function createMenuItemDraft(item, sectionId) {
  const price = clonePrice(item.price, { sectionId });
  return {
    id: item.id,
    sectionId,
    name: item.name,
    subtitle: item.subtitle,
    description: item.description,
    notes: [...item.notes],
    notesInput: item.notes.join('\n'),
    price,
    priceInput: priceToDraftInput(price, { sectionId }),
    translations: draftTranslations(item.translations),
    autoTranslate: false
  };
}

export function cloneMenuItemDraft(draft) {
  return {
    ...draft,
    notes: [...draft.notes],
    price: clonePrice(draft.price, { sectionId: draft.sectionId }),
    translations: structuredClone(draft.translations)
  };
}

export function menuItemCommandFromDraft(draft) {
  const price = parsePriceDraft(draft.priceInput, {
    sectionId: draft.sectionId,
    originalPrice: draft.price
  });
  const command = {
    sectionId: draft.sectionId,
    name: draft.name.trim(),
    subtitle: draft.subtitle.trim(),
    description: draft.description.trim(),
    notes: draft.notesInput.split('\n').map((note) => note.trim()).filter(Boolean),
    price: toPricePayload(price, { sectionId: draft.sectionId }),
    autoTranslate: draft.autoTranslate
  };
  if (!draft.autoTranslate) {
    const noteCount = command.notes.length;
    command.translations = Object.fromEntries(['en', 'de'].map((language) => [language, {
      name: draft.translations[language].name.trim(),
      subtitle: draft.translations[language].subtitle.trim(),
      description: draft.translations[language].description.trim(),
      notes: noteCount === 0 ? [] : Array.from({ length: noteCount }, (_, index) =>
        (draft.translations[language].notesInput.split('\n')[index] ?? '').trim())
    }]));
  }
  return command;
}

function emptyTranslation() {
  return { name: '', subtitle: '', description: '', notesInput: '' };
}

function emptyTranslations() {
  return { en: emptyTranslation(), de: emptyTranslation() };
}

function draftTranslations(translations = {}) {
  return Object.fromEntries(['en', 'de'].map((language) => {
    const value = translations[language] ?? {};
    return [language, {
      name: value.name ?? '',
      subtitle: value.subtitle ?? '',
      description: value.description ?? '',
      notesInput: Array.isArray(value.notes) ? value.notes.join('\n') : ''
    }];
  }));
}
