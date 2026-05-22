export function CocktailCard({ cocktail }) {
  return (
    <article className="cocktail-card">
      <div className="cocktail-card__topline">
        <p>{cocktail.subtitle}</p>
        <strong>{cocktail.price}</strong>
      </div>
      <h3>{cocktail.name}</h3>
      <p className="cocktail-card__description">{cocktail.description}</p>
      <ul className="ingredient-list" aria-label={`Ingredienti di ${cocktail.name}`}>
        {cocktail.ingredients.map((ingredient) => (
          <li key={ingredient}>{ingredient}</li>
        ))}
      </ul>
    </article>
  );
}
