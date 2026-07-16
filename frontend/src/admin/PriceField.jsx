import { useEffect, useRef, useState } from 'react';

// Mirrors the backend rule, so a bad price is caught before "Salva tutto".
const PRICE_PATTERN = /^[0-9 ,./€-]{1,32}$/;

export function PriceField({ value, isDirty, itemName, subcategory, sectionTitle, onChange }) {
  const [isEditing, setIsEditing] = useState(false);
  const [draft, setDraft] = useState(value);
  const [error, setError] = useState(null);
  const inputRef = useRef(null);

  useEffect(() => {
    if (isEditing) {
      inputRef.current?.focus();
      inputRef.current?.select();
    }
  }, [isEditing]);

  const context = [sectionTitle, subcategory].filter(Boolean).join(' · ');

  function startEditing() {
    setDraft(value);
    setError(null);
    setIsEditing(true);
  }

  function commit({ refocusOnError = true } = {}) {
    const trimmed = draft.trim();
    if (!PRICE_PATTERN.test(trimmed)) {
      // Stay in edit mode with the error visible rather than discard what was typed.
      setError('Usa solo cifre, spazi e i simboli € , . / -');
      if (refocusOnError) {
        inputRef.current?.focus();
      }
      return;
    }

    onChange(trimmed);
    setIsEditing(false);
    setError(null);
  }

  function cancel() {
    setDraft(value);
    setError(null);
    setIsEditing(false);
  }

  function handleKeyDown(event) {
    if (event.key === 'Enter') {
      event.preventDefault();
      commit();
    } else if (event.key === 'Escape') {
      event.preventDefault();
      cancel();
    }
  }

  if (!isEditing) {
    return (
      <button
        type="button"
        className={`admin-price ${isDirty ? 'admin-price--dirty' : ''}`}
        onClick={startEditing}
        aria-label={`Modifica il prezzo di ${itemName}${context ? ` (${context})` : ''}. Prezzo attuale ${value}`}
      >
        <span className="admin-price__value">{value}</span>
        <svg className="admin-price__pencil" viewBox="0 0 16 16" aria-hidden="true" focusable="false">
          <path
            d="M11.4 1.9 14.1 4.6l-8.1 8.1-3.4.7.7-3.4 8.1-8.1z"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.3"
            strokeLinejoin="round"
          />
        </svg>
      </button>
    );
  }

  return (
    <div className="admin-price-editor">
      <p className="admin-price-editor__context">
        {context && <span className="admin-price-editor__context-path">{context} ·</span>} {itemName}
      </p>

      <div className="admin-price-editor__row">
        <input
          ref={inputRef}
          className={`admin-price-editor__input ${error ? 'admin-price-editor__input--invalid' : ''}`}
          type="text"
          inputMode="decimal"
          value={draft}
          aria-label={`Prezzo di ${itemName}`}
          aria-invalid={Boolean(error)}
          onChange={(event) => {
            setDraft(event.target.value);
            setError(null);
          }}
          onKeyDown={handleKeyDown}
          // Clicking away keeps the typed value; the ✕ button cancels (it blocks blur first).
          onBlur={() => commit({ refocusOnError: false })}
        />
        <button
          type="button"
          className="admin-icon-button admin-icon-button--confirm"
          onMouseDown={(event) => event.preventDefault()}
          onClick={commit}
          aria-label={`Salva il prezzo di ${itemName}`}
        >
          ✓
        </button>
        <button
          type="button"
          className="admin-icon-button"
          onMouseDown={(event) => event.preventDefault()}
          onClick={cancel}
          aria-label={`Annulla la modifica di ${itemName}`}
        >
          ✕
        </button>
      </div>

      {error && <p className="admin-price-editor__error" role="alert">{error}</p>}
    </div>
  );
}
