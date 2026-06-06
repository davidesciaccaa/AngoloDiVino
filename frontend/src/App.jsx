import { useEffect, useState, useRef, useCallback } from 'react';
import { useTranslation } from 'react-i18next';
import { fetchApiStatus, fetchMenuSections } from './api/barApi.js';
import { Hero } from './components/Hero.jsx';
import { MenuItemCard } from './components/MenuItemCard.jsx';

const fallbackMenuSections = [
  {
    id: 'aperitivo',
    title: 'Aperitivo',
    description: 'Assaggi pensati per aprire la serata con calma.',
    items: [
      {
        id: 'tris',
        name: 'Tris',
        subtitle: 'Assaggi locali',
        description: 'Una piccola selezione di stuzzichini del giorno.',
        notes: [],
        price: '3 €'
      },
      {
        id: 'tagliere',
        name: 'Tagliere',
        subtitle: '',
        description: '',
        notes: [],
        price: '10 €'
      },
      {
        id: 'fritti',
        name: 'Fritti',
        subtitle: '',
        description: '',
        notes: [],
        price: '10 €'
      },
      {
        id: 'ostriche',
        name: 'Ostriche',
        subtitle: '',
        description: 'Ostriche fresche',
        notes: ['Fresco', 'Mare'],
        price: '10 €'
      }
    ]
  },
  {
    id: 'drink',
    title: 'Cocktails',
    description: 'I grandi classici e le nostre proposte miscelate.',
    items: [
      // Pre-Dinner (7 euro) - Aperol/Campari Spritz (6 euro)
      { id: 'americano', name: 'Americano', subtitle: 'Pre-Dinner', description: '(Vermouth Rosso, Campari, Soda)', notes: [], price: '7 €' },
      { id: 'bellini', name: 'Bellini', subtitle: 'Pre-Dinner', description: '(Prosecco, Succo di pesca)', notes: [], price: '7 €' },
      { id: 'garibaldi', name: 'Garibaldi', subtitle: 'Pre-Dinner', description: '(Campari, Succo d\'Arancia)', notes: [], price: '7 €' },
      { id: 'negroni', name: 'Negroni', subtitle: 'Pre-Dinner', description: '(gin, Vermouth rosso, Campari, Fetta d\'arancia)', notes: [], price: '7 €' },
      { id: 'negroni_sbagliato', name: 'Negroni Sbagliato', subtitle: 'Pre-Dinner', description: '(Vermouth rosso, Campari, prosecco, Fetta d\'arancia)', notes: [], price: '7 €' },
      { id: 'martini_cocktail', name: 'Martini Cocktail', subtitle: 'Pre-Dinner', description: '(Gin, Vermouth Dry, scorza di lime, olive)', notes: [], price: '7 €' },
      { id: 'mimosa', name: 'Mimosa', subtitle: 'Pre-Dinner', description: '(Prosecco, Succo d\'arancia)', notes: [], price: '7 €' },
      { id: 'kir', name: 'Kir', subtitle: 'Pre-Dinner', description: '(vino bianco fermo, Crème de Cassis)', notes: [], price: '7 €' },
      { id: 'kir_royale', name: 'Kir Royale', subtitle: 'Pre-Dinner', description: '(prosecco, Crème de Cassis)', notes: [], price: '7 €' },
      { id: 'rossini', name: 'Rossini', subtitle: 'Pre-Dinner', description: '(Prosecco, Fragola)', notes: [], price: '7 €' },
      { id: 'aperol_spritz', name: 'Aperol Spritz', subtitle: 'Pre-Dinner', description: '(Prosecco, Aperol, Soda, fetta d\'arancia)', notes: [], price: '6 €' },
      { id: 'campari_spritz', name: 'Campari Spritz', subtitle: 'Pre-Dinner', description: '(Prosecco, Campari, Soda, fetta d\'arancia)', notes: [], price: '6 €' },
      { id: 'manhattan', name: 'Manhattan', subtitle: 'Pre-Dinner', description: '(rye whiskey, vermouth rosso, angostura, ciliegina)', notes: [], price: '7 €' },
      { id: 'dry_manhattan', name: 'Dry Manhattan', subtitle: 'Pre-Dinner', description: '(rye whiskey, vermouth dry, angostura, buccia di limone)', notes: [], price: '7 €' },
      { id: 'perfect_manhattan', name: 'Perfect Manhattan', subtitle: 'Pre-Dinner', description: '(rye whiskey, vermouth rosso, vermouth dry, ciliegina)', notes: [], price: '7 €' },
      { id: 'rob_roy', name: 'Rob Roy', subtitle: 'Pre-Dinner', description: '(Scotch whisky, vermouth rosso, angostura, ciliegina)', notes: [], price: '7 €' },
      { id: 'dry_martini', name: 'Dry Martini', subtitle: 'Pre-Dinner / Martini Cocktails', description: '(gin, vermouth dry, sprizzo di buccia di limone)', notes: [], price: '7 €' },
      { id: 'vodka_martini', name: 'Vodka Martini', subtitle: 'Pre-Dinner / Martini Cocktails', description: '(vodka, vermouth dry, sprizzo di buccia di limone)', notes: [], price: '7 €' },
      { id: 'sweet_martini', name: 'Sweet Martini', subtitle: 'Pre-Dinner / Martini Cocktails', description: '(gin, vermouth rosso, ciliegina)', notes: [], price: '7 €' },
      { id: 'perfect_martini', name: 'Perfect Martini', subtitle: 'Pre-Dinner / Martini Cocktails', description: '(gin, vermouth rosso, vermouth dry, ciliegina)', notes: [], price: '7 €' },

      // After Dinner (8 euro)
      { id: 'black_russian', name: 'Black Russian', subtitle: 'After Dinner', description: '(Vodka, Kahlua)', notes: [], price: '8 €' },
      { id: 'white_russian', name: 'White Russian', subtitle: 'After Dinner', description: '(Vodka, Kahlua, Crema di latte)', notes: [], price: '8 €' },
      { id: 'cosmopolitan', name: 'Cosmopolitan', subtitle: 'After Dinner', description: '(Vodka, Triple Sec, Cranberry, Limone)', notes: [], price: '8 €' },
      { id: 'daiquiri', name: 'Daiquiri', subtitle: 'After Dinner', description: '(Rum, Succo di limone o lime, Sciroppo di zucchero)', notes: [], price: '8 €' },
      { id: 'french_connection', name: 'French connection', subtitle: 'After Dinner', description: '(Cognac, Disaronno)', notes: [], price: '8 €' },
      { id: 'mexican_passion', name: 'Mexican passion', subtitle: 'After Dinner', description: '(tequila blanco, triple sec, succo di lime, succo di passion fruit, fetta di limone)', notes: [], price: '8 €' },
      { id: 'gin_fizz', name: 'Gin Fizz', subtitle: 'After Dinner', description: '(Gin, Succo di limone, Sciroppo di zucchero, Soda water)', notes: [], price: '8 €' },
      { id: 'godfather', name: 'Godfather', subtitle: 'After Dinner', description: '(Whisky, Disaronno)', notes: [], price: '8 €' },
      { id: 'godmother', name: 'Godmother', subtitle: 'After Dinner', description: '(Vodka, Disaronno)', notes: [], price: '8 €' },
      { id: 'kamikaze', name: 'Kamikaze', subtitle: 'After Dinner', description: '(vodka, triple sec, succo di limone)', notes: [], price: '8 €' },
      { id: 'whitelady', name: 'Whitelady', subtitle: 'After Dinner', description: '(Gin, Triple Sec, Succo di limone)', notes: [], price: '8 €' },
      { id: 'limbo', name: 'Limbo', subtitle: 'After Dinner', description: '(Rum bianco, Creme de banana, Succo d\'arancia)', notes: [], price: '8 €' },
      { id: 'margarita', name: 'Margarita', subtitle: 'After Dinner', description: '(Tequila, Triple Sec, Succo di limone o lime)', notes: [], price: '8 €' },
      { id: 'orgasm', name: 'Orgasm', subtitle: 'After Dinner', description: '(Baileys, Amaretto, Kahlua)', notes: [], price: '8 €' },
      { id: 'melon_ball', name: 'Melon ball', subtitle: 'After Dinner', description: '(Midori, Vodka, Succo d\'arancia)', notes: [], price: '8 €' },
      { id: 'paradise', name: 'Paradise', subtitle: 'After Dinner', description: '(Gin, Apricot brandy, Succo d\'arancia)', notes: [], price: '8 €' },
      { id: 'rusty_nail', name: 'Rusty nail', subtitle: 'After Dinner', description: '(Scotch whisky, Drambuie)', notes: [], price: '8 €' },
      { id: 'sidecar', name: 'Sidecar', subtitle: 'After Dinner', description: '(Cognac, Triple sec o Cointreau, Succo di limone)', notes: [], price: '8 €' },
      { id: 'stinger', name: 'Stinger', subtitle: 'After Dinner', description: '(Brandy, Crema di menta bianca)', notes: [], price: '8 €' },

      // Long Drinks (8 euro)
      { id: 'cuba_libre', name: 'Cuba Libre', subtitle: 'Long Drinks', description: '(Rum, Coca cola, Succo di lime)', notes: [], price: '8 €' },
      { id: 'lynchburg_lemonade', name: 'Lynchburg lemonade', subtitle: 'Long Drinks', description: '(jack Daniel\'s, triple sec, succo di limone, sprite)', notes: [], price: '8 €' },
      { id: 'pina_colada', name: 'Pina colada', subtitle: 'Long Drinks', description: '(Rum, Crema di cocco, Ananas)', notes: [], price: '8 €' },
      { id: 'pina_colada_varianti', name: 'Varianti di Pina Colada', subtitle: 'Long Drinks', description: '', notes: [], price: '8 €' },
      { id: 'screwdriver', name: 'Screwdriver', subtitle: 'Long Drinks', description: '(Vodka, Succo d\'arancia)', notes: [], price: '8 €' },
      { id: 'sex_on_the_beach', name: 'Sex on the beach', subtitle: 'Long Drinks', description: '(vodka, liquore di pesca, succo d\'arancia, succo di cranberry)', notes: [], price: '8 €' },
      { id: 'tequila_sunrise', name: 'Tequila sunrise', subtitle: 'Long Drinks', description: '(Tequila, Succo d\'arancia, Granatina)', notes: [], price: '8 €' },

      // Iced Tea (6/7 euro)
      { id: 'long_island_iced_tea', name: 'Long Island Ice tea', subtitle: 'Iced Tea', description: '(Vodka, Gin, Rum, Triple Sec, Sweet & Sour, Top of Coke)', notes: [], price: '8 €' },
      { id: 'texas_iced_tea', name: 'Texas Ice tea', subtitle: 'Iced Tea', description: '(Vodka, Gin, Rum, Triple Sec, Tequila, Sweet & Sour, Top of Coke)', notes: [], price: '8 €' },
      { id: 'japanese_iced_tea', name: 'Japanese Ice tea', subtitle: 'Iced Tea', description: '(Vodka, Gin, Rum, Midori, Sweet & Sour, Lemonsoda)', notes: [], price: '8 €' },
      { id: 'italian_iced_tea', name: 'Italian ice tea', subtitle: 'Iced Tea', description: '(Vodka, Gin, Rum, Amaretto, Sweet & Sour, Lemonsoda)', notes: [], price: '8 €' },
      { id: 'california_iced_tea', name: 'California ice tea', subtitle: 'Iced Tea', description: '(Vodka, Gin, Rum, Triple Sec, Sweet & Sour, Succo d\'arancia)', notes: [], price: '8 €' },

      // Pestati e Frozen (8 euro)
      { id: 'caipirinha', name: 'Caipirinha', subtitle: 'Pestati e Frozen', description: '(Cachaca, Lime, Zucchero di canna)', notes: [], price: '8 €' },
      { id: 'caipiroska', name: 'Caipiroska', subtitle: 'Pestati e Frozen', description: '(Vodka, Lime, Zucchero di canna)', notes: [], price: '8 €' },
      { id: 'strawberry_caipiroska', name: 'Strawberry caipiroska', subtitle: 'Pestati e Frozen', description: '(Vodka, Lime, Zucchero di canna, Fragola)', notes: [], price: '8 €' },
      { id: 'caipirissima', name: 'Caipirissima', subtitle: 'Pestati e Frozen', description: '(Rum, Lime, Zucchero di canna)', notes: [], price: '8 €' },
      { id: 'caipiriteqa', name: 'Caipiriteqa', subtitle: 'Pestati e Frozen', description: '(Tequila, Lime, Zucchero di canna)', notes: [], price: '8 €' },
      { id: 'mojito', name: 'Mojito', subtitle: 'Pestati e Frozen', description: '(Rum, Lime, Foglie di menta, Watersoda)', notes: [], price: '8 €' },
      { id: 'frozen_slammer', name: 'Frozen slammer', subtitle: 'Pestati e Frozen', description: '(Southern comfort, Disaronno, Fragola, Sciroppo e frutta)', notes: [], price: '8 €' },
      { id: 'frozen_daiquiri', name: 'Frozen daiquiri', subtitle: 'Pestati e Frozen', description: '(Rum, Fragola, Banana, Ananas, ecc, Sweet & Sour)', notes: [], price: '8 €' },
      { id: 'frozen_margarita', name: 'Frozen margarita', subtitle: 'Pestati e Frozen', description: '(Tequila, Fragola, Banana, Ananas, ecc, Sweet & Sour)', notes: [], price: '8 €' },

      // Soft drink (analcolici, 5 euro)
      { id: 'virgin_colada', name: 'Virgin colada', subtitle: 'Soft drink', description: '(Latte di cocco o Pina colada mix, Succo d\'arancia)', notes: [], price: '5 €' },
      { id: 'daiquiri_strawberry', name: 'Daiquiri Strawberry', subtitle: 'Soft drink', description: '(Sciroppo di fragola, Succo di lime o limone)', notes: [], price: '5 €' },
      { id: 'florida', name: 'Florida', subtitle: 'Soft drink', description: '(Succo di pompelmo, Succo d\'arancia, Succo di lime o limone, Sciroppo di zucchero, Soda water)', notes: [], price: '5 €' },
      { id: 'shirley_temple', name: 'Shirley Temple', subtitle: 'Soft drink', description: '(Ginger ale, Sciroppo di granatina)', notes: [], price: '5 €' },
      { id: 'red_peach', name: 'Red peach', subtitle: 'Soft drink', description: '(Sciroppo di fragola, Succo di pesca, Succo d\'ananas)', notes: [], price: '5 €' },
      { id: 'sweet_strawberry', name: 'Sweet strawberry', subtitle: 'Soft drink', description: '(Sciroppo di fragola, Succo d\'arancia, Top of Sprite)', notes: [], price: '5 €' }
    ]
  },
  {
    id: 'vini',
    title: 'Vini',
    description: 'Una selezione ricercata di vini bianchi, rosati e rossi dalla Cantina Sampietrana e Verdeca.',
    items: [
      { id: 'tacco_barocco_bianco', name: 'Tacco Barocco - Negroamaro Bianco Primitivo', subtitle: 'Bianchi', description: 'Note di fiori bianchi e agrumi, fresco e persistente. Cantina Sampietrana. 750ml', notes: [], price: '5 € / 22 €' },
      { id: 'verdeca_salento', name: 'Verdeca del Salento', subtitle: '', description: 'Vino autoctono dal bouquet delicato e sapore secco. Cantina Verdeca. 750ml', notes: [], price: '5 € / 18 €' },
      { id: 'verdeca_itria', name: 'Verdeca Valle d\'Itria', subtitle: '', description: 'Fresco, fruttato e con una piacevole sapidità. Cantina Verdeca. 750ml', notes: [], price: '5 € / 18 €' },
      { id: 'tacco_barocco_negroamaro', name: 'Tacco Barocco - Negroamaro', subtitle: 'Rossi', description: 'Rosso rubino intenso con sentori di piccoli frutti rossi. Cantina Sampietrana. 750ml', notes: [], price: '5 € / 22 €' },
      { id: 'tacco_barocco_puglia_igp', name: 'Tacco Barocco - Puglia IGP', subtitle: '', description: 'Corposo ed equilibrato, perfetto per accompagnare taglieri. Cantina Sampietrana. 750ml', notes: [], price: '6 € - 24 €' },
      { id: 'rosato_salento', name: 'Rosato del Salento', subtitle: 'Rosati', description: 'Fresco, fruttato, con note di ciliegia e lampone. 750ml', notes: [], price: '5 € / 18 €' },
      { id: 'prosecco_doc', name: 'Prosecco D.O.C.', subtitle: 'Bollicine', description: 'Perlage fine e persistente, ideale come aperitivo. 750ml', notes: [], price: '4 € / 18 €' }
    ]
  },
  {
    id: 'amari',
    title: 'Amari e Liquori',
    description: 'Selezione di amari e liquori per chiudere in bellezza.',
    items: [
      { id: 'montenegro', name: 'Montenegro', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'del_capo', name: 'Amaro del Capo', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'jagermeister', name: 'Jagermeister', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'unicum', name: 'Unicum', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'sambuca', name: 'Sambuca', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'kahlua', name: 'Kahlua', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'limoncello', name: 'Limoncello', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'cointreau', name: 'Cointreau', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'grand_marnier', name: 'Grand Marnier', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'pernod', name: 'Pernod', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'batida_de_coco', name: 'Batida de coco', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'midori', name: 'Midori', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'malibu', name: 'Malibù', subtitle: '', description: '', notes: [], price: '4 €' }
    ]
  },
  {
    id: 'superalcolici',
    title: 'Superalcolici',
    description: 'Una selezione di pregiati distillati e rum da meditazione.',
    items: [
      { id: 'jack_daniels', name: "Jack Daniel's", subtitle: 'Whisky', description: '4cl', notes: [], price: '6 €' },
      { id: 'johnnie_walker', name: 'Johnnie Walker', subtitle: 'Whisky', description: '4cl', notes: [], price: '6 €' },
      { id: 'lagavulin', name: 'Lagavulin', subtitle: 'Whisky', description: '4cl', notes: [], price: '9 €' },
      { id: 'absolut', name: 'Absolut', subtitle: 'Vodka', description: '', notes: [], price: '6 €' },
      { id: 'moskovskaya', name: 'Moskovskaya', subtitle: 'Vodka', description: '', notes: [], price: '5 €' },
      { id: 'stolichnaya', name: 'Stolichnaya', subtitle: 'Vodka', description: '', notes: [], price: '6 €' },
      { id: 'ciroc', name: 'Cîroc', subtitle: 'Vodka', description: '', notes: [], price: '6 €' },
      { id: 'aniversario', name: 'Pampero Aniversario', subtitle: 'Rum', description: '', notes: [], price: '6 €' },
      { id: 'havana_3', name: 'Havana Club 3', subtitle: 'Rum', description: '', notes: [], price: '6 €' },
      { id: 'havana_7', name: 'Havana Club 7', subtitle: 'Rum', description: '', notes: [], price: '6 €' },
      { id: 'zacapa', name: 'Zacapa', subtitle: 'Rum', description: '', notes: [], price: '6 €' },
      { id: 'kraken', name: 'Kraken', subtitle: 'Rum', description: '', notes: [], price: '6 €' },
      { id: 'tanqueray', name: 'Tanqueray', subtitle: 'Gin', description: '', notes: [], price: '6 €' },
      { id: 'bombay', name: 'Bombay Sapphire', subtitle: 'Gin', description: '', notes: [], price: '6 €' },
      { id: 'gin_mare', name: 'Gin Mare', subtitle: 'Gin', description: '', notes: [], price: '9 €' },
      { id: 'hendricks', name: "Hendrick's", subtitle: 'Gin', description: '', notes: [], price: '9 €' },
      { id: 'malfy_originale', name: 'Malfy Originale', subtitle: 'Gin', description: '', notes: [], price: '7 €' },
      { id: 'malfy_pompelmo', name: 'Malfy Pompelmo', subtitle: 'Gin', description: '', notes: [], price: '7 €' },
      { id: 'malfy_arancia', name: 'Malfy Arancia', subtitle: 'Gin', description: '', notes: [], price: '7 €' },
      { id: 'cubical', name: 'Cubical', subtitle: 'Gin', description: '', notes: [], price: '8 €' },
      { id: 'citadelle', name: 'Gin Citadelle', subtitle: 'Gin', description: '', notes: [], price: '7 €' },
      { id: 'roku', name: 'Roku', subtitle: 'Gin', description: '', notes: [], price: '7 €' },
      { id: 'nordes', name: 'Nordés', subtitle: 'Gin', description: '', notes: [], price: '7 €' },
      { id: 'jose_cuervo', name: 'Jose Cuervo', subtitle: 'Tequila', description: '', notes: [], price: '5 €' },
      { id: 'fortaleza', name: 'Fortaleza', subtitle: 'Tequila', description: '', notes: [], price: '9 €' },
      { id: 'grappa_bianca', name: 'Grappa Bianca', subtitle: 'Grappe & Cognac', description: '', notes: [], price: '6 €' },
      { id: 'grappa_barricata', name: 'Grappa Barricata Amarone della Valpolicella', subtitle: 'Grappe & Cognac', description: '', notes: [], price: '9 €' },
      { id: 'vecchia_romagna', name: 'Vecchia Romagna', subtitle: 'Grappe & Cognac', description: '', notes: [], price: '5 €' },
      { id: 'martell', name: 'Martell', subtitle: 'Grappe & Cognac', description: '', notes: [], price: '8 €' },
      { id: 'armagnac', name: 'Armagnac', subtitle: 'Grappe & Cognac', description: 'In arrivo', notes: [], price: '-' },
      { id: 'shot_2cl', name: 'Shot con Distillati Base (2 cl)', subtitle: 'Shot', description: '', notes: [], price: '3 €' },
      { id: 'shot_4cl', name: 'Shot con Distillati Base (4 cl)', subtitle: 'Shot', description: '', notes: [], price: '5 €' }
    ]
  },
  {
    id: 'bevande',
    title: 'Bevande',
    description: 'Analcolici, soft drink e alternative leggere.',
    items: [
      { id: 'centrifuga', name: 'Centrifuga', subtitle: '', description: 'In base alla stagione', notes: [], price: '5 €' },
      { id: 'acqua', name: 'Acqua Naturale / Frizzante', subtitle: '', description: '', notes: [], price: '2,50 €' },
      { id: 'coca_cola', name: 'Coca-Cola / Coca-Cola Zero', subtitle: '', description: '', notes: [], price: '3 €' },
      { id: 'fanta', name: 'Fanta', subtitle: '', description: '', notes: [], price: '3 €' },
      { id: 'succhi', name: 'Succhi di Frutta', subtitle: '', description: '', notes: [], price: '3,50 €' },
      { id: 'chinotto', name: 'Chinotto', subtitle: '', description: '', notes: [], price: '3 €' },
      { id: 'schweppes_lemon', name: 'Schweppes Lemon', subtitle: '', description: '', notes: [], price: '3 €' },
      { id: 'te', name: 'Tè Pesca / Limone', subtitle: '', description: '', notes: [], price: '3 €' },
      { id: 'san_bitter', name: 'San Bitter Bianco / Rosso', subtitle: '', description: '', notes: [], price: '3 €' },
      { id: 'cocktail_sp', name: 'Cocktail San Pellegrino', subtitle: '', description: '', notes: [], price: '3 €' },
      { id: 'crodino', name: 'Crodino', subtitle: '', description: '', notes: [], price: '3 €' },
      { id: 'tonica', name: 'Acqua Tonica', subtitle: '', description: '', notes: [], price: '3 €' },
      { id: 'red_bull', name: 'Red Bull', subtitle: '', description: '', notes: [], price: '4 €' }
    ]
  }
];

