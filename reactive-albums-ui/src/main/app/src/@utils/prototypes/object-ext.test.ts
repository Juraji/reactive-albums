import install from './object-ext';

install();

describe('Object', () => {
  describe('copy', () => {
    it('should create shallow copies of objects', () => {
      const obj = { my: 'Object', is: 'Awesome' };

      expect(obj.copy()).not.toBe(obj);
      expect(obj.copy()).toEqual(obj);
    });

    it('should create shallow copies of arrays', () => {
      const arr = [1, 2, 3];

      expect(arr.copy()).not.toBe(arr);
      expect(arr.copy()).toEqual(arr);
    });

    it('should apply the given partial as update during copy', () => {
      const obj = { my: 'Object', is: 'Awesome' };
      const update = { my: 'Updated object' };
      const result = { my: 'Updated object', is: 'Awesome' };

      expect(obj.copy(update)).toEqual(result);
    });

    it('should throw error when applying updates to arrays', () => {
      // The interface doesn't allow the update parameter if T is an array.
      // But T might not always be statically analyzable, so use a hack.
      expect(() => [1, 2, 3].copy([1, 2] as any)).toThrow();
    });
  });
});
