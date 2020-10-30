/* eslint-disable */
///<reference path="string.d.ts"/>

export default function () {
  Object.defineProperty(String.prototype, 'isBlank', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function (this: string): boolean {
      return this.trim().length === 0;
    },
  });

  Object.defineProperty(String.prototype, 'trimChar', {
    configurable: false,
    enumerable: false,
    writable: false,
    value: function (this: string, char: string): string {
      const arr = Array.from(this);
      const first = arr.findIndex((c) => c !== char);
      const last = arr.reverse().findIndex((c) => c !== char);
      return first === -1 && last === -1 ? '' : this.substring(first, this.length - last);
    },
  });
}