function App() {
  const { t } = useTranslation();
  const [apiStatus, setApiStatus] = useState({ status: 'LOADING' });
  const [menuSections, setMenuSections] = useState(fallbackMenuSections);
  const [isUsingFallback, setIsUsingFallback] = useState(false);

  // Scroll Indicators Logic
  const navRef = useRef(null);
  const [showLeftIndicator, setShowLeftIndicator] = useState(false);
  const [showRightIndicator, setShowRightIndicator] = useState(false);

  const checkScroll = useCallback(() => {
    if (navRef.current) {
      const { scrollLeft, scrollWidth, clientWidth } = navRef.current;
      setShowLeftIndicator(scrollLeft > 10);
      setShowRightIndicator(scrollLeft < scrollWidth - clientWidth - 10);
    }
  }, []);

  useEffect(() => {
    const nav = navRef.current;
    if (nav) {
      checkScroll();
      nav.addEventListener('scroll', checkScroll);
      window.addEventListener('resize', checkScroll);
      
      // Also check when content might have rendered
      const timeout = setTimeout(checkScroll, 500);

      return () => {
        nav.removeEventListener('scroll', checkScroll);
        window.removeEventListener('resize', checkScroll);
        clearTimeout(timeout);
      };
    }
  }, [checkScroll, menuSections]);

  useEffect(() => {
    let isMounted = true;

    async function loadHomeData() {
      try {
        const [status, sections] = await Promise.all([
          fetchApiStatus(),
          fetchMenuSections()
        ]);

        if (!isMounted) {
          return;
        }

        setApiStatus(status);
        setMenuSections(sections);
        setIsUsingFallback(false);
      } catch {
        if (!isMounted) {
          return;
        }

        setApiStatus({ status: 'DOWN' });
        setMenuSections(fallbackMenuSections);
        setIsUsingFallback(true);
      }
    }

    loadHomeData();

    return () => {
      isMounted = false;
    };
  }, []);

  return (
    <main>
      <Hero status={apiStatus} />

      <section className="section section--menu" id="menu" aria-labelledby="menu-title">
        <div className="section__heading">
          <p className="section__eyebrow">{t('nav.menu', { defaultValue: 'Menu' })}</p>
          <h2 id="menu-title">{t('menu.title')}</h2>
          {isUsingFallback && (
            <p className="section__note">{t('menu.fallback_note')}</p>
          )}
        </div>

        {/* Minimal Category Navigation */}
        <div className={`category-nav-wrapper ${showLeftIndicator ? 'has-left-scroll' : ''} ${showRightIndicator ? 'has-right-scroll' : ''}`}>
          <div className="scroll-hint scroll-hint--left" aria-hidden="true">
            <span>‹</span>
          </div>
          <nav className="category-nav" ref={navRef}>
            {menuSections.map((section) => (
              <a 
                key={`nav-${section.id}`} 
                href={`#${section.id}`} 
                className="category-link"
              >
                {t(`menu.sections.${section.id}.title`, { defaultValue: section.title })}
              </a>
            ))}
          </nav>
          <div className="scroll-hint scroll-hint--right" aria-hidden="true">
            <span>›</span>
          </div>
        </div>

        <div className="menu-section-list">
          {menuSections.map((section) => (
            <section className="menu-section" id={section.id} key={section.id}>
              <div className="menu-section__header-editorial">
                <p className="section__eyebrow">{t(`menu.sections.${section.id}.title`, { defaultValue: section.title })}</p>
                <h3>{t(`menu.sections.${section.id}.title`, { defaultValue: section.title })}</h3>
                {section.description && (
                  <p className="menu-item-editorial__description" style={{ marginTop: '4px' }}>
                    {t(`menu.sections.${section.id}.description`, { defaultValue: section.description })}
                  </p>
                )}
              </div>

              <div className="menu-list-editorial">
                {section.id === 'vini' && (
                  <div className="menu-item-editorial__header" style={{ justifyContent: 'flex-end', marginBottom: '-10px', opacity: 0.6 }}>
                    <span className="menu-item-editorial__price" style={{ fontSize: '0.7rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                      {t(`menu.sections.${section.id}.price_header`)}
                    </span>
                  </div>
                )}
                {section.items.map((item) => (
                  <MenuItemCard key={`${section.id}-${item.name}`} item={item} />
                ))}
              </div>
            </section>
          ))}
        </div>
      </section>

      <div className="experience-wrapper">
        <section className="section section--experience" id="esperienza">
          <div className="experience-panel">
            <p className="section__eyebrow">{t('experience.eyebrow')}</p>
            <h1>{t('experience.title')}</h1>

            <p>
              {t('experience.text')}
            </p>
          </div>
          <div className="hours-panel" id="contatti">
            <p className="section__eyebrow">{t('contacts.eyebrow')}</p>
            <h1>{t('contacts.title')}</h1>
            <p>{t('contacts.hours')}</p>
            <div className="hours-panel__links">
              <a href={`tel:${t('contacts.phone')}`}>{t('contacts.phone')}</a>
              <a href="mailto:ilbistrodeidotti19@gmail.com">ilbistrodeidotti19@gmail.com</a>
            </div>
          </div>
        </section>
      </div>
    </main>
  );
}

export default App;
