import { useEffect, useState } from 'react';
import { fetchApiStatus, fetchMenuSections } from './api/barApi.js';
import { Hero } from './components/Hero.jsx';
import { MenuItemCard } from './components/MenuItemCard.jsx';

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
        name: 'Tagliere Salandra',
        subtitle: 'Formaggi, conserve',
        description: 'Selezione di formaggi locali, olive, focaccia calda e con fettura della casa.',
        notes: ['Vegetariano', 'Perfetto per due'],
        price: '14 EUR'
      },
      {
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
      { name: 'Aperol Spritz', subtitle: '', description: '(4 cl Aperol, 1 dl Prosecco, Sprite q.b., arancia)', notes: [], price: '' },
      { name: 'Campari Spritz', subtitle: '', description: '(4 cl Campari, 1 dl Prosecco, acqua tonica q.b.)', notes: [], price: '' },
      { name: 'Campari & Prosecco', subtitle: '', description: '(4 cl Campari, 1 dl Prosecco, arancia)', notes: [], price: '' },
      { name: 'Caipirinha', subtitle: '', description: '(4 cl Cachaça, zucchero di canna, lime, ghiaccio tritato)', notes: [], price: '' },
      { name: 'Caipiroska alla Fragola', subtitle: '', description: '', notes: [], price: '' },
      { name: 'Gin Tonic', subtitle: '', description: '(4 cl Gin, acqua tonica)', notes: [], price: '' },
      { name: 'Hugo', subtitle: '', description: '(2 cl succo di lime, 2 cl sciroppo di sambuco, 1 dl Prosecco, Sprite q.b., menta)', notes: [], price: '' },
      { name: 'Moscow Mule', subtitle: '', description: '(1,5 cl succo di lime, 4 cl Vodka, Ginger Beer)', notes: [], price: '' },
      { name: 'London Mule', subtitle: '', description: '(1,5 cl succo di lime, 4 cl Gin, Ginger Beer)', notes: [], price: '' },
      { name: 'Long Island Iced Tea', subtitle: '', description: '(3 cl succo di lime, 2 cl zucchero di canna, 1,5 cl Triple Sec, 1,5 cl Gin, 1,5 cl Vodka, 1,5 cl Rum, 1,5 cl Tequila, Cola)', notes: [], price: '' },
      { name: 'Japan Iced Tea', subtitle: '', description: '(1,5 cl Vodka, 1,5 cl Rum, 1,5 cl Gin, 1,5 cl Midori, 6 cl Sweet & Sour al limone)', notes: [], price: '' },
      { name: 'Mojito Scuro', subtitle: '', description: '(zucchero di canna, lime, menta, rum scuro, acqua frizzante)', notes: [], price: '' },
      { name: 'Negroni', subtitle: '', description: '(3 cl Gin, 3 cl Campari, 3 cl Vermouth rosso, arancia)', notes: [], price: '' },
      { name: 'Negroni Sbagliato', subtitle: '', description: '(3 cl Prosecco, 3 cl Bitter, 3 cl Vermouth rosso)', notes: [], price: '' },
      { name: 'Sex on the Beach', subtitle: '', description: '(4 cl Vodka, 2 cl liquore alla pesca, 4 cl succo d’arancia, 4 cl succo di mirtillo)', notes: [], price: '' },
      { name: 'Martini Cocktail', subtitle: '', description: '(6 cl Gin, 1 cl Martini Dry, scorza di lime, olive)', notes: [], price: '' },
      { name: 'Espresso Martini', subtitle: '', description: '(5 cl Vodka, 2 cl caffè espresso, liquore al caffè, zucchero)', notes: [], price: '' },
      { name: 'Cosmopolitan', subtitle: '', description: '(4 cl Vodka, 1,5 cl Triple Sec, 3 cl succo di mirtillo, 1,5 cl succo di lime, scorza d’arancia, ribes rosso)', notes: [], price: '' },
      { name: 'Quattro Bianchi', subtitle: '', description: '(2 cl Gin, 2 cl Rum, 2 cl Vodka, 2 cl Tequila)', notes: [], price: '' }
    ]
  },
  {
    id: 'vini',
    title: 'Vini',
    description: 'Una selezione ricercata di vini bianchi, rosati e rossi, anche biologici.',
    items: [
      { name: 'Calice di Vino', subtitle: '', description: '', notes: [], price: '4 €' },
      { name: 'Calice di Prosecco', subtitle: '', description: '', notes: [], price: '4 €' },
      { name: 'Calavento IGP Salento', subtitle: 'Bianco', description: '', notes: [], price: '21 €' },
      { name: 'Luna IGP Salento', subtitle: 'Bianco', description: '', notes: [], price: '21 €' },
      { name: 'Leverano Vecchia Torre', subtitle: 'Bianco', description: '', notes: [], price: '16 €' },
      { name: 'Müller Thurgau', subtitle: 'Bianco', description: '', notes: [], price: '21 €' },
      { name: 'Gewürztraminer', subtitle: 'Bianco', description: '', notes: [], price: '21 €' },
      { name: 'Trebbiano d’Abruzzo', subtitle: 'Bianco', description: '', notes: [], price: '16 €' },
      { name: 'Verdeca Due Trulli', subtitle: 'Bianco', description: '', notes: [], price: '18 €' },
      { name: 'Chardonnay', subtitle: 'Bianco', description: '', notes: [], price: '18 €' },
      { name: 'Trebbiano d’Abruzzo Bio Vegano', subtitle: 'Bianco Bio', description: '', notes: [], price: '19 €' },
      { name: 'Passerina Bio Vegano', subtitle: 'Bianco Bio', description: '', notes: [], price: '19 €' },
      { name: 'Pecorino Bio', subtitle: 'Bianco Bio', description: '', notes: [], price: '19 €' },
      { name: 'Castel del Monte Bio', subtitle: 'Bianco Bio', description: '', notes: [], price: '19 €' },
      { name: 'Vitalba Bio', subtitle: 'Bianco Bio', description: '', notes: [], price: '19 €' },
      { name: 'Dharma Bio', subtitle: 'Bianco Bio', description: '', notes: [], price: '19 €' },
      { name: 'Novebolle D.O.C.', subtitle: 'Spumante Bio', description: '', notes: [], price: '19 €' },
      { name: 'Castel del Monte Bio', subtitle: 'Rosato Bio', description: '', notes: [], price: '19 €' },
      { name: 'Castel del Monte Bio', subtitle: 'Rosso Bio', description: '', notes: [], price: '19 €' },
      { name: 'Leverano DOP Vecchia Torre', subtitle: 'Rosato', description: '', notes: [], price: '16 €' },
      { name: 'Negroamaro Vecchia Torre', subtitle: 'Rosato', description: '', notes: [], price: '18 €' },
      { name: 'Primitivo Rosato 1932', subtitle: 'Rosato', description: '', notes: [], price: '19 €' },
      { name: 'Numero Zero Negroamaro Susumaniello', subtitle: 'Rosato', description: '', notes: [], price: '21 €' },
      { name: 'Susumaniello Due Trulli', subtitle: 'Rosato', description: '', notes: [], price: '21 €' },
      { name: 'Primitivo Vecchia Torre', subtitle: 'Rosso', description: '', notes: [], price: '16 €' },
      { name: 'Primitivo Due Trulli', subtitle: 'Rosso', description: '', notes: [], price: '18 €' },
      { name: 'Primitivo Vignaioli 68 IGP', subtitle: 'Rosso', description: '', notes: [], price: '28 €' },
      { name: 'Primitivo di Manduria 1932', subtitle: 'Rosso', description: '', notes: [], price: '21 €' },
      { name: 'Negroamaro Vecchia Torre', subtitle: 'Rosso', description: '', notes: [], price: '16 €' },
      { name: 'Negroamaro Due Trulli', subtitle: 'Rosso', description: '', notes: [], price: '18 €' },
      { name: 'Negroamaro Manorossa', subtitle: 'Rosso', description: '', notes: [], price: '60 €' },
      { name: 'Negroamaro Susumaniello', subtitle: 'Rosso', description: '', notes: [], price: '34 €' },
      { name: 'Susumaniello Vigna 14 IGP', subtitle: 'Rosso', description: '', notes: [], price: '18 €' },
      { name: 'Nerotavola Sicilia DOC', subtitle: 'Rosso', description: '', notes: [], price: '28 €' },
      { name: 'Nero di Troia', subtitle: 'Rosso', description: '', notes: [], price: '16 €' },
      { name: 'Aglianico', subtitle: 'Rosso', description: '', notes: [], price: '18 €' },
      { name: 'Cabernet Veneto', subtitle: 'Rosso', description: '', notes: [], price: '21 €' },
      { name: 'Ripasso Negrar', subtitle: 'Rosso', description: '', notes: [], price: '26 €' },
      { name: 'Chianti Classico', subtitle: 'Rosso', description: '', notes: [], price: '20 €' },
      { name: 'Brunello', subtitle: 'Rosso', description: '', notes: [], price: '40 €' },
      { name: 'Amarone', subtitle: 'Rosso', description: '', notes: [], price: '40 €' }
    ]
  },
  {
    id: 'frullati',
    title: 'Frullati',
    description: 'Frullati vitaminici, salutari e preparati con ingredienti freschi di stagione.',
    items: [
      {
        name: 'Frullato Tropicale',
        subtitle: 'Mango, Ananas, Cocco',
        description: 'Un viaggio esotico cremoso e rinfrescante.',
        notes: ['Fresco', 'Vitamina C'],
        price: '7 EUR'
      },
      {
        name: 'Frutti di Bosco',
        subtitle: 'Mora, Lampone, Mirtillo',
        description: 'Il sapore intenso del sottobosco in un mix vellutato.',
        notes: ['Antiossidante'],
        price: '7 EUR'
      }
    ]
  },
  {
    id: 'superalcolici',
    title: 'Distillati e Rum',
    description: 'Una selezione di pregiati distillati e rum da meditazione.',
    items: [
      { name: 'Vecchia Romagna', subtitle: 'Distillato', description: '', notes: [], price: '5 €' },
      { name: 'Cointreau', subtitle: 'Distillato', description: '', notes: [], price: '5 €' },
      { name: 'Jack Daniel’s', subtitle: 'Distillato', description: '', notes: [], price: '6 €' },
      { name: 'Jack Daniel’s Honey', subtitle: 'Distillato', description: '', notes: [], price: '6 €' },
      { name: 'Cardinal Mendoza', subtitle: 'Distillato', description: '', notes: [], price: '12 €' },
      { name: 'Oban', subtitle: 'Distillato', description: '', notes: [], price: '12 €' },
      { name: 'Laphroaig', subtitle: 'Distillato', description: '', notes: [], price: '12 €' },
      { name: 'Lagavulin', subtitle: 'Distillato', description: '', notes: [], price: '12 €' },
      { name: 'Sambuca', subtitle: 'Distillato', description: '', notes: [], price: '5 €' },
      { name: 'Martini Bianco / Rosso / Dry', subtitle: 'Distillato', description: '', notes: [], price: '5 €' },
      { name: 'Bacardi', subtitle: 'Rum', description: '', notes: [], price: '5 €' },
      { name: 'Don Papa', subtitle: 'Rum', description: '', notes: [], price: '9 €' },
      { name: 'Zacapa 23 Anni', subtitle: 'Rum', description: '', notes: [], price: '12 €' },
      { name: 'J. Bally', subtitle: 'Rum', description: '', notes: [], price: '' },
      { name: 'La Hechicera', subtitle: 'Rum', description: '', notes: [], price: '' },
      { name: 'Shot con Distillati Base (2 cl)', subtitle: 'Shot', description: '', notes: [], price: '3 €' },
      { name: 'Shot con Distillati Base (4 cl)', subtitle: 'Shot', description: '', notes: [], price: '5 €' }
    ]
  },
  {
    id: 'bevande',
    title: 'Bevande',
    description: 'Analcolici, soft drink e alternative leggere.',
    items: [
      { name: 'Acqua Naturale / Frizzante', subtitle: '', description: '', notes: [], price: '2,50 €' },
      { name: 'Coca-Cola / Coca-Cola Zero', subtitle: '', description: '', notes: [], price: '3 €' },
      { name: 'Fanta', subtitle: '', description: '', notes: [], price: '3 €' },
      { name: 'Succhi di Frutta', subtitle: '', description: '', notes: [], price: '3,50 €' },
      { name: 'Chinotto', subtitle: '', description: '', notes: [], price: '3 €' },
      { name: 'Schweppes Lemon', subtitle: '', description: '', notes: [], price: '3 €' },
      { name: 'Tè Pesca / Limone', subtitle: '', description: '', notes: [], price: '3 €' },
      { name: 'San Bitter Bianco / Rosso', subtitle: '', description: '', notes: [], price: '3 €' },
      { name: 'Cocktail San Pellegrino', subtitle: '', description: '', notes: [], price: '3 €' },
      { name: 'Crodino', subtitle: '', description: '', notes: [], price: '3 €' },
      { name: 'Acqua Tonica', subtitle: '', description: '', notes: [], price: '3 €' },
      { name: 'Red Bull', subtitle: '', description: '', notes: [], price: '4 €' }
    ]
  }
];

