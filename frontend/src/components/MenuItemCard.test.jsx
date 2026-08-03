import { render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { MenuItemCard } from './MenuItemCard.jsx';

vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (_key, options) => options?.defaultValue ?? '',
    i18n: { language: 'it', getResourceBundle: () => ({}) }
  })
}));

const baseItem = {
  id: 'wine',
  name: 'Vino',
  subtitle: '',
  description: '',
  notes: []
};

describe('MenuItemCard price rendering', () => {
  it('renders every multiple amount with the current slash separator', () => {
    render(<MenuItemCard item={{ ...baseItem, price: '5 € / 22 €' }} sectionId="vini" />);
    expect(screen.getByText('5 € / 22 €')).toBeInTheDocument();
  });

  it('renders absent and invalid prices without NaN', () => {
    const { rerender } = render(<MenuItemCard item={{ ...baseItem, price: null }} />);
    expect(screen.getByText('-')).toBeInTheDocument();
    rerender(<MenuItemCard item={{ ...baseItem, price: 'ambiguous' }} />);
    expect(screen.getByText('Prezzo non valido')).toBeInTheDocument();
    expect(screen.queryByText(/NaN/)).not.toBeInTheDocument();
  });
});
