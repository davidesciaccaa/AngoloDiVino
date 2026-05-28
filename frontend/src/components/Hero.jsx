import { useTranslation } from 'react-i18next';
import heroImage from '../assets/lounge-still-life.jpeg';
import { StatusBadge } from './StatusBadge.jsx';
import { LanguageSwitcher } from './LanguageSwitcher.jsx';

export function Hero({ status }) {
  const { t } = useTranslation();

  return (
    <section className="hero" style={{ '--hero-image': `url(${heroImage})` }}>
      <header className="site-header" aria-label="Intestazione principale">
        <a className="brand" href="#top" aria-label="L'Angolo diVino">
          L&apos;Angolo di<span className="brand-v">V</span>ino
        </a>
        <div className="header-actions">
          <nav className="site-nav" aria-label="Navigazione principale">
            <a href="#aperitivo">{t('nav.aperitivo')}</a>
            <a href="#drink">{t('nav.drink')}</a>
            <a href="#vini">{t('nav.vini')}</a>
            <a href="#superalcolici">{t('nav.superalcolici')}</a>
            <a href="#frullati">{t('nav.frullati')}</a>
            <a href="#bevande">{t('nav.bevande')}</a>
            <a href="#contatti">{t('nav.contatti')}</a>
          </nav>
          <LanguageSwitcher />
        </div>
      </header>

      <div className="hero__content" id="top">
        <StatusBadge status={status} />
        <p className="hero__eyebrow">{t('hero.eyebrow')}</p>
        <h1>L&apos;Angolo di<span className="brand-v">V</span>ino</h1>
        <p className="hero__copy">
          {t('hero.copy')}
        </p>
        <div className="hero__actions" aria-label="Azioni principali">
          <a className="button button--primary" href="#aperitivo">
            {t('hero.button')}
          </a>
          {/* <a className="button button--ghost" href="#contatti">
            Prenota un tavolo
          </a> */}
        </div>
      </div>  
    </section>
  );
}
