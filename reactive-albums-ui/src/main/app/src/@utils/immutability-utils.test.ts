import { boundary, isEmpty, isInBoundary, merge, remove, replace, update } from './immutability-utils';

interface TestObj {
  key1?: number;
  key2?: number;
  key3?: number;
}

describe('immutability-utils', () => {
  describe('#isEmpty', () => {
    it('should support countable objects', () => {
      expect(isEmpty([])).toBeTruthy();
      expect(isEmpty([1])).toBeFalsy();

      expect(isEmpty('')).toBeTruthy();
      expect(isEmpty('Not empty')).toBeFalsy();
    });
  });

  describe('#merge', () => {
    it('should merge partial objects', () => {
      const target: TestObj = { key1: 1, key2: 2 };
      const source1: TestObj = { key2: 3 };
      const source2: TestObj = { key3: 4 };

      expect(merge(target, source1, source2)).toEqual({ key1: 1, key2: 3, key3: 4 });
    });

    it('should support null/undefined input as source', () => {
      const target = undefined;
      const source1 = { key1: 1 };
      const source2 = undefined;

      expect(merge(target, source1, source2)).toEqual({ key1: 1 });
    });
  });

  describe('#update', () => {
    it('should update specific object keys', () => {
      const target: TestObj = {
        key1: 1,
      };

      expect(update(target, 'key2', 5)).toEqual({ key1: 1, key2: 5 });
    });

    it('should support undefined input as target', () => {
      const target: TestObj | undefined = undefined;

      expect(update<TestObj>(target, 'key2', 6)).toEqual({ key2: 6 });
    });
  });

  describe('#remove', () => {
    it('should remove object properties', () => {
      const target: TestObj = { key1: 1, key2: 2, key3: 3 };

      expect(remove(target, 'key1')).toEqual({ key2: 2, key3: 3 });
    });
  });

  describe('#replace', () => {
    it('should replace array items at specific indices', () => {
      const arr = [1, 2, 3];
      expect(replace(arr, 1, 8)).toEqual([1, 8, 3]);
    });

    it('should handle undefined input array', () => {
      expect(replace<number>(undefined, 1, 8)).toEqual([8]);
    });
  });

  describe('#boundary', () => {
    it('should determine the right outer boundary index of an array', () => {
      expect(boundary([0, 1, 2])).toBe(3);
      expect(boundary([0, 1, 2], true)).toBe(2);
      expect(boundary([])).toBe(0);
      expect(boundary([], true)).toBe(-1);
      expect(boundary(undefined)).toBe(0);
      expect(boundary(undefined, true)).toBe(-1);
    });
  });

  describe('#isInBoundary', () => {
    it('should determine if the given index is within the array boundaries', () => {
      expect(isInBoundary([0, 1, 2], 2)).toBeTruthy();
      expect(isInBoundary([0, 1, 2], 2, true, true)).toBeFalsy();
      expect(isInBoundary([0, 1, 2], 0)).toBeTruthy();
      expect(isInBoundary([0, 1, 2], 0, true, true)).toBeFalsy();
      expect(isInBoundary([0, 1, 2], 1, true, true)).toBeTruthy();
    });
  });
});
