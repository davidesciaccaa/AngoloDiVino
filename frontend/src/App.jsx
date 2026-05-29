import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { fetchApiStatus, fetchMenuSections } from './api/barApi.js';
import { Hero } from './components/Hero.jsx';
import { MenuItemCard } from './components/MenuItemCard.jsx';
import { LanguageSwitcher } from './components/LanguageSwitcher.jsx';

// Import immagini locali
import aperitivoImg from './assets/images/aperitivo.jpeg';
import drinkImg from './assets/images/drink.jpeg';
import vinoImg from './assets/images/vino.jpeg';
import amaroImg from './assets/images/amaro.jpeg';
import bevandeImg from './assets/images/bevande.jpeg';
import frullatiImg from './assets/images/frullati.jpeg';

const sectionImages = {
  aperitivo: aperitivoImg,
  drink: drinkImg,
  vini: vinoImg,
  frullati: frullatiImg,
  superalcolici: amaroImg,
  bevande: bevandeImg
};

const fallbackMenuSections = [
  {
    id: 'aperitivo',
    title: 'Aperitivo',
    description: 'Assaggi pensati per aprire la serata con calma.',
    items: [
      {
        id: 'tagliere_salandra',
        name: 'Tagliere Salandra',
        subtitle: 'Formaggi, conserve',
        description: 'Selezione di formaggi locali, olive, focaccia calda e con fettura della casa.',
        notes: ['Vegetariano', 'Perfetto per due'],
        price: '14 EUR'
      },
      {
        id: 'fritti_di_corte',
        name: 'Fritti di Corte',
        subtitle: 'Croccanti, mediterranei',
        description: 'Piccoli fritti misti con verdure di stagione, agrumi e maionese alle erbe.',
        notes: ['Stuzzicheria', 'Servito caldo'],
        price: '11 EUR'
      }
    ]
  },
  {
    id: 'drink',
    title: 'Cocktails',
    description: 'I grandi classici e le nostre proposte miscelate.',
    items: [
      { id: 'aperol_spritz', name: 'Aperol Spritz', subtitle: '', description: '(4 cl Aperol, 1 dl Prosecco, Sprite q.b., arancia)', notes: [], price: '' },
      { id: 'campari_spritz', name: 'Campari Spritz', subtitle: '', description: '(4 cl Campari, 1 dl Prosecco, acqua tonica q.b.)', notes: [], price: '' },
      { id: 'campari_prosecco', name: 'Campari & Prosecco', subtitle: '', description: '(4 cl Campari, 1 dl Prosecco, arancia)', notes: [], price: '' },
      { id: 'caipirinha', name: 'Caipirinha', subtitle: '', description: '(4 cl Cachaça, zucchero di canna, lime, ghiaccio tritato)', notes: [], price: '' },
      { id: 'caipiroska_fragola', name: 'Caipiroska alla Fragola', subtitle: '', description: '', notes: [], price: '' },
      { id: 'gin_tonic', name: 'Gin Tonic', subtitle: '', description: '(4 cl Gin, acqua tonica)', notes: [], price: '' },
      { id: 'hugo', name: 'Hugo', subtitle: '', description: '(2 cl succo di lime, 2 cl sciroppo di sambuco, 1 dl Prosecco, Sprite q.b., menta)', notes: [], price: '' },
      { id: 'moscow_mule', name: 'Moscow Mule', subtitle: '', description: '(1,5 cl succo di lime, 4 cl Vodka, Ginger Beer)', notes: [], price: '' },
      { id: 'london_mule', name: 'London Mule', subtitle: '', description: '(1,5 cl succo di lime, 4 cl Gin, Ginger Beer)', notes: [], price: '' },
      { id: 'long_island', name: 'Long Island Iced Tea', subtitle: '', description: '(3 cl succo di lime, 2 cl zucchero di canna, 1,5 cl Triple Sec, 1,5 cl Gin, 1,5 cl Vodka, 1,5 cl Rum, 1,5 cl Tequila, Cola)', notes: [], price: '' },
      { id: 'japan_iced_tea', name: 'Japan Iced Tea', subtitle: '', description: '(1,5 cl Vodka, 1,5 cl Rum, 1,5 cl Gin, 1,5 cl Midori, 6 cl Sweet & Sour al limone)', notes: [], price: '' },
      { id: 'mojito_scuro', name: 'Mojito Scuro', subtitle: '', description: '(zucchero di canna, lime, menta, rum scuro, acqua frizzante)', notes: [], price: '' },
      { id: 'negroni', name: 'Negroni', subtitle: '', description: '(3 cl Gin, 3 cl Campari, 3 cl Vermouth rosso, arancia)', notes: [], price: '' },
      { id: 'negroni_sbagliato', name: 'Negroni Sbagliato', subtitle: '', description: '(3 cl Prosecco, 3 cl Bitter, 3 cl Vermouth rosso)', notes: [], price: '' },
      { id: 'sex_on_the_beach', name: 'Sex on the Beach', subtitle: '', description: '(4 cl Vodka, 2 cl liquore alla pesca, 4 cl succo d’arancia, 4 cl succo di mirtillo)', notes: [], price: '' },
      { id: 'martini_cocktail', name: 'Martini Cocktail', subtitle: '', description: '(6 cl Gin, 1 cl Martini Dry, scorza di lime, olive)', notes: [], price: '' },
      { id: 'espresso_martini', name: 'Espresso Martini', subtitle: '', description: '(5 cl Vodka, 2 cl caffè espresso, liquore al caffè, zucchero)', notes: [], price: '' },
      { id: 'cosmopolitan', name: 'Cosmopolitan', subtitle: '', description: '(4 cl Vodka, 1,5 cl Triple Sec, 3 cl succo di mirtillo, 1,5 cl succo di lime, scorza d’arancia, ribes rosso)', notes: [], price: '' },
      { id: 'quattro_bianchi', name: 'Quattro Bianchi', subtitle: '', description: '(2 cl Gin, 2 cl Rum, 2 cl Vodka, 2 cl Tequila)', notes: [], price: '' }
    ]
  },
  {
    id: 'vini',
    title: 'Vini',
    description: 'Una selezione ricercata di vini bianchi, rosati e rossi, anche biologici.',
    items: [
      { id: 'calice_vino', name: 'Calice di Vino', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'calice_prosecco', name: 'Calice di Prosecco', subtitle: '', description: '', notes: [], price: '4 €' },
      { id: 'calavento', name: 'Calavento IGP Salento', subtitle: 'Bianco', description: '', notes: [], price: '21 €' },
      { id: 'luna', name: 'Luna IGP Salento', subtitle: 'Bianco', description: '', notes: [], price: '21 €' },
      { id: 'leverano_bianco', name: 'Leverano Vecchia Torre', subtitle: 'Bianco', description: '', notes: [], price: '16 €' },
      { id: 'muller_thurgau', name: 'Müller Thurgau', subtitle: 'Bianco', description: '', notes: [], price: '21 €' },
      { id: 'gewurztraminer', name: 'Gewürztraminer', subtitle: 'Bianco', description: '', notes: [], price: '21 €' },
      { id: 'trebbiano_abruzzo', name: 'Trebbiano d’Abruzzo', subtitle: 'Bianco', description: '', notes: [], price: '16 €' },
      { id: 'verdeca', name: 'Verdeca Due Trulli', subtitle: 'Bianco', description: '', notes: [], price: '18 €' },
      { id: 'chardonnay', name: 'Chardonnay', subtitle: 'Bianco', description: '', notes: [], price: '18 €' },
      { id: 'trebbiano_bio', name: 'Trebbiano d’Abruzzo Bio Vegano', subtitle: 'Bianco Bio', description: '', notes: [], price: '19 €' },
      { id: 'passerina_bio', name: 'Passerina Bio Vegano', subtitle: 'Bianco Bio', description: '', notes: [], price: '19 €' },
      { id: 'pecorino_bio', name: 'Pecorino Bio', subtitle: 'Bianco Bio', description: '', notes: [], price: '19 €' },
      { id: 'castel_del_monte_bianco_bio', name: 'Castel del Monte Bio', subtitle: 'Bianco Bio', description: '', notes: [], price: '19 €' },
      { id: 'vitalba_bio', name: 'Vitalba Bio', subtitle: 'Bianco Bio', description: '', notes: [], price: '19 €' },
      { id: 'dharma_bio', name: 'Dharma Bio', subtitle: 'Bianco Bio', description: '', notes: [], price: '19 €' },
      { id: 'novebolle', name: 'Novebolle D.O.C.', subtitle: 'Spumante Bio', description: '', notes: [], price: '19 €' },
      { id: 'castel_del_monte_rosato_bio', name: 'Castel del Monte Bio', subtitle: 'Rosato Bio', description: '', notes: [], price: '19 €' },
      { id: 'castel_del_monte_rosso_bio', name: 'Castel del Monte Bio', subtitle: 'Rosso Bio', description: '', notes: [], price: '19 €' },
      { id: 'leverano_rosato', name: 'Leverano DOP Vecchia Torre', subtitle: 'Rosato', description: '', notes: [], price: '16 €' },
      { id: 'negroamaro_rosato', name: 'Negroamaro Vecchia Torre', subtitle: 'Rosato', description: '', notes: [], price: '18 €' },
      { id: 'primitivo_rosato_1932', name: 'Primitivo Rosato 1932', subtitle: 'Rosato', description: '', notes: [], price: '19 €' },
      { id: 'numero_zero', name: 'Numero Zero Negroamaro Susumaniello', subtitle: 'Rosato', description: '', notes: [], price: '21 €' },
      { id: 'susumaniello_rosato', name: 'Susumaniello Due Trulli', subtitle: 'Rosato', description: '', notes: [], price: '21 €' },
      { id: 'primitivo_rosso', name: 'Primitivo Vecchia Torre', subtitle: 'Rosso', description: '', notes: [], price: '16 €' },
      { id: 'primitivo_due_trulli', name: 'Primitivo Due Trulli', subtitle: 'Rosso', description: '', notes: [], price: '18 €' },
      { id: 'primitivo_vignaioli', name: 'Primitivo Vignaioli 68 IGP', subtitle: 'Rosso', description: '', notes: [], price: '28 €' },
      { id: 'primitivo_1932', name: 'Primitivo di Manduria 1932', subtitle: 'Rosso', description: '', notes: [], price: '21 €' },
      { id: 'negroamaro_rosso', name: 'Negroamaro Vecchia Torre', subtitle: 'Rosso', description: '', notes: [], price: '16 €' },
      { id: 'negroamaro_due_trulli', name: 'Negroamaro Due Trulli', subtitle: 'Rosso', description: '', notes: [], price: '18 €' },
      { id: 'negroamaro_manorossa', name: 'Negroamaro Manorossa', subtitle: 'Rosso', description: '', notes: [], price: '60 €' },
      { id: 'negroamaro_susumaniello', name: 'Negroamaro Susumaniello', subtitle: 'Rosso', description: '', notes: [], price: '34 €' },
      { id: 'susumaniello_rosso', name: 'Susumaniello Vigna 14 IGP', subtitle: 'Rosso', description: '', notes: [], price: '18 €' },
      { id: 'nerotavola', name: 'Nerotavola Sicilia DOC', subtitle: 'Rosso', description: '', notes: [], price: '28 €' },
      { id: 'nero_di_troia', name: 'Nero di Troia', subtitle: 'Rosso', description: '', notes: [], price: '16 €' },
      { id: 'aglianico', name: 'Aglianico', subtitle: 'Rosso', description: '', notes: [], price: '18 €' },
      { id: 'cabernet', name: 'Cabernet Veneto', subtitle: 'Rosso', description: '', notes: [], price: '21 €' },
      { id: 'ripasso_negrar', name: 'Ripasso Negrar', subtitle: 'Rosso', description: '', notes: [], price: '26 €' },
      { id: 'chianti', name: 'Chianti Classico', subtitle: 'Rosso', description: '', notes: [], price: '20 €' },
      { id: 'brunello', name: 'Brunello', subtitle: 'Rosso', description: '', notes: [], price: '40 €' },
      { id: 'amarone', name: 'Amarone', subtitle: 'Rosso', description: '', notes: [], price: '40 €' }
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
    title: 'Distillati e Rum',
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
      { id: 'j_bally', name: 'J. Bally', subtitle: 'Rum', description: '', notes: [], price: '' },
      { id: 'la_hechicera', name: 'La Hechicera', subtitle: 'Rum', description: '', notes: [], price: '' },
      { id: 'shot_2cl', name: 'Shot con Distillati Base (2 cl)', subtitle: 'Shot', description: '', notes: [], price: '3 €' },
      { id: 'shot_4cl', name: 'Shot con Distillati Base (4 cl)', subtitle: 'Shot', description: '', notes: [], price: '5 €' }
    ]
  },
  {
    id: 'frullati',
    title: 'Frullati',
    description: 'Frullati vitaminici, salutari e preparati con ingredienti freschi di stagione.',
    items: [
      {
        id: 'frullato_tropicale',
        name: 'Frullato Tropicale',
        subtitle: 'Mango, Ananas, Cocco',
        description: 'Un viaggio esotico cremoso e rinfrescante.',
        notes: ['Fresco', 'Vitamina C'],
        price: '7 EUR'
      },
      {
        id: 'frutti_di_bosco',
        name: 'Frutti di Bosco',
        subtitle: 'Mora, Lampone, Mirtillo',
        description: 'Il sapore intenso del sottobosco in un mix vellutato.',
        notes: ['Antiossidante'],
        price: '7 EUR'
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
