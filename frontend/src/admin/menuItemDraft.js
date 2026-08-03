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
    priceInput: ''
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
    priceInput: priceToDraftInput(price, { sectionId })
  };
}

export function cloneMenuItemDraft(draft) {
  return {
    ...draft,
    notes: [...draft.notes],
    price: clonePrice(draft.price, { sectionId: draft.sectionId })
  };
}

export function menuItemCommandFromDraft(draft) {
  const price = parsePriceDraft(draft.priceInput, {
    sectionId: draft.sectionId,
    originalPrice: draft.price
  });
  return {
    sectionId: draft.sectionId,
    name: draft.name.trim(),
    subtitle: draft.subtitle.trim(),
    description: draft.description.trim(),
    notes: draft.notesInput.split('\n').map((note) => note.trim()).filter(Boolean),
    price: toPricePayload(price, { sectionId: draft.sectionId })
  };
}
