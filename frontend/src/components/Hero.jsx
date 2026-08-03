import { useTranslation } from 'react-i18next';
import { useRef, useState, useEffect, useCallback } from 'react';
import heroImage from '../assets/lounge-still-life.jpeg';
import { LanguageSwitcher } from './LanguageSwitcher.jsx';

export function Hero() {
  const { t, i18n } = useTranslation();

  // Scroll Indicators Logic for Hero Nav
  const navRef = useRef(null);
  const [showLeftIndicator, setShowLeftIndicator] = useState(false);
  const [showRightIndicator, setShowRightIndicator] = useState(false);

  const scrollNav = (direction) => {
    const nav = navRef.current;

    if (nav) {
      const scrollAmount = Math.max(120, nav.clientWidth * 0.75);

      nav.scrollBy({
        left: direction === 'left' ? -scrollAmount : scrollAmount,
        behavior: 'smooth',
      });
    }
  };

  const checkScroll = useCallback(() => {
    if (navRef.current) {
      const { scrollLeft, scrollWidth, clientWidth } = navRef.current;
      const maxScrollLeft = scrollWidth - clientWidth;

      setShowLeftIndicator(scrollLeft > 1);
      setShowRightIndicator(maxScrollLeft > 1 && scrollLeft < maxScrollLeft - 1);
    }
  }, []);

  useEffect(() => {
    const nav = navRef.current;
    if (nav) {
      checkScroll();
      nav.addEventListener('scroll', checkScroll, { passive: true });
      window.addEventListener('resize', checkScroll);

      const frame = window.requestAnimationFrame(checkScroll);
      const timeout = setTimeout(checkScroll, 500);
      const resizeObserver = typeof ResizeObserver === 'undefined'
        ? null
        : new ResizeObserver(checkScroll);
      resizeObserver?.observe(nav);

      return () => {
        nav.removeEventListener('scroll', checkScroll);
        window.removeEventListener('resize', checkScroll);
        window.cancelAnimationFrame(frame);
        clearTimeout(timeout);
        resizeObserver?.disconnect();
      };
    }
  }, [checkScroll, i18n.resolvedLanguage]);

  return (
    <section className="hero" style={{ '--hero-image': `url(${heroImage})` }}>
      <header className="site-header" aria-label="Intestazione principale">
        <a className="brand" href="#top" aria-label="L'Angolo diVino">
          L&apos;Angolo di<span className="brand-v">V</span>ino
        </a>
        <div className="header-actions">
          <div className={`site-nav-wrapper ${showLeftIndicator ? 'has-left-scroll' : ''} ${showRightIndicator ? 'has-right-scroll' : ''}`}>
            {showLeftIndicator && (
              <button
                type="button"
                className="nav-scroll-hint nav-scroll-hint--left"
                onClick={() => scrollNav('left')}
                aria-label="Scorri la navigazione verso sinistra"
              >
                <span className="nav-arrow" aria-hidden="true">‹</span>
              </button>
            )}
            <nav className="site-nav" aria-label="Navigazione principale" ref={navRef}>
              <a href="#aperitivo">{t('nav.aperitivo')}</a>
              <a href="#drink">{t('nav.drink')}</a>
              <a href="#vini">{t('nav.vini')}</a>
              <a href="#amari">{t('nav.amari')}</a>
              <a href="#superalcolici">{t('nav.superalcolici')}</a>
              <a href="#bevande">{t('nav.bevande')}</a>
              <a href="#contatti">{t('nav.contatti')}</a>
            </nav>
            {showRightIndicator && (
              <button
                type="button"
                className="nav-scroll-hint nav-scroll-hint--right"
                onClick={() => scrollNav('right')}
                aria-label="Scorri la navigazione verso destra"
              >
                <span className="nav-arrow" aria-hidden="true">›</span>
              </button>
            )}
          </div>
          <LanguageSwitcher />
        </div>
      </header>


      <div className="hero__content" id="top">
        {/* <StatusBadge status={status} /> */}
        <p className="hero__eyebrow">{t('hero.eyebrow')}</p>
        <h1 className="hero__title">L&apos;Angolo di<span className="brand-v">V</span>ino</h1>
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

      <a href="#menu" className="hero__scroll-down" aria-label={t('hero.scroll_down', { defaultValue: 'Scorri per scoprire il menu' })}>
        <div className="mouse-icon">
          <div className="wheel"></div>
        </div>
        <div className="scroll-arrow"></div>
      </a>
    </section>
  );
}
