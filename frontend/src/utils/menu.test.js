import { describe, expect, it } from 'vitest';
import { normalizeMenuSections } from './menu.js';

const payload = [{
  id: 'vini',
  title: 'Vini',
  description: '',
  items: [{
    id: 'wine',
    name: 'Vino',
    subtitle: '',
    description: '',
    notes: ['Nota'],
    price: '5 € / 22 €'
  }]
}];

describe('menu normalization', () => {
  it('creates independent sections, items, notes and price options', () => {
    const normalized = normalizeMenuSections(payload);
    expect(normalized).not.toBe(payload);
    expect(normalized[0]).not.toBe(payload[0]);
    expect(normalized[0].items[0]).not.toBe(payload[0].items[0]);
    expect(normalized[0].items[0].notes).not.toBe(payload[0].items[0].notes);
    expect(normalized[0].items[0].price.options.map((option) => option.amount)).toEqual([5, 22]);

    normalized[0].items[0].notes.push('Temporanea');
    normalized[0].items[0].price.options[0].amount = 99;
    expect(payload[0].items[0].notes).toEqual(['Nota']);
    expect(payload[0].items[0].price).toBe('5 € / 22 €');
  });

  it('rejects malformed API prices instead of putting them into state', () => {
    const malformed = structuredClone(payload);
    malformed[0].items[0].price = '5 circa';
    expect(() => normalizeMenuSections(malformed)).toThrow(/malformato/);
  });
});
