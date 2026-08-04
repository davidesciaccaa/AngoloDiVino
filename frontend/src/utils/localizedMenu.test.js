import { describe, expect, it } from 'vitest';
import { normalizeMenuLanguage, resolveMenuItemText } from './localizedMenu.js';

const item = {
  id: 'vino', name: 'Vino', subtitle: 'Bianchi', description: 'Descrizione italiana', notes: ['Fresco'],
  translations: {
    en: { name: 'Dynamic wine', subtitle: 'White', description: 'Dynamic description', notes: ['Fresh'] },
    de: { name: 'Dynamischer Wein', subtitle: 'Weiß', description: 'Dynamische Beschreibung', notes: ['Frisch'] }
  }
};
const legacy = {
  'menu.items.vino.name': 'Legacy wine',
  'menu.items.vino.description': 'Legacy description',
  'menu.items.vini_labels.bianchi': 'Legacy white',
  'menu.items.vino.notes.fresco': 'Legacy fresh'
};
const t = (key, options) => legacy[key] ?? options?.defaultValue ?? '';
const i18n = { language: 'it', getResourceBundle: () => ({}) };

describe('dynamic menu localization', () => {
  it.each(['it', 'it-IT'])('always renders API Italian for %s', (language) => {
    expect(resolveMenuItemText(item, { language, t, i18n })).toEqual({
      name: 'Vino', subtitle: 'Bianchi', description: 'Descrizione italiana', notes: ['Fresco']
    });
  });

  it.each([
    ['en', 'Dynamic wine'], ['en-US', 'Dynamic wine'], ['en-GB', 'Dynamic wine'],
    ['de', 'Dynamischer Wein'], ['de-DE', 'Dynamischer Wein']
  ])('prefers dynamic translations for %s', (language, name) => {
    expect(resolveMenuItemText(item, { language, t, i18n }).name).toBe(name);
  });

  it('falls back from dynamic to legacy and finally Italian per field', () => {
    const partial = { ...item, translations: { en: { name: '', notes: [] } } };
    expect(resolveMenuItemText(partial, { language: 'en', t, i18n })).toEqual({
      name: 'Legacy wine', subtitle: 'Legacy white', description: 'Legacy description', notes: ['Legacy fresh']
    });
    const noLegacy = resolveMenuItemText({ ...partial, id: 'unknown' }, {
      language: 'en-US', t: (_key, options) => options?.defaultValue ?? '', i18n
    });
    expect(noLegacy.name).toBe('Vino');
    expect(noLegacy.description).toBe('Descrizione italiana');
  });

  it('normalizes regional and unsupported language tags', () => {
    expect(['en-US', 'en-GB', 'de-DE', 'it-IT'].map(normalizeMenuLanguage)).toEqual(['en', 'en', 'de', 'it']);
    expect(normalizeMenuLanguage('fr-FR')).toBe('it');
  });
});
