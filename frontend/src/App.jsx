import { useEffect, useState } from 'react';
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
        name: 'Tagliere Salandra',
        subtitle: 'Formaggi, conserve',
        description: 'Selezione di formaggi locali, olive, focaccia calda e confettura della casa.',
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
    title: 'Drink',
    description: 'Semplice e diretto: Drink.',
    items: [
      {
        name: 'Rubino Sour',
        subtitle: 'Vino rosso, agrumi',
        description: 'Un sour vellutato con vino rosso ridotto, limone fresco e albume.',
        notes: ['Signature', 'Agrumato'],
        price: '12 EUR'
      },
      {
        name: 'Notturno Bianco',
        subtitle: 'Gin, uva bianca',
        description: "Gin floreale, mosto d'uva bianca e una chiusura fresca di salvia.",
        notes: ['Floreale', 'Fresco'],
        price: '13 EUR'
      }
    ]
  },
  {
    id: 'vini',
    title: 'Vini',
    description: 'Etichette salentine e vini locali aperti da scoprire al calice.',
    items: [
      {
        name: 'Primitivo del Cortile',
        subtitle: 'Rosso, Salento',
        description: 'Calice morbido e speziato, ideale con assaggi sapidi e formaggi stagionati.',
        notes: ['Calice', 'Corposo'],
        price: '7 EUR'
      },
      {
        name: 'Bianco di Pietra',
        subtitle: 'Bianco, Nardò',
        description: 'Bianco minerale, teso e luminoso, con finale di mandorla fresca.',
        notes: ['Calice', 'Minerale'],
        price: '6 EUR'
      }
    ]
  },
  {
    id: 'superalcolici',
    title: 'Superalcolici',
    description: 'Distillati selezionati per degustazioni.',
    items: [
      {
        name: 'Amaro dei Dotti',
        subtitle: 'Erbe, radici',
        description: "Amaro intenso con note balsamiche, scorza d'arancia e finale persistente.",
        notes: ['Dopocena', 'Servito freddo'],
        price: '6 EUR'
      },
      {
        name: 'Rum Riserva 8',
        subtitle: 'Morbido, speziato',
        description: 'Rum ambrato con vaniglia, cacao e legno dolce.',
        notes: ['Degustazione', 'Liscio'],
        price: '9 EUR'
      }
    ]
  },
  {
    id: 'bevande',
    title: 'Bevande',
    description: 'Analcolici, soft drink e alternative leggere per ogni momento.',
    items: [
      {
        name: 'Limonata',
        subtitle: 'Agrumi, erbe',
        description: 'Limonata fresca, zest di limone.',
        notes: ['Analcolico', 'Rinfrescante'],
        price: '5 EUR'
      },
      {
        name: 'Tonica',
        subtitle: 'Erbe, agrumi',
        description: 'Tonica secca con rosmarino, pompelmo rosa e ghiaccio pieno.',
        notes: ['Analcolico', 'Dry'],
        price: '5 EUR'
      }
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

      <section className="section section--menu" aria-labelledby="menu-title">
        <div className="section__heading">
          <p className="section__eyebrow">Menu</p>
          <h2 id="menu-title">Aperitivo, drink, vini e bottiglie per ogni ritmo della serata.</h2>
          {isUsingFallback && (
            <p className="section__note">Backend non raggiungibile: stai vedendo il menu locale.</p>
          )}
        </div>

        <div className="menu-section-list">
          {menuSections.map((section) => (
            <section className="menu-section" id={section.id} key={section.id}>
              <div className="menu-section__heading">
                <p className="section__eyebrow">{section.title}</p>
                <h3>{section.description}</h3>
              </div>
              <div className="menu-grid">
                {section.items.map((item) => (
                  <MenuItemCard key={`${section.id}-${item.name}`} item={item} />
                ))}
              </div>
            </section>
          ))}
        </div>
      </section>

      <section className="section section--experience" id="esperienza">
        <div className="experience-panel">
          <p className="section__eyebrow">Esperienza</p>
          <h1>Piazza San Domenico, Nardò: <br />calici e serate per tutti.</h1>

          <p>
            Nel cuore barocco di Piazza San Domenico, tra pietra leccese e palazzi che raccontano secoli
            di incontri, il locale accoglie ogni identità con rispetto, musica e
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
    </main>
  );
}

export default App;
