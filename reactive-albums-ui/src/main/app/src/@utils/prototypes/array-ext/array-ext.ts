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

  Object.defineProperty(Array.prototype, 'isNotEmpty', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T>(this: Array<T>): boolean {
      return this.length !== 0;
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

  Object.defineProperty(Array.prototype, 'partition', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function <T, K extends number | string | symbol>(this: Array<T>, partitionBy: (t: T) => K): Record<K, T[]> {
      return this.reduce((acc, next) => {
        const key: K = partitionBy(next);
        acc[key] = acc[key] ? [...acc[key], next] : [next];
        return acc;
      }, {} as Record<K, T[]>);
    },
  });
}
