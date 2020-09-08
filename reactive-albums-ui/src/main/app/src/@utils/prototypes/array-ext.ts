/* eslint-disable */
///<reference path="array.d.ts"/>

export default function () {
  Object.defineProperty(Array.prototype, 'replace', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T>(this: Array<T>, index: number, replacement: T): Array<T> {
      const n = this.slice();
      n[index] = replacement;
      return n;
    },
  });

  Object.defineProperty(Array.prototype, 'withinBounds', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T>(
      this: Array<T>,
      index: number,
      startExclusive: boolean = false,
      endExclusive: boolean = false
    ): boolean {
      const innerBound = startExclusive ? 1 : 0;
      const outerBound = Math.max(endExclusive ? this.length - 2 : this.length - 1, 0);

      return index >= innerBound && index <= outerBound;
    },
  });

  Object.defineProperty(Array.prototype, 'isEmpty', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T>(this: Array<T>): boolean {
      return this.length === 0;
    },
  });

  Object.defineProperty(Array.prototype, 'filterNotNull', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T>(this: Array<T>): T[] {
      return this.filter((t) => !!t);
    },
  });
}
