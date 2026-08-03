import { afterEach, describe, expect, it, vi } from 'vitest';
import { fetchAdminMenuSections, updateAdminMenuItem } from './adminApi.js';
import { fetchMenuSections } from './barApi.js';

const responsePayload = [{
  id: 'vini',
  title: 'Vini',
  description: '',
  items: [{ id: 'wine', name: 'Vino', subtitle: '', description: '', notes: [], price: '5 € / 22 €' }]
}];

function response(body) {
  return Promise.resolve({ ok: true, status: 200, json: () => Promise.resolve(body) });
}

afterEach(() => {
  vi.unstubAllGlobals();
  sessionStorage.clear();
});

describe('menu API boundaries', () => {
  it('normalizes public and admin GET responses before returning them', async () => {
    const fetch = vi.fn()
      .mockImplementationOnce(() => response(responsePayload))
      .mockImplementationOnce(() => response(responsePayload));
    vi.stubGlobal('fetch', fetch);

    const publicMenu = await fetchMenuSections();
    const adminMenu = await fetchAdminMenuSections();
    expect(publicMenu[0].items[0].price.options.map((option) => option.amount)).toEqual([5, 22]);
    expect(adminMenu[0].items[0].price.options.map((option) => option.amount)).toEqual([5, 22]);
    expect(publicMenu).not.toBe(adminMenu);
  });

  it('keeps PUT for item updates and normalizes the mutation response', async () => {
    const fetch = vi.fn(() => response(responsePayload));
    vi.stubGlobal('fetch', fetch);
    const command = { price: { options: [{ amount: 5 }, { amount: 22 }] } };

    const menu = await updateAdminMenuItem('wine id', command);

    expect(fetch).toHaveBeenCalledWith('/api/admin/menu/items/wine%20id', expect.objectContaining({
      method: 'PUT',
      body: JSON.stringify(command)
    }));
    expect(menu[0].items[0].price.kind).toBe('multiple');
  });
});
