import { useEffect, useState } from 'react';
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
        price: '1 €'
      },
      {
        id: 'tagliere_salandra',
        name: 'Tagliere Salandra',
        subtitle: 'Formaggi, conserve',
        description: 'Selezione di formaggi locali, olive, focaccia calda e con fettura della casa.',
        notes: ['Vegetariano', 'Perfetto per due'],
        price: '14 €'
      },
      {
        id: 'fritti_di_corte',
        name: 'Fritti di Corte',
        subtitle: 'Croccanti, mediterranei',
        description: 'Piccoli fritti misti con verdure di stagione, agrumi e maionese alle erbe.',
        notes: ['Stuzzicheria', 'Servito caldo'],
        price: '11 €'
      },
      {
        id: 'ostriche',
        name: 'Ostriche',
        subtitle: 'Specialità di mare',
        description: 'Ostriche fresche servite con limone e pepe nero.',
        notes: ['Fresco', 'Mare'],
        price: '8 €'
      }
    ]
  },
  {
    id: 'drink',
    title: 'Cocktails',
    description: 'I grandi classici e le nostre proposte miscelate.',
    items: [
      // Pre-Dinner (6/7 euro)
      { id: 'americano', name: 'Americano', subtitle: 'Pre-Dinner', description: '(Vermouth Rosso, Campari, Soda)', notes: [], price: '6 € / 7 €' },
      { id: 'bellini', name: 'Bellini', subtitle: 'Pre-Dinner', description: '(Prosecco, Succo di pesca)', notes: [], price: '6 € / 7 €' },
      { id: 'garibaldi', name: 'Garibaldi', subtitle: 'Pre-Dinner', description: '(Campari, Succo d\'Arancia)', notes: [], price: '6 € / 7 €' },
      { id: 'negroni', name: 'Negroni', subtitle: 'Pre-Dinner', description: '(Gin, Vermouth Rosso, Campari)', notes: [], price: '6 € / 7 €' },
      { id: 'negroni_sbagliato', name: 'Negroni Sbagliato', subtitle: 'Pre-Dinner', description: '(Vermouth Rosso, Campari, Prosecco)', notes: [], price: '6 € / 7 €' },
      { id: 'martini_cocktail', name: 'Martini Cocktail', subtitle: 'Pre-Dinner', description: '(Gin, Vermouth Dry)', notes: [], price: '6 € / 7 €' },
      { id: 'mimosa', name: 'Mimosa', subtitle: 'Pre-Dinner', description: '(Prosecco, Succo d\'arancia)', notes: [], price: '6 € / 7 €' },
      { id: 'kir', name: 'Kir', subtitle: 'Pre-Dinner', description: '(Vino bianco fermo, Crème de Cassis, Fragola)', notes: [], price: '6 € / 7 €' },
      { id: 'kir_royale', name: 'Kir Royale', subtitle: 'Pre-Dinner', description: '(Champagne/Prosecco, Crème de Cassis, Fragola)', notes: [], price: '6 € / 7 €' },
      { id: 'rossini', name: 'Rossini', subtitle: 'Pre-Dinner', description: '(Prosecco, Fragola)', notes: [], price: '6 € / 7 €' },
      { id: 'aperol_spritz', name: 'Aperol Spritz', subtitle: 'Pre-Dinner', description: '(Prosecco, Aperol, Soda)', notes: [], price: '6 € / 7 €' },
      { id: 'campari_spritz', name: 'Campari Spritz', subtitle: 'Pre-Dinner', description: '(Prosecco, Campari, Soda)', notes: [], price: '6 € / 7 €' },
      { id: 'manhattan', name: 'Manhattan', subtitle: 'Pre-Dinner', description: '(Rye whiskey, Vermouth Rosso, Angostura, Ciliegina)', notes: [], price: '6 € / 7 €' },
      { id: 'manhattan_varianti', name: 'Varianti Manhattan', subtitle: 'Pre-Dinner', description: '(Dry, Perfect, Rob Roy, Presidente)', notes: [], price: '6 € / 7 €' },
      { id: 'dry_martini', name: 'Dry Martini', subtitle: 'Pre-Dinner / Martini Cocktails', description: '', notes: [], price: '6 € / 7 €' },
      { id: 'vodka_martini', name: 'Vodka Martini', subtitle: 'Pre-Dinner / Martini Cocktails', description: '', notes: [], price: '6 € / 7 €' },
      { id: 'sweet_martini', name: 'Sweet Martini', subtitle: 'Pre-Dinner / Martini Cocktails', description: '', notes: [], price: '6 € / 7 €' },
      { id: 'perfect_martini', name: 'Perfect Martini', subtitle: 'Pre-Dinner / Martini Cocktails', description: '', notes: [], price: '6 € / 7 €' },

      // After Dinner (6/7 euro)
      { id: 'alexander', name: 'Alexander', subtitle: 'After Dinner', description: '(Brandy, Crema di latte, Crema di cacao scuro)', notes: [], price: '6 € / 7 €' },
      { id: 'black_russian', name: 'Black Russian', subtitle: 'After Dinner', description: '(Vodka, Kahlua)', notes: [], price: '6 € / 7 €' },
      { id: 'white_russian', name: 'White Russian', subtitle: 'After Dinner', description: '(Vodka, Kahlua, Crema di latte)', notes: [], price: '6 € / 7 €' },
      { id: 'cosmopolitan', name: 'Cosmopolitan', subtitle: 'After Dinner', description: '(Vodka, Triple Sec, Cranberry, Limone)', notes: [], price: '6 € / 7 €' },
      { id: 'daiquiri', name: 'Daiquiri', subtitle: 'After Dinner', description: '(Rum, Succo di limone o lime, Sciroppo di zucchero)', notes: [], price: '6 € / 7 €' },
      { id: 'french_connection', name: 'French connection', subtitle: 'After Dinner', description: '(Cognac, Disaronno)', notes: [], price: '6 € / 7 €' },
      { id: 'gin_fizz', name: 'Gin Fizz', subtitle: 'After Dinner', description: '(Gin, Succo di limone, Sciroppo di zucchero, Soda water)', notes: [], price: '6 € / 7 €' },
      { id: 'godfather', name: 'Godfather', subtitle: 'After Dinner', description: '(Whisky, Disaronno)', notes: [], price: '6 € / 7 €' },
      { id: 'godmother', name: 'Godmother', subtitle: 'After Dinner', description: '(Vodka, Disaronno)', notes: [], price: '6 € / 7 €' },
      { id: 'kamikaze', name: 'Kamikaze', subtitle: 'After Dinner', description: '(Vodka, Triple Sec, Succo di limone)', notes: [], price: '6 € / 7 €' },
      { id: 'whitelady', name: 'Whitelady', subtitle: 'After Dinner', description: '(Gin, Triple Sec, Succo di limone)', notes: [], price: '6 € / 7 €' },
      { id: 'limbo', name: 'Limbo', subtitle: 'After Dinner', description: '(Rum bianco, Creme de banana, Succo d\'arancia)', notes: [], price: '6 € / 7 €' },
      { id: 'margarita', name: 'Margarita', subtitle: 'After Dinner', description: '(Tequila, Triple Sec, Succo di limone o lime)', notes: [], price: '6 € / 7 €' },
      { id: 'orgasm', name: 'Orgasm', subtitle: 'After Dinner', description: '(Baileys, Amaretto, Kahlua)', notes: [], price: '6 € / 7 €' },
      { id: 'melon_ball', name: 'Melon ball', subtitle: 'After Dinner', description: '(Midori, Vodka, Succo d\'arancia)', notes: [], price: '6 € / 7 €' },
      { id: 'mexican_passion', name: 'Mexican passion', subtitle: 'After Dinner', description: '(Tequila Blanco/Passoa, Triple Sec, Succo di limone o lime, Polpa frutto della passione, Ananas, Lime)', notes: [], price: '6 € / 7 €' },
      { id: 'paradise', name: 'Paradise', subtitle: 'After Dinner', description: '(Gin, Apricot brandy, Succo d\'arancia)', notes: [], price: '6 € / 7 €' },
      { id: 'rusty_nail', name: 'Rusty nail', subtitle: 'After Dinner', description: '(Scotch whisky, Drambuie)', notes: [], price: '6 € / 7 €' },
      { id: 'sidecar', name: 'Sidecar', subtitle: 'After Dinner', description: '(Cognac, Triple sec o Cointreau, Succo di limone)', notes: [], price: '6 € / 7 €' },
      { id: 'stinger', name: 'Stinger', subtitle: 'After Dinner', description: '(Brandy, Crema di menta bianca)', notes: [], price: '6 € / 7 €' },

      // Long Drinks (6 euro)
      { id: 'alabama_slammer', name: 'Alabama slammer', subtitle: 'Long Drinks', description: '(Vodka, Southern Comfort, Amaretto, Sloe Gin o Granatina, Succo d\'arancia)', notes: [], price: '6 €' },
      { id: 'cuba_libre', name: 'Cuba Libre', subtitle: 'Long Drinks', description: '(Rum, Coca cola, Succo di lime)', notes: [], price: '6 €' },
      { id: 'lynchburg_lemonade', name: 'Lynchburg lemonade', subtitle: 'Long Drinks', description: '(Jack Daniel\'s, Triple sec, Sciroppo di zucchero, Succo di limone, Sprite)', notes: [], price: '6 €' },
      { id: 'pina_colada', name: 'Pina colada', subtitle: 'Long Drinks', description: '(Rum, Crema di cocco, Ananas)', notes: [], price: '6 €' },
      { id: 'pina_colada_varianti', name: 'Varianti di Pina Colada', subtitle: 'Long Drinks', description: '', notes: [], price: '6 €' },
      { id: 'screwdriver', name: 'Screwdriver', subtitle: 'Long Drinks', description: '(Vodka, Succo d\'arancia)', notes: [], price: '6 €' },
      { id: 'sex_on_the_beach', name: 'Sex on the beach', subtitle: 'Long Drinks', description: '(Vodka, Liquore di pesca, Succo d\'arancia, Succo di cranberry)', notes: [], price: '6 €' },
      { id: 'tequila_sunrise', name: 'Tequila sunrise', subtitle: 'Long Drinks', description: '(Tequila, Succo d\'arancia, Granatina)', notes: [], price: '6 €' },
      { id: 'singapore_sling', name: 'Singapore sling', subtitle: 'Long Drinks', description: '(Gin, Cherry brandy, Succo di limone o lime, Soda water)', notes: [], price: '6 €' },
      { id: 'sloe_gin', name: 'Sloe gin', subtitle: 'Long Drinks', description: '(Liquore a base di gin aromatizzato al prugnolo o Granatina)', notes: [], price: '6 €' },

      // Iced Tea (6/7 euro)
      { id: 'long_island_iced_tea', name: 'Long Island Ice tea', subtitle: 'Iced Tea', description: '(Vodka, Gin, Rum, Triple Sec, Sweet & Sour, Top of Coke)', notes: [], price: '6 € / 7 €' },
      { id: 'texas_iced_tea', name: 'Texas Ice tea', subtitle: 'Iced Tea', description: '(Vodka, Gin, Rum, Triple Sec, Tequila, Sweet & Sour, Top of Coke)', notes: [], price: '6 € / 7 €' },
      { id: 'japanese_iced_tea', name: 'Japanese Ice tea', subtitle: 'Iced Tea', description: '(Vodka, Gin, Rum, Midori, Sweet & Sour, Lemonsoda)', notes: [], price: '6 € / 7 €' },
      { id: 'italian_iced_tea', name: 'Italian ice tea', subtitle: 'Iced Tea', description: '(Vodka, Gin, Rum, Amaretto, Sweet & Sour, Lemonsoda)', notes: [], price: '6 € / 7 €' },
      { id: 'california_iced_tea', name: 'California ice tea', subtitle: 'Iced Tea', description: '(Vodka, Gin, Rum, Triple Sec, Sweet & Sour, Succo d\'arancia)', notes: [], price: '6 € / 7 €' },
      { id: 'invisibile', name: 'Invisibile', subtitle: 'Iced Tea', description: '(Vodka, Gin, Rum, Triple Sec, Sweet & Sour)', notes: [], price: '6 € / 7 €' },

      // Pestati e Frozen (6/7 euro)
      { id: 'caipirinha', name: 'Caipirinha', subtitle: 'Pestati e Frozen', description: '(Cachaca, Lime, Zucchero di canna)', notes: [], price: '6 € / 7 €' },
      { id: 'caipiroska', name: 'Caipiroska', subtitle: 'Pestati e Frozen', description: '(Vodka, Lime, Zucchero di canna)', notes: [], price: '6 € / 7 €' },
      { id: 'strawberry_caipiroska', name: 'Strawberry caipiroska', subtitle: 'Pestati e Frozen', description: '(Vodka, Lime, Zucchero di canna, Fragola)', notes: [], price: '6 € / 7 €' },
      { id: 'caipirissima', name: 'Caipirissima', subtitle: 'Pestati e Frozen', description: '(Rum, Lime, Zucchero di canna)', notes: [], price: '6 € / 7 €' },
      { id: 'caipiriteqa', name: 'Caipiriteqa', subtitle: 'Pestati e Frozen', description: '(Tequila, Lime, Zucchero di canna)', notes: [], price: '6 € / 7 €' },
      { id: 'mojito', name: 'Mojito', subtitle: 'Pestati e Frozen', description: '(Rum, Lime, Foglie di menta, Watersoda)', notes: [], price: '6 € / 7 €' },
      { id: 'frozen_slammer', name: 'Frozen slammer', subtitle: 'Pestati e Frozen', description: '(Southern comfort, Disaronno, Fragola, Sciroppo e frutta)', notes: [], price: '6 € / 7 €' },
      { id: 'frozen_daiquiri', name: 'Frozen daiquiri', subtitle: 'Pestati e Frozen', description: '(Rum, Fragola, Banana, Ananas, ecc, Sweet & Sour)', notes: [], price: '6 € / 7 €' },
      { id: 'frozen_margarita', name: 'Frozen margarita', subtitle: 'Pestati e Frozen', description: '(Tequila, Fragola, Banana, Ananas, ecc, Sweet & Sour)', notes: [], price: '6 € / 7 €' },

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
      { id: 'tacco_barocco_puglia_igp', name: 'Tacco Barocco - Puglia IGP', subtitle: '', description: 'Corposo ed equilibrato, perfetto per accompagnare taglieri. Cantina Sampietrana. 750ml', notes: [], price: '6 € - 20 €' },
      { id: 'rosato_salento', name: 'Rosato del Salento', subtitle: 'Rosati', description: 'Fresco, fruttato, con note di ciliegia e lampone. 750ml', notes: [], price: '5 € / 18 €' },
      { id: 'prosecco_doc', name: 'Prosecco D.O.C.', subtitle: 'Bollicine', description: 'Perlage fine e persistente, ideale come aperitivo. 750ml', notes: [], price: '4 € / 16 €' }
    ]
  },
  {
    id: 'amari',
    title: 'Amari',
    description: 'Selezione di amari e liquori per chiudere in bellezza.',
    items: [
      { id: 'ramazzotti', name: 'Ramazzotti', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'cynar', name: 'Cynar', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'montenegro', name: 'Montenegro', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'fernet', name: 'Fernet Branca Menta', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'averna', name: 'Averna', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'petrus', name: 'Petrus', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'jagermeister', name: 'Jagermeister', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'jefferson', name: 'Jefferson', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'unicum', name: 'Unicum', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'lucano', name: 'Lucano', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'del_capo', name: 'Amaro del capo', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'sambuca', name: 'Sambuca', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'borghetti', name: 'Caffè borghetti', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'vena_caffe', name: 'Vena Caffè', subtitle: '', description: '', notes: [], price: '4 €' }
    ]
  },
  {
    id: 'superalcolici',
    title: 'Superalcolici',
    description: 'Una selezione di pregiati distillati e rum da meditazione.',
    items: [
      { id: 'vecchia_romagna', name: 'Vecchia Romagna', subtitle: 'Distillato', description: '', notes: [], price: '5 €' },
      { id: 'cointreau', name: 'Cointreau', subtitle: 'Distillato', description: '', notes: [], price: '5 €' },
      { id: 'jack_daniels', name: 'Jack Daniel’s', subtitle: 'Distillato', description: '', notes: [], price: '6 €' },
      { id: 'jack_daniels_honey', name: 'Jack Daniel’s Honey', subtitle: 'Distillato', description: '', notes: [], price: '6 €' },
      { id: 'cardinal_mendoza', name: 'Cardinal Mendoza', subtitle: 'Distillato', description: '', notes: [], price: '12 €' },
      { id: 'oban', name: 'Oban', subtitle: 'Distillato', description: '', notes: [], price: '12 €' },
      { id: 'laphroaig', name: 'Laphroaig', subtitle: 'Distillato', description: '', notes: [], price: '12 €' },
      { id: 'lagavulin', name: 'Lagavulin', subtitle: 'Distillato', description: '', notes: [], price: '12 €' },
      { id: 'sambuca_superalcolico', name: 'Sambuca', subtitle: 'Distillato', description: '', notes: [], price: '5 €' },
      { id: 'martini', name: 'Martini Bianco / Rosso / Dry', subtitle: 'Distillato', description: '', notes: [], price: '5 €' },
      { id: 'bacardi', name: 'Bacardi', subtitle: 'Rum', description: '', notes: [], price: '5 €' },
      { id: 'don_papa', name: 'Don Papa', subtitle: 'Rum', description: '', notes: [], price: '9 €' },
      { id: 'zacapa', name: 'Zacapa 23 Anni', subtitle: 'Rum', description: '', notes: [], price: '12 €' },
      { id: 'j_bally', name: 'J. Bally', subtitle: 'Rum', description: '', notes: [], price: '10 €' },
      { id: 'la_hechicera', name: 'La Hechicera', subtitle: 'Rum', description: '', notes: [], price: '12 €' },
      { id: 'shot_2cl', name: 'Shot con Distillati Base (2 cl)', subtitle: 'Shot', description: '', notes: [], price: '3 €' },
      { id: 'shot_4cl', name: 'Shot con Distillati Base (4 cl)', subtitle: 'Shot', description: '', notes: [], price: '5 €' }
    ]
  },
  {
    id: 'frullati',
    title: 'Frullati e Centrifughe',
    description: 'Frullati vitaminici, salutari e preparati con ingredienti freschi di stagione.',
    items: [
      {
        id: 'frullato_tropicale',
        name: 'Frullato Tropicale',
        subtitle: 'Mango, Ananas, Cocco',
        description: 'Un viaggio esotico cremoso e rinfrescante.',
        notes: ['Fresco', 'Vitamina C'],
        price: '7 €'
      },
      {
        id: 'frutti_di_bosco',
        name: 'Frutti di Bosco',
        subtitle: 'Mora, Lampone, Mirtillo',
        description: 'Il sapore intenso del sottobosco in un mix vellutato.',
        notes: ['Antiossidante'],
        price: '7 €'
      }
    ]
  },
  {
    id: 'bevande',
    title: 'Bevande',
    description: 'Analcolici, soft drink e alternative leggere.',
    items: [
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
        // Integrate locally added categories if not present in API
        const hasFrullati = sections.some(s => s.id === 'frullati');
        if (!hasFrullati) {
           const frullatiSection = fallbackMenuSections.find(s => s.id === 'frullati');
           setMenuSections([...sections, frullatiSection]);
        } else {
           setMenuSections(sections);
        }
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
        <div className="category-nav-wrapper">
          <nav className="category-nav">
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
            <a href="mailto:ilbistrodeidotti19@gmail.com">ilbistrodeidotti19@gmail.com</a>
          </div>
        </section>
      </div>
    </main>
  );
}

export default App;
