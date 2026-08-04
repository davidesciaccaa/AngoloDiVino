import { useCallback, useEffect, useState } from 'react';
import {
  adminLogout,
  backfillAdminMenuTranslations,
  createAdminMenuItem,
  deleteAdminMenuItem,
  fetchAdminMenuSections,
  updateAdminMenuItem
} from '../api/adminApi.js';
import { formatPrice } from '../utils/price.js';
import {
  cloneMenuItemDraft,
  createEmptyMenuItemDraft,
  createMenuItemDraft,
  menuItemCommandFromDraft
} from './menuItemDraft.js';

function LocalizedFields({ language, title, value, disabled, onChange }) {
  const change = (field) => (event) => onChange(language, field, event.target.value);
  return (
    <fieldset className="admin-language-fields" disabled={disabled}>
      <legend>{title}</legend>
      <label className="admin-field">
        <span className="admin-field__label">Nome</span>
        <input maxLength="120" className="admin-field__input" value={value.name} onChange={change('name')} />
      </label>
      <label className="admin-field">
        <span className="admin-field__label">Sottotitolo</span>
        <input maxLength="80" className="admin-field__input" value={value.subtitle} onChange={change('subtitle')} />
      </label>
      <label className="admin-field">
        <span className="admin-field__label">Descrizione</span>
        <textarea maxLength="1000" className="admin-field__input" value={value.description} onChange={change('description')} />
      </label>
      <label className="admin-field">
        <span className="admin-field__label">Note (una per ogni nota italiana)</span>
        <textarea className="admin-field__input" value={value.notesInput} onChange={change('notesInput')} />
      </label>
    </fieldset>
  );
}

function ItemForm({ sections, initial, onSave, onCancel, saving }) {
  const [form, setForm] = useState(() => cloneMenuItemDraft(initial));
  const [error, setError] = useState('');
  const set = (key) => (event) => {
    setError('');
    setForm((old) => ({ ...old, [key]: event.target.value }));
  };
  const setTranslation = (language, field, value) => {
    setError('');
    setForm((old) => ({
      ...old,
      translations: {
        ...old.translations,
        [language]: { ...old.translations[language], [field]: value }
      }
    }));
  };

  useEffect(() => {
    function cancelOnEscape(event) {
      if (event.key === 'Escape' && !saving) {
        event.preventDefault();
        onCancel();
      }
    }
    window.addEventListener('keydown', cancelOnEscape);
    return () => window.removeEventListener('keydown', cancelOnEscape);
  }, [onCancel, saving]);

  function submit(event) {
    event.preventDefault();
    try {
      onSave(menuItemCommandFromDraft(form));
    } catch (submitError) {
      setError(submitError.message);
    }
  }

  return (
    <form className="admin-item-form" onSubmit={submit}>
      <label className="admin-field">
        <span className="admin-field__label">Sezione</span>
        <select className="admin-field__input" value={form.sectionId} onChange={set('sectionId')}>
          {sections.map((section) => <option key={section.id} value={section.id}>{section.title}</option>)}
        </select>
      </label>
      <fieldset className="admin-language-fields">
        <legend>Italiano</legend>
      <label className="admin-field">
        <span className="admin-field__label">Nome</span>
        <input required maxLength="120" className="admin-field__input" value={form.name} onChange={set('name')} />
      </label>
      <label className="admin-field">
        <span className="admin-field__label">Categoria / sottocategoria</span>
        <input maxLength="80" className="admin-field__input" value={form.subtitle} onChange={set('subtitle')} />
      </label>
      <label className="admin-field">
        <span className="admin-field__label">Descrizione</span>
        <textarea maxLength="1000" className="admin-field__input" value={form.description} onChange={set('description')} />
      </label>
      <label className="admin-field">
        <span className="admin-field__label">Note (una per riga)</span>
        <textarea className="admin-field__input" value={form.notesInput} onChange={set('notesInput')} />
      </label>
      </fieldset>
      <label className="admin-translation-toggle">
        <input
          type="checkbox"
          checked={form.autoTranslate}
          onChange={(event) => setForm((old) => ({ ...old, autoTranslate: event.target.checked }))}
        />
        <span>Traduci automaticamente dall&apos;italiano</span>
      </label>
      {form.autoTranslate && <small>English e Deutsch saranno generati al salvataggio.</small>}
      <LocalizedFields language="en" title="English" value={form.translations.en} disabled={form.autoTranslate} onChange={setTranslation} />
      <LocalizedFields language="de" title="Deutsch" value={form.translations.de} disabled={form.autoTranslate} onChange={setTranslation} />
      <label className="admin-field">
        <span className="admin-field__label">Prezzo</span>
        <span className="admin-price-input">
          <input
            inputMode="decimal"
            className="admin-field__input"
            value={form.priceInput}
            onChange={set('priceInput')}
            aria-describedby="price-help"
          />
          <span aria-hidden="true">€</span>
        </span>
        <small id="price-help">
          Un importo, oppure più importi separati da /. Lascia vuoto per nessun prezzo.
        </small>
      </label>
      {error && <p className="admin-feedback admin-feedback--error" role="alert">{error}</p>}
      <div className="admin-form-actions">
        <button className="admin-button admin-button--primary" disabled={saving}>
          {saving ? (form.autoTranslate ? 'Traduzione in corso…' : 'Salvataggio…') : 'Salva piatto'}
        </button>
        <button type="button" className="admin-button admin-button--ghost" onClick={onCancel} disabled={saving}>
          Annulla
        </button>
      </div>
    </form>
  );
}

