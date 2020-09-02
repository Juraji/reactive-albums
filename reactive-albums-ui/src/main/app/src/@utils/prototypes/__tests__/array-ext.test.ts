import install from '../array-ext';

install();

describe('Array', () => {
  describe('replace', () => {
    it('should replace items', () => {
      expect([0, 1, 5].replace(1, 3)).toEqual([0, 3, 5]);
    });
  });

  describe('withinBounds', () => {
    it('should detect if index is within array bounds', () => {
      expect([0, 1, 2, 3, 4].withinBounds(3)).toBeTruthy();
      expect([0, 1, 2, 3, 4].withinBounds(0)).toBeTruthy();
      expect([0, 1, 2, 3, 4].withinBounds(4)).toBeTruthy();
      expect([0, 1, 2, 3, 4].withinBounds(9)).toBeFalsy();
    });

    it('should support start exclusivity', () => {
      expect([0, 1, 2, 3, 4].withinBounds(3, true)).toBeTruthy();
      expect([0, 1, 2, 3, 4].withinBounds(0, true)).toBeFalsy();
      expect([0, 1, 2, 3, 4].withinBounds(4, true)).toBeTruthy();
      expect([0, 1, 2, 3, 4].withinBounds(9, true)).toBeFalsy();
    });

    it('should support end exclusivity', () => {
      expect([0, 1, 2, 3, 4].withinBounds(3, false, true)).toBeTruthy();
      expect([0, 1, 2, 3, 4].withinBounds(0, false, true)).toBeTruthy();
      expect([0, 1, 2, 3, 4].withinBounds(4, false, true)).toBeFalsy();
      expect([0, 1, 2, 3, 4].withinBounds(9, false, true)).toBeFalsy();
    });
  });

  describe('isEmpty', () => {
    it('should correctly report empty-ness', () => {
      expect([].isEmpty()).toBeTruthy();
      expect([1, 2, 3].isEmpty()).toBeFalsy();
    });
  });
});
