import { act, cleanup, render, screen } from '@testing-library/react';
import { afterEach, describe, expect, it } from 'vitest';
import i18n from '../i18n.js';
import { MenuItemCard } from './MenuItemCard.jsx';

afterEach(async () => {
  cleanup();
  await i18n.changeLanguage('it');
});

describe('MenuItemCard language changes', () => {
  it('switches immediately using the translations already in the item payload', async () => {
    await i18n.changeLanguage('it-IT');
    render(<MenuItemCard sectionId="vini" item={{
      id: 'dynamic-test', name: 'Vino', subtitle: '', description: 'Italiano', notes: [], price: null,
      translations: { en: { name: 'Wine', subtitle: '', description: 'English', notes: [] } }
    }} />);
    expect(screen.getByText('Vino')).toBeInTheDocument();
    await act(() => i18n.changeLanguage('en-US'));
    expect(screen.getByText('Wine')).toBeInTheDocument();
    expect(screen.getByText('English')).toBeInTheDocument();
  });
});
