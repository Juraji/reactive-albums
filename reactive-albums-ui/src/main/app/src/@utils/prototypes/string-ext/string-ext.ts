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
}
