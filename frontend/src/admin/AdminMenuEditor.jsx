import { useCallback, useEffect, useState } from 'react';
import { adminLogout, createAdminMenuItem, deleteAdminMenuItem, fetchAdminMenuSections, updateAdminMenuItem } from '../api/adminApi.js';

const empty = (sectionId = '') => ({ sectionId, name: '', subtitle: '', description: '', notes: '', price: '' });
const numericValue = (value) => String(value).match(/\d+(?:[.,]\d+)?/)?.[0]?.replace(',', '.') ?? '';
const price = (value) => `${Number(numericValue(value)).toLocaleString('it-IT', { minimumFractionDigits: Number(numericValue(value)) % 1 ? 2 : 0, maximumFractionDigits: 2 })} €`;

function ItemForm({ sections, initial, onSave, onCancel, saving }) {
  const [form, setForm] = useState(initial);
  const [error, setError] = useState('');
  const set = (key) => (event) => setForm((old) => ({ ...old, [key]: event.target.value }));
  function submit(event) {
    event.preventDefault();
    const value = form.price.replace(',', '.');
    if (!/^\d+(?:\.\d{1,2})?$/.test(value) || Number(value) <= 0) { setError('Inserisci un prezzo numerico valido.'); return; }
    onSave({ ...form, price: Number(value), notes: form.notes.split('\n').map((note) => note.trim()).filter(Boolean) });
  }
  return <form className="admin-item-form" onSubmit={submit}>
    <label className="admin-field"><span className="admin-field__label">Sezione</span><select className="admin-field__input" value={form.sectionId} onChange={set('sectionId')}>{sections.map((section) => <option key={section.id} value={section.id}>{section.title}</option>)}</select></label>
    <label className="admin-field"><span className="admin-field__label">Nome</span><input required maxLength="120" className="admin-field__input" value={form.name} onChange={set('name')} /></label>
    <label className="admin-field"><span className="admin-field__label">Categoria / sottocategoria</span><input maxLength="80" className="admin-field__input" value={form.subtitle} onChange={set('subtitle')} /></label>
    <label className="admin-field"><span className="admin-field__label">Descrizione</span><textarea maxLength="1000" className="admin-field__input" value={form.description} onChange={set('description')} /></label>
    <label className="admin-field"><span className="admin-field__label">Note (una per riga)</span><textarea className="admin-field__input" value={form.notes} onChange={set('notes')} /></label>
    <label className="admin-field"><span className="admin-field__label">Prezzo</span><span className="admin-price-input"><input required inputMode="decimal" pattern="[0-9]+([.,][0-9]{1,2})?" className="admin-field__input" value={form.price} onChange={set('price')} aria-describedby="price-help" /><span aria-hidden="true">€</span></span><small id="price-help">Inserisci solo cifre e decimali: il simbolo è fisso.</small></label>
    {error && <p className="admin-feedback admin-feedback--error" role="alert">{error}</p>}
    <div className="admin-form-actions"><button className="admin-button admin-button--primary" disabled={saving}>{saving ? 'Salvataggio…' : 'Salva piatto'}</button><button type="button" className="admin-button admin-button--ghost" onClick={onCancel}>Annulla</button></div>
  </form>;
}

export function AdminMenuEditor({ onSignedOut }) {
  const [sections, setSections] = useState([]); const [state, setState] = useState('loading'); const [editing, setEditing] = useState(null); const [saving, setSaving] = useState(false); const [feedback, setFeedback] = useState(null);
  const load = useCallback(async () => { try { setState('loading'); setSections(await fetchAdminMenuSections()); setState('ready'); } catch (error) { if (error.status === 401) onSignedOut(); else { setFeedback({ type: 'error', message: error.message }); setState('error'); } } }, [onSignedOut]);
  useEffect(() => { load(); }, [load]);
  async function save(data) { setSaving(true); setFeedback(null); try { const updated = editing?.id ? await updateAdminMenuItem(editing.id, data) : await createAdminMenuItem(data); setSections(updated); setEditing(null); setFeedback({ type: 'success', message: 'Menù aggiornato. Il sito pubblico mostra subito le modifiche.' }); } catch (error) { setFeedback({ type: 'error', message: error.message }); } finally { setSaving(false); } }
  async function remove(item) { if (!window.confirm(`Eliminare “${item.name}”?`)) return; try { setSections(await deleteAdminMenuItem(item.id)); setFeedback({ type: 'success', message: 'Piatto eliminato.' }); } catch (error) { setFeedback({ type: 'error', message: error.message }); } }
  return <main className="admin-shell"><div className="admin-topbar-wrapper"><header className="admin-topbar"><div><p className="admin-eyebrow">L&apos;Angolo diVino</p><h1 className="admin-topbar__title">Gestione menù</h1></div><div className="admin-topbar__actions"><button className="admin-button admin-button--primary" onClick={() => setEditing(empty(sections[0]?.id))}>Aggiungi piatto</button><button className="admin-button admin-button--quiet" onClick={async () => { await adminLogout(); onSignedOut(); }}>Esci</button></div></header>{feedback && <p className={`admin-feedback admin-feedback--${feedback.type}`} role="status">{feedback.message}</p>}</div>
    {editing && <ItemForm sections={sections} initial={editing} onSave={save} onCancel={() => setEditing(null)} saving={saving} />}
    {state === 'loading' && <p className="admin-boot">Caricamento del menù…</p>}{state === 'error' && <button className="admin-button admin-button--ghost" onClick={load}>Riprova</button>}
    {state === 'ready' && <div className="admin-sections">{sections.map((section) => <section className="admin-section" key={section.id}><div className="admin-section__header"><p className="admin-eyebrow">{section.title}</p><h2 className="admin-section__title">{section.title}</h2>{section.description && <p className="admin-section__description">{section.description}</p>}</div><div className="admin-item-list">{section.items.map((item) => <article className="admin-item" key={item.id}>{item.subtitle && <p className="admin-item__subtitle">{item.subtitle}</p>}<div className="admin-item__header"><h3 className="admin-item__name">{item.name}</h3><strong>{price(item.price)}</strong></div>{item.description && <p className="admin-item__description">{item.description}</p>}<div className="admin-item-actions"><button className="admin-button admin-button--ghost" onClick={() => setEditing({ ...item, price: numericValue(item.price), sectionId: section.id, notes: item.notes.join('\n') })}>Modifica</button><button className="admin-button admin-button--danger" onClick={() => remove(item)}>Elimina</button></div></article>)}</div></section>)}</div>}
  </main>;
}
