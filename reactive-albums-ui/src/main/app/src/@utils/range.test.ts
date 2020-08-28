import { range } from './range';

describe('range.ts', () => {
  describe('#range', () => {
    it('should generate numeric range generators', () => {
      expect(Array.from(range(0, 5))).toEqual([0, 1, 2, 3, 4]);
      expect(Array.from(range(5, 10))).toEqual([5, 6, 7, 8, 9]);
    });
  });
});
