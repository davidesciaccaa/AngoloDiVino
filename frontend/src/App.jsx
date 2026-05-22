import { useEffect, useState } from 'react';
import { fetchApiStatus, fetchSignatureCocktails } from './api/barApi.js';
import { CocktailCard } from './components/CocktailCard.jsx';
import { Hero } from './components/Hero.jsx';

const fallbackCocktails = [
  {
    name: 'Rubino Sour',
    subtitle: 'Vino rosso, agrumi',
    description: 'Un sour vellutato con vino rosso ridotto, limone fresco e albume.',
    ingredients: ['Vino rosso', 'Limone', 'Sciroppo speziato'],
    price: '12 EUR'
  },
  {
    name: 'Spritz del Vicolo',
    subtitle: 'Bitter, bollicine',
    description: 'Aperitivo verticale, asciutto, con erbe amare e prosecco extra dry.',
    ingredients: ['Bitter italiano', 'Prosecco', 'Soda'],
    price: '10 EUR'
  },
  {
    name: 'Notturno Bianco',
    subtitle: 'Gin, uva bianca',
    description: "Gin floreale, mosto d'uva bianca e una chiusura fresca di salvia.",
    ingredients: ['Gin', "Mosto d'uva", 'Salvia'],
    price: '13 EUR'
  }
];

function App() {
  const [apiStatus, setApiStatus] = useState({ status: 'LOADING' });
  const [cocktails, setCocktails] = useState(fallbackCocktails);
  const [isUsingFallback, setIsUsingFallback] = useState(false);

  useEffect(() => {
    let isMounted = true;

    async function loadHomeData() {
      try {
        const [status, signatureCocktails] = await Promise.all([
          fetchApiStatus(),
          fetchSignatureCocktails()
        ]);

        if (!isMounted) {
          return;
        }

        setApiStatus(status);
        setCocktails(signatureCocktails);
        setIsUsingFallback(false);
      } catch {
        if (!isMounted) {
          return;
        }

        setApiStatus({ status: 'DOWN' });
        setCocktails(fallbackCocktails);
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

      <section className="section section--menu" id="signature">
        <div className="section__heading">
          <p className="section__eyebrow">Carta signature</p>
          <h2>Drink con carattere da enoteca e precisione da cocktail bar.</h2>
          {isUsingFallback && (
            <p className="section__note">Backend non raggiungibile: stai vedendo la carta locale.</p>
          )}
        </div>

        <div className="cocktail-grid">
          {cocktails.map((cocktail) => (
            <CocktailCard key={cocktail.name} cocktail={cocktail} />
          ))}
        </div>
      </section>

      <section className="section section--experience" id="esperienza">
        <div className="experience-panel">
          <p className="section__eyebrow">Esperienza</p>
          <h2>Un banco intimo, una cantina viva, una lista corta e curata.</h2>
          <p>
            Ogni sera alterna twist sui classici, mescite al calice e pairing leggeri: pane caldo,
            conserve, formaggi e note mediterranee.
          </p>
        </div>
        <div className="hours-panel" id="contatti">
          <p className="section__eyebrow">Contatti</p>
          <h2>Mercoledi - Domenica</h2>
          <p>18:00 - 01:00</p>
          <a href="mailto:prenotazioni@angolodivino.local">prenotazioni@angolodivino.local</a>
        </div>
      </section>
    </main>
  );
}

export default App;
