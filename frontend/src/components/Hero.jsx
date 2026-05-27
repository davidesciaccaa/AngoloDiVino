import heroImage from '../assets/lounge-still-life.jpeg';
import { StatusBadge } from './StatusBadge.jsx';

export function Hero({ status }) {
  return (
    <section className="hero" style={{ '--hero-image': `url(${heroImage})` }}>
      <header className="site-header" aria-label="Intestazione principale">
        <a className="brand" href="#top" aria-label="L'Angolo diVino">
          L&apos;Angolo di<span className="brand-v">V</span>ino
        </a>
        <nav className="site-nav" aria-label="Navigazione principale">
          <a href="#aperitivo">Aperitivo</a>
          <a href="#drink">Drink</a>
          <a href="#vini">Vini</a>
          <a href="#frullati">Frullati</a>
          <a href="#superalcolici">Superalcolici</a>
          <a href="#bevande">Bevande</a>
          <a href="#contatti">Contatti</a>
        </nav>
      </header>

      <div className="hero__content" id="top">
        <StatusBadge status={status} />
        <p className="hero__eyebrow">Cocktail bar e vini selezionati</p>
        <h1>L&apos;Angolo di<span className="brand-v">V</span>ino</h1>
        <p className="hero__copy">
          Drink avvolgenti, calici curati e piccoli assaggi pensati per serate che iniziano senza fretta.
        </p>
        <div className="hero__actions" aria-label="Azioni principali">
          <a className="button button--primary" href="#aperitivo">
            Scopri il menu
          </a>
          {/* <a className="button button--ghost" href="#contatti">
            Prenota un tavolo
          </a> */}
        </div>
      </div>  
    </section>
  );
}
