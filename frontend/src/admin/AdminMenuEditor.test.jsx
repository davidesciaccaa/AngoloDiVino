import { fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AdminMenuEditor } from './AdminMenuEditor.jsx';

vi.mock('../api/adminApi.js', () => ({
  adminLogout: vi.fn(),
  createAdminMenuItem: vi.fn(),
  deleteAdminMenuItem: vi.fn(),
  fetchAdminMenuSections: vi.fn(),
  updateAdminMenuItem: vi.fn()
}));

import {
  createAdminMenuItem,
  deleteAdminMenuItem,
  fetchAdminMenuSections,
  updateAdminMenuItem
} from '../api/adminApi.js';

const originalSections = [{
  id: 'vini',
  title: 'Vini',
  description: '',
  items: [{
    id: 'wine',
    name: 'Vino prova',
    subtitle: 'Bianchi',
    description: 'Descrizione',
    notes: ['Nota originale'],
    price: {
      kind: 'multiple',
      options: [
        { label: 'glass', amount: 5 },
        { label: 'bottle', amount: 22 }
      ]
    }
  }]
}];

function editButton() {
  const heading = screen.getByRole('heading', { name: 'Vino prova' });
  return within(heading.closest('article')).getByRole('button', { name: 'Modifica' });
}

beforeEach(() => {
  fetchAdminMenuSections.mockResolvedValue(structuredClone(originalSections));
  updateAdminMenuItem.mockResolvedValue(structuredClone(originalSections));
  createAdminMenuItem.mockResolvedValue(structuredClone(originalSections));
  deleteAdminMenuItem.mockResolvedValue(structuredClone(originalSections));
});

describe('AdminMenuEditor cancellation', () => {
  it('immediate cancellation closes the draft without a write', async () => {
    const user = userEvent.setup();
    render(<AdminMenuEditor onSignedOut={vi.fn()} />);
    await user.click(await screen.findByRole('button', { name: 'Modifica' }));
    await user.click(screen.getByRole('button', { name: 'Annulla' }));

    expect(screen.queryByRole('button', { name: 'Salva piatto' })).not.toBeInTheDocument();
    expect(updateAdminMenuItem).not.toHaveBeenCalled();
  });

  it('button cancellation discards a temporary price and performs no write', async () => {
    const user = userEvent.setup();
    render(<AdminMenuEditor onSignedOut={vi.fn()} />);
    await user.click(await screen.findByRole('button', { name: 'Modifica' }));
    const input = screen.getByRole('textbox', { name: /Prezzo/ });
    expect(input).toHaveValue('5 / 22');
    await user.clear(input);
    await user.type(input, '99 / 100');
    await user.click(screen.getByRole('button', { name: 'Annulla' }));

    expect(screen.queryByRole('button', { name: 'Salva piatto' })).not.toBeInTheDocument();
    expect(updateAdminMenuItem).not.toHaveBeenCalled();
    expect(createAdminMenuItem).not.toHaveBeenCalled();
    expect(deleteAdminMenuItem).not.toHaveBeenCalled();

    await user.click(editButton());
    expect(screen.getByRole('textbox', { name: /Prezzo/ })).toHaveValue('5 / 22');
  });

  it('Escape discards temporary fields and performs no write', async () => {
    const user = userEvent.setup();
    render(<AdminMenuEditor onSignedOut={vi.fn()} />);
    await user.click(await screen.findByRole('button', { name: 'Modifica' }));
    await user.clear(screen.getByRole('textbox', { name: /Prezzo/ }));
    await user.type(screen.getByRole('textbox', { name: /Prezzo/ }), '7 / 30');
    fireEvent.keyDown(window, { key: 'Escape' });

    expect(screen.queryByRole('button', { name: 'Salva piatto' })).not.toBeInTheDocument();
    expect(updateAdminMenuItem).not.toHaveBeenCalled();
  });

  it('leaving the admin form discards the draft without a write', async () => {
    const user = userEvent.setup();
    const view = render(<AdminMenuEditor onSignedOut={vi.fn()} />);
    await user.click(await screen.findByRole('button', { name: 'Modifica' }));
    await user.clear(screen.getByRole('textbox', { name: /Prezzo/ }));
    await user.type(screen.getByRole('textbox', { name: /Prezzo/ }), '7 / 30');
    view.unmount();

    expect(updateAdminMenuItem).not.toHaveBeenCalled();
    expect(createAdminMenuItem).not.toHaveBeenCalled();
  });

  it('saves a valid structured price after a previous cancellation', async () => {
    const user = userEvent.setup();
    render(<AdminMenuEditor onSignedOut={vi.fn()} />);
    await user.click(await screen.findByRole('button', { name: 'Modifica' }));
    await user.clear(screen.getByRole('textbox', { name: /Prezzo/ }));
    await user.type(screen.getByRole('textbox', { name: /Prezzo/ }), '99 / 100');
    await user.click(screen.getByRole('button', { name: 'Annulla' }));

    await user.click(editButton());
    const input = screen.getByRole('textbox', { name: /Prezzo/ });
    await user.clear(input);
    await user.type(input, '5,50 / 23');
    await user.click(screen.getByRole('button', { name: 'Salva piatto' }));

    await waitFor(() => expect(updateAdminMenuItem).toHaveBeenCalledTimes(1));
    expect(updateAdminMenuItem).toHaveBeenCalledWith('wine', expect.objectContaining({
      notes: ['Nota originale'],
      price: {
        options: [
          { label: 'glass', amount: 5.5 },
          { label: 'bottle', amount: 23 }
        ]
      }
    }));
  });
});