export function AdminMenuEditor({ onSignedOut }) {
  const [sections, setSections] = useState([]);
  const [state, setState] = useState('loading');
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);
  const [backfilling, setBackfilling] = useState(false);
  const [feedback, setFeedback] = useState(null);

  const load = useCallback(async () => {
    try {
      setState('loading');
      setSections(await fetchAdminMenuSections());
      setState('ready');
    } catch (error) {
      if (error.status === 401) {
        onSignedOut();
      } else {
        setFeedback({ type: 'error', message: error.message });
        setState('error');
      }
    }
  }, [onSignedOut]);

  useEffect(() => {
    load();
  }, [load]);

  async function save(data) {
    setSaving(true);
    setFeedback(null);
    try {
      const updated = editing?.id
        ? await updateAdminMenuItem(editing.id, data)
        : await createAdminMenuItem(data);
      setSections(updated);
      setEditing(null);
      setFeedback({
        type: 'success',
        message: data.autoTranslate
          ? 'Traduzione completata. Il sito pubblico mostra subito le modifiche.'
          : 'Traduzione manuale salvata. Il sito pubblico mostra subito le modifiche.'
      });
    } catch (error) {
      setFeedback({ type: 'error', message: error.message });
    } finally {
      setSaving(false);
    }
  }

  async function remove(item) {
    if (!window.confirm(`Eliminare “${item.name}”?`)) return;
    try {
      setSections(await deleteAdminMenuItem(item.id));
      setFeedback({ type: 'success', message: 'Piatto eliminato.' });
    } catch (error) {
      setFeedback({ type: 'error', message: error.message });
    }
  }

  async function backfill() {
    if (backfilling || !window.confirm('Generare soltanto le traduzioni mancanti senza sovrascrivere quelle esistenti?')) return;
    setBackfilling(true);
    setFeedback(null);
    try {
      const result = await backfillAdminMenuTranslations();
      setSections(result.sections);
      setFeedback({
        type: 'success',
        message: `Traduzioni generate per ${result.updatedItems} voci; ${result.completeItems} erano già complete.`
      });
    } catch (error) {
      setFeedback({ type: 'error', message: error.message });
    } finally {
      setBackfilling(false);
    }
  }

  const cancelEditing = useCallback(() => setEditing(null), []);

  return (
    <main className="admin-shell">
      <div className="admin-topbar-wrapper">
        <header className="admin-topbar">
          <div>
            <p className="admin-eyebrow">L&apos;Angolo diVino</p>
            <h1 className="admin-topbar__title">Gestione menù</h1>
          </div>
          <div className="admin-topbar__actions">
            <button
              className="admin-button admin-button--ghost"
              onClick={backfill}
              disabled={backfilling || saving}
            >
              {backfilling ? 'Generazione in corso…' : 'Genera traduzioni mancanti'}
            </button>
            <button
              className="admin-button admin-button--primary"
              onClick={() => setEditing(createEmptyMenuItemDraft(sections[0]?.id))}
            >
              Aggiungi piatto
            </button>
            <button
              className="admin-button admin-button--quiet"
              onClick={async () => {
                await adminLogout();
                onSignedOut();
              }}
            >
              Esci
            </button>
          </div>
        </header>
        {feedback && (
          <p className={`admin-feedback admin-feedback--${feedback.type}`} role="status">{feedback.message}</p>
        )}
      </div>

      {editing && (
        <ItemForm
          key={editing.id ?? 'new'}
          sections={sections}
          initial={editing}
          onSave={save}
          onCancel={cancelEditing}
          saving={saving}
        />
      )}

      {state === 'loading' && <p className="admin-boot">Caricamento del menù…</p>}
      {state === 'error' && <button className="admin-button admin-button--ghost" onClick={load}>Riprova</button>}
      {state === 'ready' && (
        <div className="admin-sections">
          {sections.map((section) => (
            <section className="admin-section" key={section.id}>
              <div className="admin-section__header">
                <p className="admin-eyebrow">{section.title}</p>
                <h2 className="admin-section__title">{section.title}</h2>
                {section.description && <p className="admin-section__description">{section.description}</p>}
              </div>
              <div className="admin-item-list">
                {section.items.map((item) => (
                  <article className="admin-item" key={item.id}>
                    {item.subtitle && <p className="admin-item__subtitle">{item.subtitle}</p>}
                    <div className="admin-item__header">
                      <h3 className="admin-item__name">{item.name}</h3>
                      <strong>{formatPrice(item.price, { sectionId: section.id })}</strong>
                    </div>
                    {item.description && <p className="admin-item__description">{item.description}</p>}
                    <div className="admin-item-actions">
                      <button
                        className="admin-button admin-button--ghost"
                        onClick={() => setEditing(createMenuItemDraft(item, section.id))}
                      >
                        Modifica
                      </button>
                      <button className="admin-button admin-button--danger" onClick={() => remove(item)}>Elimina</button>
                    </div>
                  </article>
                ))}
              </div>
            </section>
          ))}
        </div>
      )}
    </main>
  );
}
