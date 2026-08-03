import { describe, expect, it } from 'vitest';
import {
  formatPrice,
  normalizePrice,
  parsePriceDraft,
  priceToDraftInput,
  toPricePayload
} from './price.js';

describe('price normalization', () => {
  it.each([
    [25, [25]],
    [1.11, [1.11]],
    [2.55, [2.55]],
    ['25', [25]],
    ['2,50', [2.5]],
    ['25 €', [25]],
    ['25 \u00e2\u201a\u00ac', [25]],
    ['5 € / 22 €', [5, 22]],
    ['6 € - 24 €', [6, 24]],
    [[5, 22], [5, 22]]
  ])('normalizes %j without losing amounts', (input, amounts) => {
    expect(normalizePrice(input, { sectionId: 'vini' })?.options.map((option) => option.amount))
      .toEqual(amounts);
  });

  it('distinguishes absent, single and multiple prices', () => {
    expect(normalizePrice('-')).toBeNull();
    expect(normalizePrice(null)).toBeNull();
    expect(normalizePrice(5).kind).toBe('single');
    const multiple = normalizePrice('5 € / 22 €', { sectionId: 'vini' });
    expect(multiple.kind).toBe('multiple');
    expect(multiple.options.map((option) => option.label)).toEqual(['glass', 'bottle']);
  });

  it('formats only validated values and never emits NaN', () => {
    expect(formatPrice(2.5)).toBe('2,50 €');
    expect(formatPrice('5 € / 22 €', { sectionId: 'vini' })).toBe('5 € / 22 €');
    expect(formatPrice('-')).toBe('-');
    expect(formatPrice('not a price')).toBe('Prezzo non valido');
    expect(formatPrice('not a price')).not.toContain('NaN');
  });

  it.each([0, -1, Number.NaN, Number.POSITIVE_INFINITY, '5 circa', '5 / x', [], [5, -2], ['5'],
    [{ amount: '5' }]])(
    'rejects invalid or ambiguous value %j',
    (input) => expect(() => normalizePrice(input)).toThrow()
  );

  it('parses admin drafts into numeric options and preserves wine labels', () => {
    const original = normalizePrice('5 € / 22 €', { sectionId: 'vini' });
    const parsed = parsePriceDraft('5,50 / 23', { sectionId: 'vini', originalPrice: original });
    expect(priceToDraftInput(parsed)).toBe('5.5 / 23');
    expect(toPricePayload(parsed)).toEqual({
      options: [
        { label: 'glass', amount: 5.5 },
        { label: 'bottle', amount: 23 }
      ]
    });
    expect(parsePriceDraft('', { sectionId: 'vini', originalPrice: original })).toBeNull();
    expect(() => parsePriceDraft('0', { sectionId: 'vini', originalPrice: original })).toThrow();
    expect(() => parsePriceDraft('5 € / 22 €', { sectionId: 'vini', originalPrice: original })).toThrow();
  });

  it('never converts legacy representations into an admin payload', () => {
    for (const value of [0, '0', '0 €', '5 €', [5, 22]]) {
      expect(() => toPricePayload(value)).toThrow();
    }
    expect(toPricePayload(null)).toBeNull();
  });
});
