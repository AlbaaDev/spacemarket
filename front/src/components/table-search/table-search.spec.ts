import { matchesSearch, normalizeForSearch } from './table-search';

describe('Search', () => {
  it('ignores case and accents', () => {
    expect(normalizeForSearch('Éric DUPONT')).toBe('eric dupont');
    expect(matchesSearch(['Éric', 'Dupont'], 'eric')).toBeTrue();
  });

  it('matches part of a word', () => {
    expect(matchesSearch(['Dupont'], 'dup')).toBeTrue();
    expect(matchesSearch(['Dupont'], 'pon')).toBeTrue();
  });

  it('keeps a row only when every word appears, in any Column', () => {
    expect(matchesSearch(['Jean', 'Dupont', 'Lyon'], 'jean lyon')).toBeTrue();
    expect(matchesSearch(['Jean', 'Dupont', 'Paris'], 'jean lyon')).toBeFalse();
  });

  it('does not join two cells into one word', () => {
    expect(matchesSearch(['Jean', 'Dupont'], 'jeandupont')).toBeFalse();
  });

  it('keeps every row when the Search is blank', () => {
    expect(matchesSearch(['Jean'], '')).toBeTrue();
    expect(matchesSearch(['Jean'], '   ')).toBeTrue();
  });
});