function App() {
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
          <p className="section__eyebrow">Menu</p>
          <h2 id="menu-title">Selezione d&apos;autore.</h2>
          {isUsingFallback && (
            <p className="section__note">Backend non raggiungibile: stai vedendo il menu locale.</p>
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
                {section.title}
              </a>
            ))}
          </nav>
        </div>

        <div className="menu-section-list">
          {menuSections.map((section) => (
            <section className="menu-section" id={section.id} key={section.id}>
              <div className="menu-section__header-editorial">
                <p className="section__eyebrow">{section.title}</p>
                <h3>{section.title}</h3>
                {section.description && <p className="menu-item-editorial__description" style={{ marginTop: '4px' }}>{section.description}</p>}
              </div>

              <div className="menu-list-editorial">
                {section.items.map((item) => (
                  <MenuItemCard key={`${section.id}-${item.name}`} item={item} />
                ))}
              </div>

              {/* {sectionImages[section.id] && (
                <img 
                  src={sectionImages[section.id]} 
                  alt={section.title} 
                  className="menu-thumbnail" 
                  loading="lazy"
                />
              )} */}
            </section>
          ))}
        </div>
      </section>

      <div className="experience-wrapper">
        <section className="section section--experience" id="esperienza">
          <div className="experience-panel">
            <p className="section__eyebrow">Esperienza</p>
            <h1>Piazza San Domenico, Nardò: <br />calici e serate per tutti.</h1>

            <p>
              Nel cuore barocco di Piazza San Domenico, tra pietra leccese e palazzi che raccontano secoli
              di incontri, L&apos;Angolo di<span className="brand-v">V</span>ino accoglie ogni identità con rispetto, musica e
              serate a tema, con tavoli pensati per sentirsi liberi di restare.
            </p>
          </div>
          <div className="hours-panel" id="contatti">
            <p className="section__eyebrow">Contatti</p>
            <h1>Tutti i giorni, 7 su 7</h1>
            <p>18:00 - 01:00</p>
            <a href="mailto:ilbistrodeidotti19@gmail.com">ilbistrodeidotti19@gmail.com</a>
          </div>
        </section>
      </div>
    </main>
  );
}

export default App;
