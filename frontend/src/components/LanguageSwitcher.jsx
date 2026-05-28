import { useTranslation } from 'react-i18next';

const languages = [
  { code: 'it', label: 'ITA', flag: '🇮🇹' },
  { code: 'en', label: 'ENG', flag: '🇬🇧' },
  { code: 'de', label: 'DEU', flag: '🇩🇪' }
];

export function LanguageSwitcher() {
  const { i18n } = useTranslation();

  return (
    <div className="language-switcher">
      {languages.map((lang) => (
        <button
          key={lang.code}
          onClick={() => i18n.changeLanguage(lang.code)}
          className={`lang-button ${i18n.language === lang.code ? 'active' : ''}`}
          aria-label={`Switch to ${lang.label}`}
        >
          <span className="lang-flag">{lang.flag}</span>
          <span className="lang-label">{lang.label}</span>
        </button>
      ))}
    </div>
  );
}
