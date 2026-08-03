const LEGACY_MOJIBAKE_EURO = '\u00e2\u201a\u00ac';
const LEGACY_AMOUNT = /^\d+(?:[.,]\d{1,2})?\s*€?$/;
const DRAFT_AMOUNT = /^\d+(?:[.,]\d{1,2})?$/;
const MAX_AMOUNT = 9999.99;
const VALID_LABELS = new Set(['glass', 'bottle']);

function normalizeAmount(value) {
  if (typeof value !== 'number') {
    throw new TypeError(`L'importo strutturato deve essere numerico: ${String(value)}`);
  }
  const amount = value;
  if (!Number.isFinite(amount) || amount <= 0 || amount > MAX_AMOUNT) {
    throw new TypeError(`Importo prezzo non valido: ${String(value)}`);
  }
  const cents = amount * 100;
  if (Math.abs(Math.round(cents) - cents) > 1e-8) {
    throw new TypeError(`Il prezzo può avere al massimo due decimali: ${String(value)}`);
  }
  return amount;
}

function labelOptions(options, sectionId) {
  const hasLabels = options.some((option) => option.label);
  if (sectionId && sectionId !== 'vini' && hasLabels) {
    throw new TypeError('Le etichette glass/bottle sono valide solo per i vini.');
  }
  if (sectionId !== 'vini' || options.length !== 2) {
    return options;
  }
  if (hasLabels) {
    if (options[0].label !== 'glass' || options[1].label !== 'bottle') {
      throw new TypeError('L’ordine dei prezzi dei vini deve essere glass, bottle.');
    }
    return options;
  }
  return [
    { ...options[0], label: 'glass' },
    { ...options[1], label: 'bottle' }
  ];
}

function priceFromOptions(rawOptions, sectionId) {
  if (!Array.isArray(rawOptions) || rawOptions.length === 0 || rawOptions.length > 8) {
    throw new TypeError('Il prezzo deve contenere da 1 a 8 importi.');
  }
  const options = rawOptions.map((option) => {
    const source = typeof option === 'object' && option !== null && !Array.isArray(option)
      ? option
      : { amount: option };
    const label = source.label ?? null;
    if (label !== null && !VALID_LABELS.has(label)) {
      throw new TypeError(`Etichetta prezzo non valida: ${String(label)}`);
    }
    return { ...(label ? { label } : {}), amount: normalizeAmount(source.amount) };
  });
  const labels = options.map((option) => option.label).filter(Boolean);
  if (new Set(labels).size !== labels.length) {
    throw new TypeError('Le etichette dei prezzi non possono essere duplicate.');
  }
  const labeled = labelOptions(options, sectionId);
  return {
    kind: labeled.length === 1 ? 'single' : 'multiple',
    options: labeled.map((option) => ({ ...option }))
  };
}

function parseLegacyPrice(raw, sectionId) {
  const value = raw.trim().replaceAll(LEGACY_MOJIBAKE_EURO, '€');
  if (value === '-') return null;

  let parts;
  if (value.includes('/')) {
    parts = value.split('/');
  } else if (/\s+-\s+/.test(value)) {
    parts = value.split(/\s+-\s+/);
  } else {
    parts = [value];
  }

  const options = parts.map((part) => {
    const amount = part.trim();
    if (!LEGACY_AMOUNT.test(amount)) {
      throw new TypeError(`Prezzo legacy ambiguo o malformato: ${raw}`);
    }
    return { amount: Number(amount.replace('€', '').trim().replace(',', '.')) };
  });
  return priceFromOptions(options, sectionId);
}

export function normalizePrice(value, { sectionId } = {}) {
  if (value === null || value === undefined) return null;
  if (typeof value === 'string') return parseLegacyPrice(value, sectionId);
  if (typeof value === 'number') return priceFromOptions([value], sectionId);
  if (Array.isArray(value)) return priceFromOptions(value, sectionId);
  if (typeof value === 'object' && Array.isArray(value.options)) {
    return priceFromOptions(value.options, sectionId);
  }
  throw new TypeError(`Formato prezzo non supportato: ${String(value)}`);
}

export function clonePrice(value, { sectionId } = {}) {
  const normalized = normalizePrice(value, { sectionId });
  return normalized
    ? { kind: normalized.kind, options: normalized.options.map((option) => ({ ...option })) }
    : null;
}

export function formatPrice(value, { locale = 'it-IT', sectionId } = {}) {
  let normalized;
  try {
    normalized = normalizePrice(value, { sectionId });
  } catch {
    return 'Prezzo non valido';
  }
  if (!normalized) return '-';

  return normalized.options
    .map((option) => {
      const formatter = new Intl.NumberFormat(locale, {
        minimumFractionDigits: Number.isInteger(option.amount) ? 0 : 2,
        maximumFractionDigits: 2
      });
      return `${formatter.format(option.amount)} €`;
    })
    .join(' / ');
}

export function priceToDraftInput(value, { sectionId } = {}) {
  const normalized = normalizePrice(value, { sectionId });
  return normalized ? normalized.options.map((option) => String(option.amount)).join(' / ') : '';
}

export function parsePriceDraft(input, { sectionId, originalPrice } = {}) {
  const value = input.trim();
  if (value === '' || value === '-') return null;
  if (/\s+-\s+/.test(value) || value.includes('€') || value.includes(LEGACY_MOJIBAKE_EURO)) {
    throw new TypeError('Nel form usa solo importi numerici separati da /.');
  }

  const parts = value.split('/');
  const original = normalizePrice(originalPrice, { sectionId });
  const options = parts.map((part, index) => {
    const amount = part.trim();
    if (!DRAFT_AMOUNT.test(amount)) {
      throw new TypeError('Inserisci importi positivi con al massimo due decimali.');
    }
    const label = sectionId === 'vini' ? original?.options[index]?.label : null;
    return { ...(label ? { label } : {}), amount: Number(amount.replace(',', '.')) };
  });
  return priceFromOptions(options, sectionId);
}

export function toPricePayload(value, { sectionId } = {}) {
  if (value === null || value === undefined) return null;
  if (typeof value !== 'object' || Array.isArray(value) || !Array.isArray(value.options)) {
    throw new TypeError('Il payload admin richiede un prezzo strutturato.');
  }
  const normalized = priceFromOptions(value.options, sectionId);
  return normalized
    ? { options: normalized.options.map((option) => ({ ...option })) }
    : null;
}
